package com.udacity.asteroidradar.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AsteroidsRepository(private val appContext: Context, private val database: AsteroidsDatabase) {

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAsteroids()){
        it.asDomainModel()
    }

    suspend fun refreshAsteroids(){
        withContext(Dispatchers.IO){
            val result = AsteroidNetwork.asteroidService.getAsteroids(TODAYS_DATE,
                appContext.resources.getString(R.string.neoWs_key))
            val resultJson = JSONObject( result )
            val asteroids = parseAsteroidsJsonResult( resultJson )
            val asteroidContainer = NetworkAsteroidContainer(asteroids)
            database.asteroidDao.insertAll(*asteroidContainer.asDatabaseModel().toTypedArray())
        }
    }
}