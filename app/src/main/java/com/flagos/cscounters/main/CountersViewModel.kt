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
import com.flagos.data.model.CounterItem
import com.flagos.data.repository.CountersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val NO_COUNTERS = 0

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

    init {
        fetchCounters()
    }

    fun fetchCounters() {
        viewModelScope.launch(Dispatchers.IO) {
            _onCountersStateChanged.postValue(CountersState.OnLoading)
            try {
                currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.retrieveAll())
                getCountersInfo()
            } catch (exception: Exception) {
                _onCountersStateChanged.postValue(CountersState.OnError(exception.message.orEmpty()))
            }
        }
    }

    private fun getCountersInfo() {
        val times = currentCountersList.sumBy { it.count }
        val itemCount = currentCountersList.size
        if (itemCount > NO_COUNTERS) {
            _onCountersStateChanged.postValue(CountersState.OnSuccess(currentCountersList))
            _onCountersInfoRetrieved.postValue(Pair(itemCount, times))
        } else {
            _onCountersStateChanged.postValue(CountersState.OnNoContent)
        }
    }

    fun clearInfo() {
        _onCountersInfoRetrieved.postValue(null)
        _onCountersStateChanged.postValue(null)
    }

    fun incrementCounter(counterId: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                _onCountersStateChanged.postValue(CountersState.OnLoading)
                try {
                    currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.increment(CounterItem(id = counterId)))
                    getCountersInfo()
                } catch (exception: Exception) {
                    _onCountersStateChanged.postValue(CountersState.OnError(exception.message.orEmpty()))
                }
            }
        } else {
            sendNoInternetDialog(counterId, INCREMENT)
        }
    }

    fun decrementCounter(counterId: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                _onCountersStateChanged.postValue(CountersState.OnLoading)
                try {
                    currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.decrement(CounterItem(id = counterId)))
                    getCountersInfo()
                } catch (exception: Exception) {
                    _onCountersStateChanged.postValue(CountersState.OnError(exception.message.orEmpty()))
                }
            }
        } else {
            sendNoInternetDialog(counterId, DECREMENT)
        }
    }

    private fun getCounterItemById(counterId: String): CounterUiItem? {
        return currentCountersList.find { counterId == it.id }
    }

    private fun sendNoInternetDialog(counterId: String, actionType: CounterActionType) {
        getCounterItemById(counterId)?.let { counterItem ->
            _onCountersStateChanged.value = CountersState.OnNoInternet(Pair(counterItem, actionType))
        }
    }

    fun retryAction(counterId: String?, actionType: CounterActionType) {
        counterId?.let { if (actionType == INCREMENT) incrementCounter(counterId) else decrementCounter(counterId) }
    }

    sealed class CountersState {
        object OnLoading : CountersState()
        object OnNoContent : CountersState()
        data class OnNoInternet(val counterInfo: Pair<CounterUiItem, CounterActionType>) : CountersState()
        data class OnError(val message: String) : CountersState()
        data class OnSuccess(val countersList: List<CounterUiItem>) : CountersState()
    }
}