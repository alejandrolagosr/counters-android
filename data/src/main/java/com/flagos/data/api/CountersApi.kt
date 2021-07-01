package com.flagos.data.api

import com.flagos.data.model.CounterItem
import retrofit2.http.*

interface CountersApi {

    @GET("/api/v1/counters")
    suspend fun retrieveAll(): List<CounterItem>

    @POST("/api/v1/counter")
    suspend fun save(@Body counterItem: CounterItem): List<CounterItem>

    @POST("/api/v1/counter/inc")
    suspend fun increment(@Body counterItem: CounterItem): List<CounterItem>

    @POST("/api/v1/counter/dec")
    suspend fun decrement(@Body counterItem: CounterItem): List<CounterItem>

    @HTTP(method = "DELETE", path = "/api/v1/counter", hasBody = true)
    suspend fun delete(@Body counterItem: CounterItem): List<CounterItem>
}
