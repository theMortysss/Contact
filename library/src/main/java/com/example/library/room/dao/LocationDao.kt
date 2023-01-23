package com.example.library.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.library.room.entity.LocationEntity

@Dao
interface LocationDao {

    @Insert
    suspend fun addContactLocation(locationEntity: LocationEntity)

    @Query("SELECT * FROM marks_location_table WHERE id == :id")
    suspend fun getContactLocation(id: String): LocationEntity?

    @Query("SELECT * FROM marks_location_table")
    suspend fun getAllContactLocations(): List<LocationEntity>?

    @Query("SELECT COUNT(id) FROM marks_location_table")
    suspend fun getRowCount(): Int

    @Update
    suspend fun updateContactLocation(contactLocationEntity: LocationEntity)

    @Query("DELETE FROM marks_location_table WHERE id == :id")
    suspend fun deleteContactLocationById(id: String)
}
