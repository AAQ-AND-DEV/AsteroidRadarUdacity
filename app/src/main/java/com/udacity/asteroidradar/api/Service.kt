package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION,
AnnotationTarget.PROPERTY_GETTER,
AnnotationTarget.PROPERTY_SETTER)
@Retention()
internal annotation class Scalar

@Target(AnnotationTarget.FUNCTION,
AnnotationTarget.PROPERTY_SETTER,
AnnotationTarget.PROPERTY_GETTER)
@Retention
internal annotation class Json

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface AsteroidService{
    @GET("neo/rest/v1/feed")
    @Scalar
    suspend fun getAsteroids(@Query("START_DATE") start: String,
                     @Query("API_KEY") apiKey: String) : String


    @GET("planetary/apod")
    @com.udacity.asteroidradar.api.Json
    suspend fun getPod(@Query("api_key") apiKey: String) : NetworkPod

}

object AsteroidNetwork{

    private val interceptor = HttpLoggingInterceptor().apply{
        level= if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient().newBuilder()
        .addInterceptor(interceptor)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .addConverterFactory(EnvelopingConverterFactory.create())
        .client(client)
        .build()

    val asteroidService = retrofit.create(AsteroidService::class.java)
}

//This class was attained from Muriel's comment on https://knowledge.udacity.com/questions/380481
class EnvelopingConverterFactory : Converter.Factory(){
    override fun responseBodyConverter (
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        annotations.forEach {
           return when (it){
               is Json -> MoshiConverterFactory.create(moshi)
                   .responseBodyConverter(type, annotations, retrofit)
               is Scalar -> ScalarsConverterFactory.create()
                   .responseBodyConverter(type, annotations, retrofit)
               else -> return null
           }
        }
        return null
    }

    companion object {
        fun create() = EnvelopingConverterFactory()
    }
}