package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private val database = getDatabase(application)
    private val repo = AsteroidsRepository(application, database)
    init{
        viewModelScope.launch{
            repo.refreshAsteroids()
            getPod()
        }
    }

    val asteroids = repo.asteroids

    private suspend fun getPod(){
        _pod.value = AsteroidNetwork.asteroidService.getPod(
            apiKey = getApplication<AsteroidApplication>().resources.getString(R.string.neoWs_key))
            .asDomainModel()

    }
}