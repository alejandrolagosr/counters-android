package com.flagos.cscounters.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.flagos.cscounters.main.mapper.CounterItemUiMapper
import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.data.repository.CountersRepository
import com.flagos.data.utils.Resource
import kotlinx.coroutines.Dispatchers

private const val NO_COUNTERS = 0

class CountersViewModel(
    private val countersRepository: CountersRepository,
    private val counterItemUiMapper: CounterItemUiMapper = CounterItemUiMapper()
) : ViewModel() {

    private var _onCountersInfoRetrieved = MutableLiveData<Pair<Int, Int>>()
    val onCounterInfoRetrieved: LiveData<Pair<Int, Int>>
        get() = _onCountersInfoRetrieved

    private var _onNoCountersAdded = MutableLiveData<Unit>()
    val onNoCountersAdded: LiveData<Unit>
        get() = _onNoCountersAdded

    fun fetchCounters() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            val counterItemsList = counterItemUiMapper.toCounterUiItemList(countersRepository.retrieveAll())
            getCountersInfo(counterItemsList)
            emit(Resource.success(data = counterItemsList))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message.orEmpty()))
        }
    }

    private fun getCountersInfo(items: List<CounterUiItem>) {
        val times = items.sumBy { it.count!! }
        val itemCount = items.size
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
}