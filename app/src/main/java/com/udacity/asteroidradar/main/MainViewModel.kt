package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidNetwork
import com.udacity.asteroidradar.api.asDomainModel
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private var _asteroidList = listOf<Asteroid>()
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init{
        viewModelScope.launch{
            getAsteroids()
            getPod()
        }
    }

    private suspend fun getAsteroids(){
        _asteroidList =
            parseAsteroidsJsonResult(
                JSONObject(AsteroidNetwork.asteroidService.getAsteroids("2020-12-25", "2020-12-26",
            getApplication<AsteroidApplication>().resources.getString(R.string.neoWs_key))))
        _asteroids.value = _asteroidList
    }

    private suspend fun getPod(){
        _pod.value = AsteroidNetwork.asteroidService.getPod(
            apiKey = getApplication<AsteroidApplication>().resources.getString(R.string.neoWs_key))
            .asDomainModel()

    }
}