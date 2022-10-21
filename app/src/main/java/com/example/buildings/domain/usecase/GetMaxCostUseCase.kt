package com.example.buildings.domain.usecase

import com.example.buildings.domain.repository.ComplexesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetMaxCostUseCase(
    private val repository: ComplexesRepository
) {
    operator fun invoke(): Flow<Double> {
        return repository.getMaxCost().map { it }
    }
}