package com.example.buildings.domain.repository

import com.example.buildings.domain.data.Complex
import kotlinx.coroutines.flow.Flow

interface ComplexesRepository {
    fun getAreas(): Flow<List<String>>
    fun getMinCost(): Flow<Double?>
    fun getMaxCost(): Flow<Double?>
    fun getAllComplexes(): Flow<List<Complex>>
    fun getFilterComplexes(filter: Map<String, Any>): Flow<List<Complex>>
    suspend fun getComplexById(id: Int): Flow<Complex?>
    suspend fun insertComplex(complex: Complex): Flow<Int>
    suspend fun deleteComplex(complex: Complex): Flow<Boolean>
}