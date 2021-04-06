package com.flagos.data.repository

import com.flagos.data.api.ApiHelper
import com.flagos.data.model.CounterItem

class CountersRepository(private val apiHelper: ApiHelper) {

    suspend fun retrieveAll() = apiHelper.retrieveAll()

    suspend fun save(counterItem: CounterItem) = apiHelper.save(counterItem)

    suspend fun increment(counterItem: CounterItem) = apiHelper.increment(counterItem)

    suspend fun decrement(counterItem: CounterItem) = apiHelper.decrement(counterItem)

    suspend fun delete(counterItem: CounterItem) = apiHelper.delete(counterItem)
}
