package com.udacity.asteroidradar.work

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :CoroutineWorker(appContext, params) {

    companion object{
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        val db = getDatabase(applicationContext)
        val repo = AsteroidsRepository(applicationContext, db)
        return try {
            repo.refreshAsteroids()
            db.asteroidDao.deleteStaleAsteroids()
            Result.success()
        } catch (e: HttpException){
            Result.retry()
        }
    }
}