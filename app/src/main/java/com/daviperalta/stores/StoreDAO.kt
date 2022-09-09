package com.daviperalta.stores

import androidx.room.*

@Dao
interface StoreDAO {
    @Query("SELECT * FROM StoreEntity")
    fun getAllStores(): MutableList<StoreEntity>

    @Insert
    fun addStore(storeEntity: StoreEntity): Long

    @Update
    fun  updateStore(storeEntity: StoreEntity)

    @Delete
    fun deleteStore(storeEntity: StoreEntity)
}