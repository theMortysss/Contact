package com.example.library.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.library.room.dao.LocationDao
import com.example.library.room.entity.LocationEntity

@Database(entities = [LocationEntity::class], version = 1)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao
}
