package com.example.buildings.data.local

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.buildings.domain.data.Complex
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplexesDao {
    @Query("SELECT DISTINCT area FROM complexes")
    fun getAreas() : Flow<List<String>>

    @Query("SELECT MIN(cost) FROM complexes")
    fun getMinCost() : Flow<Double>

    @Query("SELECT MAX(cost) FROM complexes")
    fun getMaxCost() : Flow<Double>

    @Query("SELECT * FROM complexes")
    fun getAllComplexes() : Flow<List<Complex>>

    @RawQuery(observedEntities = [Complex::class])
    fun getFilterComplexes(query: SupportSQLiteQuery) : Flow<List<Complex>>

    @Query("SELECT * FROM complexes WHERE id = :id")
    suspend fun getComplexById(id: Int): Complex?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplex(complex: Complex): Long

    @Delete
    suspend fun deleteComplex(complex: Complex)
}