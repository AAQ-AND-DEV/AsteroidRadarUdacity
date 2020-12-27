package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface AsteroidService{
    @GET("feed")
    fun getAsteroids(@Query("START_DATE") start: String,
                     @Query("END_DATE") end: String,
                     @Query("API_KEY") apiKey: String) : Deferred<ResponseBody>


//            https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY
}

object AsteroidNetwork{
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/neo/rest/v1/")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val asteroidService = retrofit.create(AsteroidService::class.java)
}