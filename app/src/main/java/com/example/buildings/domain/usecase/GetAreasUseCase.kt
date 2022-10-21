package com.example.buildings.domain.usecase

import com.example.buildings.domain.repository.ComplexesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAreasUseCase(
    private val repository: ComplexesRepository
) {
    operator fun invoke(): Flow<List<String>> {
        return repository.getAreas().map { it }
    }
}