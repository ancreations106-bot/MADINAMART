package com.madinamart.app

import android.app.Application
import com.madinamart.app.data.repository.StoreRepository

class MadinaMartApplication : Application() {
    lateinit var repository: StoreRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        repository = StoreRepository(this)
    }

    companion object {
        lateinit var instance: MadinaMartApplication
            private set
    }
}
