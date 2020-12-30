package com.udacity.asteroidradar

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.DatabaseAsteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import timber.log.Timber
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var context: Context
    private lateinit var db: AsteroidsDatabase
    private lateinit var dao : AsteroidDao
    private lateinit var repo: AsteroidsRepository
    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp(){
        context = ApplicationProvider.getApplicationContext()
        val localContext = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(localContext, AsteroidsDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.asteroidDao
        repo = AsteroidsRepository(localContext, db)
    }

    private suspend fun populateDb(){

        val asteroid1 = DatabaseAsteroid(
            1, "test1", "2020-01-12",
            40.0, 45.0, 42.0, 41.0, false
        )
        val asteroid2 = DatabaseAsteroid(
            2, "test2", "2020-12-30",
            45.0, 45.0, 45.0, 45.0, true
        )
        val asteroidArray = arrayOf(asteroid1, asteroid2)

        dao.insertAll(*asteroidArray)

    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun addItemsToDb(){
        runBlocking {
            populateDb()

        }
        val asteroids = dao.getAllAsteroids().blockingObserve()
        if (asteroids != null) {
            for (item in asteroids){
                println(item.toString())
            }
        }
        assertEquals(2, asteroids?.size)
    }

    @Test
    @Throws(Exception::class)
    fun deleteOldAsteroid(){
        runBlocking {

            populateDb()
        }
        val currAsteroids = dao.getAllAsteroids().blockingObserve()
        assertEquals(2, currAsteroids?.size)
        runBlocking {
            dao.deleteStaleAsteroids()
        }
        val newAsteroids = dao.getAllAsteroids().blockingObserve()
        assertEquals(1, newAsteroids?.size)
    }

    //Extension function for observing LiveData returned from Dao
    //found: https://stackoverflow.com/a/44991770/8049500
    private fun <T> LiveData<T>.blockingObserve(): T? {
        var value: T? = null
        val latch = CountDownLatch(1)

        val observer = Observer<T> { t ->
            value = t
            latch.countDown()
        }

        observeForever(observer)

        latch.await(2, TimeUnit.SECONDS)
        return value
    }
}
