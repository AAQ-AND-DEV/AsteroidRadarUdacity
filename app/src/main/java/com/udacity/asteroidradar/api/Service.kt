package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query



interface AsteroidService{
    @GET("neo/rest/v1/feed")
    fun getAsteroids(@Query("START_DATE") start: String,
                     @Query("END_DATE") end: String,
                     @Query("API_KEY") apiKey: String) : Deferred<List<Asteroid>>

    @GET("planetary/apod")
    fun getPod(@Query("api_key") apiKey: String) : Deferred<NetworkPod>

//            https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=API_KEY
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object AsteroidNetwork{

    private val interceptor = HttpLoggingInterceptor().apply{
        level= if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    val client = OkHttpClient().newBuilder()
        .addInterceptor(interceptor).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(client)
        .build()

    val asteroidService = retrofit.create(AsteroidService::class.java)
}