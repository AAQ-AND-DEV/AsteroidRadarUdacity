package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidNetwork
import com.udacity.asteroidradar.api.asDomainModel
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private val _asteroidList = mutableListOf<Asteroid>()
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    init{
        viewModelScope.launch{
            //getAsteroids()
            getPod()
        }
    }

    private suspend fun getAsteroids(){
        AsteroidNetwork.asteroidService.getAsteroids("2020-12-25", "2020-12-26",
            getApplication<AsteroidApplication>().resources.getString(R.string.neoWs_key)).await()

    }

    private suspend fun getPod(){
        _pod.value = AsteroidNetwork.asteroidService.getPod(
            apiKey = getApplication<AsteroidApplication>().resources.getString(R.string.neoWs_key))
            .await().asDomainModel()

    }
}