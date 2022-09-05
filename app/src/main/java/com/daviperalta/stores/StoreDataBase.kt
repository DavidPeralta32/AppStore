package com.daviperalta.stores

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(StoreEntity::class), version = 1)
abstract class StoreDataBase :RoomDatabase(){

    abstract fun storeDao(): StoreDAO

}