package com.example.data

import kotlinx.coroutines.flow.Flow

class GardenRepository(private val gardenPlantDao: GardenPlantDao) {
    val allGardenPlants: Flow<List<GardenPlant>> = gardenPlantDao.getAllGardenPlants()

    suspend fun insert(gardenPlant: GardenPlant) {
        gardenPlantDao.insertGardenPlant(gardenPlant)
    }

    suspend fun update(gardenPlant: GardenPlant) {
        gardenPlantDao.updateGardenPlant(gardenPlant)
    }

    suspend fun delete(gardenPlant: GardenPlant) {
        gardenPlantDao.deleteGardenPlant(gardenPlant)
    }

    suspend fun deleteById(id: Int) {
        gardenPlantDao.deleteById(id)
    }
}
