package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.api.getTodaysDate
import retrofit2.http.DELETE

@Dao
interface AsteroidDao{

    @Query("SELECT * FROM databaseasteroid WHERE closeApproachDate >= DATE() ORDER BY closeApproachDate ASC")
    fun getAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseasteroid ORDER BY closeApproachDate ASC")
    fun getAllAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE FROM databaseasteroid WHERE closeApproachDate < DATE()")
    suspend fun deleteStaleAsteroids()
}

@Database(entities=[DatabaseAsteroid::class], version = 1, exportSchema = false)
abstract class AsteroidsDatabase : RoomDatabase(){
    abstract val asteroidDao :AsteroidDao
}

private lateinit var INSTANCE : AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase{
    synchronized(AsteroidsDatabase::class.java){
        if (!::INSTANCE.isInitialized){
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            AsteroidsDatabase::class.java, "asteroids")
                .build()
        }
    }
    return INSTANCE
}