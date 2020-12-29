package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.AsteroidApplication
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.AsteroidNetwork
import com.udacity.asteroidradar.api.asDomainModel
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _pod = MutableLiveData<PictureOfDay>()
    val pod: LiveData<PictureOfDay>
        get() = _pod

    private val database = getDatabase(application)
    private val repo = AsteroidsRepository(application, database)

    init {
        viewModelScope.launch {
            try {
                repo.refreshAsteroids()
                getPod()
            } catch (e: Exception) {
                Timber.e(e.message)
            }
        }
    }

    val asteroids = repo.asteroids

    private suspend fun getPod() {
        _pod.value = AsteroidNetwork.asteroidService.getPod(
            apiKey = getApplication<AsteroidApplication>().resources.getString(R.string.neoWs_key)
        )
            .asDomainModel()

    }
}