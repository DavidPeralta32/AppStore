package com.daviperalta.stores

import android.app.Application
import androidx.room.Room

class StoreApplication : Application() {
    //patron singleton
    companion object{
        lateinit var dataBase: StoreDataBase
    }

    override fun onCreate() {
        super.onCreate()

        dataBase = Room.databaseBuilder(this, StoreDataBase::class.java, "StoreDatabase").build()
    }
}