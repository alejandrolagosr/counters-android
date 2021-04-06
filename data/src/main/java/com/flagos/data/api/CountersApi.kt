package com.flagos.data.api

import com.flagos.data.model.CounterItem
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Body


interface CountersApi {

    @GET("/api/v1/counters")
    suspend fun retrieveAll(): List<CounterItem>

    @POST("/api/v1/counter")
    suspend fun save(@Body counterItem: CounterItem): List<CounterItem>

    @POST("/api/v1/counter/inc")
    suspend fun increment(@Body counterItem: CounterItem): List<CounterItem>

    @POST("/api/v1/counter/dec")
    suspend fun decrement(@Body counterItem: CounterItem): List<CounterItem>

    @DELETE("/api/v1/counter")
    suspend fun delete(@Body counterItem: CounterItem): List<CounterItem>
}
