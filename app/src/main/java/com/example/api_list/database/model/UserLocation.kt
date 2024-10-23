package com.example.api_list.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_locations")
data class UserLocation (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    var latitude: Double = 0.toDouble(),
    var longitude: Double = 0.toDouble(),
    var createdAt: Date = Date()
)