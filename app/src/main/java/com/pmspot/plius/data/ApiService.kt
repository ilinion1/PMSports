package com.pmspot.plius.data

import retrofit2.http.GET

interface ApiService {

    @GET("psport.json")
    suspend fun getDataServer(): Container
}