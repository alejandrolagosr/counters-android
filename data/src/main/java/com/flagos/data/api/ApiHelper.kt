package com.flagos.data.api

import com.flagos.data.model.CounterItem

class ApiHelper(private val countersApi: CountersApi) {

    suspend fun retrieveAll() = countersApi.retrieveAll()

    suspend fun save(counterItem: CounterItem) = countersApi.save(counterItem)

    suspend fun increment(counterItem: CounterItem) = countersApi.increment(counterItem)

    suspend fun decrement(counterItem: CounterItem) = countersApi.decrement(counterItem)

    suspend fun delete(counterItem: CounterItem) = countersApi.delete(counterItem)
}