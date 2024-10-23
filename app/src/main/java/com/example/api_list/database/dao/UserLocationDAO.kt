package com.example.api_list.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.api_list.database.model.UserLocation

@Dao
interface UserLocationDAO {

    @Insert
    suspend fun insert(userLocation: UserLocation)

    @Query("SELECT * FROM user_locations")
    suspend fun getAllUserLocations(): List<UserLocation>

    @Query("SELECT * FROM user_locations ORDER BY id DESC LIMIT 1")
    suspend fun getLastUserLocation(): UserLocation?

}