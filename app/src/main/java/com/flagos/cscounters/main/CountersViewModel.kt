package com.flagos.cscounters.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.flagos.cscounters.helpers.NetworkHelper
import com.flagos.cscounters.main.mapper.CounterItemUiMapper
import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.cscounters.main.CounterActionType.DECREMENT
import com.flagos.cscounters.main.CounterActionType.INCREMENT
import com.flagos.data.model.CounterItem
import com.flagos.data.repository.CountersRepository
import com.flagos.data.utils.Resource
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

    private var _onNoCountersAdded = MutableLiveData<Unit>()
    val onNoCountersAdded: LiveData<Unit>
        get() = _onNoCountersAdded

    private var _onShowNoInternetDialog = MutableLiveData<Pair<CounterUiItem, CounterActionType>>()
    val onShowNoInternetDialog: LiveData<Pair<CounterUiItem, CounterActionType>>
        get() = _onShowNoInternetDialog

    fun fetchCounters() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            currentCountersList = counterItemUiMapper.toCounterUiItemList(countersRepository.retrieveAll())
            getCountersInfo()
            emit(Resource.success(data = currentCountersList))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message.orEmpty()))
        }
    }

    private fun getCountersInfo() {
        val times = currentCountersList.sumBy { it.count!! }
        val itemCount = currentCountersList.size
        if (itemCount > NO_COUNTERS) {
            _onCountersInfoRetrieved.postValue(Pair(itemCount, times))
        } else {
            _onNoCountersAdded.postValue(Unit)
        }
    }

    fun clearInfo() {
        _onCountersInfoRetrieved.postValue(null)
        _onNoCountersAdded.postValue(null)
    }

    fun incrementCounter(counterId: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                countersRepository.increment(CounterItem(id = counterId))
            }
        } else {
            sendNoInternetDialog(counterId, INCREMENT)
        }
    }

    fun decrementCounter(counterId: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch(Dispatchers.IO) {
                countersRepository.decrement(CounterItem(id = counterId))
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
            _onShowNoInternetDialog.value = Pair(counterItem, actionType)
        }
    }

    fun retryAction(counterId: String?, actionType: CounterActionType) {
        counterId?.let { if (actionType == INCREMENT) incrementCounter(counterId) else decrementCounter(counterId) }
    }
}