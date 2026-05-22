package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "garden_plants")
data class GardenPlant(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val plantId: String,       // Matches Plant.id
    val customName: String,    // User's custom nickname for their plant
    val datePlanted: Long = System.currentTimeMillis(),
    val notes: String = "",
    val lastWatered: Long = System.currentTimeMillis()
)
