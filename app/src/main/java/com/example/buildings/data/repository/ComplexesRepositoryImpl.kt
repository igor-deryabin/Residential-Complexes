package com.example.buildings.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.buildings.data.local.ComplexesDao
import com.example.buildings.domain.data.Complex
import com.example.buildings.domain.repository.ComplexesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


class ComplexesRepositoryImpl(
    private val dao: ComplexesDao
): ComplexesRepository {
    override fun getAreas(): Flow<List<String>> {
        return dao.getAreas()
    }

    override fun getMinCost(): Flow<Double> {
        return dao.getMinCost()
    }

    override fun getMaxCost(): Flow<Double> {
        return dao.getMaxCost()
    }

    override fun getAllComplexes(): Flow<List<Complex>> {
        return dao.getAllComplexes()
    }

    override fun getFilterComplexes(filter: Map<String, Any>): Flow<List<Complex>> {
        var queryString = String()
        val args: MutableList<Any> = ArrayList()
        var containsCondition = false

        queryString += "SELECT * FROM complexes"

        if (filter["area"] != null && filter["area"] is String) {
            queryString += " WHERE"
            queryString += " area = ?"
            args.add(filter["area"] as String)
            containsCondition = true
        }

        if (filter["costMin"] != null && filter["costMin"] is Double) {
            if (containsCondition) {
                queryString += " AND"
            } else {
                queryString += " WHERE"
                containsCondition = true
            }
            queryString += " cost >= ?"
            args.add(filter["costMin"] as Double)
        }

        if (filter["costMax"] != null && filter["costMax"] is Double) {
            if (containsCondition) {
                queryString += " AND"
            } else {
                queryString += " WHERE"
                containsCondition = true
            }
            queryString += " cost <= ?"
            args.add(filter["costMax"] as Double)
        }

        queryString += ";"
        val query = SimpleSQLiteQuery(queryString, args.toTypedArray())
        return dao.getFilterComplexes(query)
    }

    override suspend fun getComplexById(id: Int): Flow<Complex?> {
        return flow {
            emit(dao.getComplexById(id))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun insertComplex(complex: Complex): Flow<Int> {
        return flow {
            emit(dao.insertComplex(complex).toInt())
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteComplex(complex: Complex): Flow<Boolean> {
        return flow {
            dao.deleteComplex(complex)
            emit(true)
        }.flowOn(Dispatchers.IO)
    }
}