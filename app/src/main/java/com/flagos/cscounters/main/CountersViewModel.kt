package com.flagos.cscounters.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flagos.cscounters.helpers.NetworkHelper
import com.flagos.cscounters.main.mapper.CounterItemUiMapper
import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.cscounters.main.CounterActionType.DECREMENT
import com.flagos.cscounters.main.CounterActionType.INCREMENT
import com.flagos.cscounters.main.CounterActionType.SHARE
import com.flagos.cscounters.main.CounterActionType.DELETE
import com.flagos.cscounters.main.CounterActionType.FETCH
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnSuccess
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnGenericError
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnUpdateError
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnLoading
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnNoContent
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnDeleteError
import com.flagos.data.model.CounterItem
import com.flagos.data.repository.CountersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val NO_COUNTERS = 0
private const val INITIAL_DELAY = 500L
private const val ACTION_DELAY = 300L
private const val COMMA = ", "

class CountersViewModel(
    private val countersRepository: CountersRepository,
    private val networkHelper: NetworkHelper,
    private val counterItemUiMapper: CounterItemUiMapper = CounterItemUiMapper()
) : ViewModel() {

    private var currentCountersList = listOf<CounterUiItem>()

    private var _onCountersInfoRetrieved = MutableLiveData<Pair<Int, Int>>()
    val onCounterInfoRetrieved: LiveData<Pair<Int, Int>>
        get() = _onCountersInfoRetrieved

    private var _onCountersStateChanged = MutableLiveData<CountersState>()
    val onCountersStateChanged: LiveData<CountersState>
        get() = _onCountersStateChanged

    private var _onShareCounters = MutableLiveData<String>()
    val onShareCounters: LiveData<String>
        get() = _onShareCounters

    fun fetchCounters() {
        viewModelScope.launch(Dispatchers.IO) {
            _onCountersStateChanged.postValue(OnLoading)
            delay(INITIAL_DELAY)
            try {
                currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.retrieveAll())
                getCountersInfo()
            } catch (exception: Exception) {
                _onCountersStateChanged.postValue(OnGenericError(null, FETCH, exception.message.orEmpty()))
            }
        }
    }

    private fun getCountersInfo() {
        val times = currentCountersList.sumBy { it.count }
        val itemCount = currentCountersList.size
        if (itemCount > NO_COUNTERS) {
            _onCountersStateChanged.postValue(OnSuccess(currentCountersList))
            _onCountersInfoRetrieved.postValue(Pair(itemCount, times))
        } else {
            _onCountersStateChanged.postValue(OnNoContent)
        }
    }

    fun clearInfo() {
        _onCountersInfoRetrieved.postValue(null)
        _onCountersStateChanged.postValue(null)
    }

    fun incrementCounter(counterId: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    delay(ACTION_DELAY)
                    currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.increment(CounterItem(id = counterId)))
                    getCountersInfo()
                } catch (exception: Exception) {
                    _onCountersStateChanged.postValue(OnGenericError(counterId, INCREMENT, exception.message.orEmpty()))
                }
            }
        } else {
            sendCantUpdateCounterDialog(counterId, INCREMENT)
        }
    }

    fun decrementCounter(counterId: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    delay(ACTION_DELAY)
                    currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.decrement(CounterItem(id = counterId)))
                    getCountersInfo()
                } catch (exception: Exception) {
                    _onCountersStateChanged.postValue(OnGenericError(counterId, DECREMENT, exception.message.orEmpty()))
                }
            }
        } else {
            sendCantUpdateCounterDialog(counterId, DECREMENT)
        }
    }

    private fun getCounterItemById(counterId: String): CounterUiItem? {
        return currentCountersList.find { counterId == it.id }
    }

    private fun sendCantUpdateCounterDialog(counterId: String, actionType: CounterActionType) {
        getCounterItemById(counterId)?.let { counterItem ->
            _onCountersStateChanged.value = OnUpdateError(Pair(counterItem, actionType))
        }
    }

    fun performRetry(counterId: String?, actionType: CounterActionType) {
        counterId?.let {
            when(actionType) {
                INCREMENT -> incrementCounter(it)
                DECREMENT -> decrementCounter(it)
                DELETE -> { delete(listOf(getCounterItemById(counterId))) }
                else -> fetchCounters()
            }
        } ?: fetchCounters()
    }

    fun performSelectionAction(counterItems: List<CounterUiItem>, selectionActionType: CounterActionType) {
        if (selectionActionType == SHARE) share(counterItems) else delete(counterItems)
    }

    private fun share(counterItems: List<CounterUiItem>) {
        _onShareCounters.value = counterItems.joinToString(COMMA) { "${it.count} x ${it.title}" }
    }

    private fun delete(counterItems: List<CounterUiItem?>) {
        if (networkHelper.isConnected()) {
            if (counterItems.isNotEmpty()) {
                _onCountersStateChanged.value = OnLoading
                counterItems.forEach {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.delete(CounterItem(id = it?.id)))
                            getCountersInfo()
                        } catch (exception: Exception) {
                            _onCountersStateChanged.postValue(OnGenericError(it?.id, DELETE, exception.message.orEmpty()))
                        }
                    }
                }
            }
        } else {
            _onCountersStateChanged.value = OnDeleteError
        }
    }

    sealed class CountersState {
        object OnLoading : CountersState()
        object OnNoContent : CountersState()
        object OnDeleteError : CountersState()
        data class OnUpdateError(val counterInfo: Pair<CounterUiItem, CounterActionType>) : CountersState()
        data class OnGenericError(val counterId: String?, val actionType: CounterActionType, val message: String) : CountersState()
        data class OnSuccess(val countersList: List<CounterUiItem>) : CountersState()
    }
}
