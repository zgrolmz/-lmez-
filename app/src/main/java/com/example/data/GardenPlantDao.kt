package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GardenPlantDao {
    @Query("SELECT * FROM garden_plants ORDER BY datePlanted DESC")
    fun getAllGardenPlants(): Flow<List<GardenPlant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGardenPlant(gardenPlant: GardenPlant)

    @Update
    suspend fun updateGardenPlant(gardenPlant: GardenPlant)

    @Delete
    suspend fun deleteGardenPlant(gardenPlant: GardenPlant)

    @Query("DELETE FROM garden_plants WHERE id = :id")
    suspend fun deleteById(id: Int)
}
