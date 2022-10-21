package com.example.buildings.domain.usecase

import com.example.buildings.domain.data.Complex
import com.example.buildings.domain.repository.ComplexesRepository
import com.example.buildings.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetComplexUseCase(
    private val repository: ComplexesRepository
) {
    suspend operator fun invoke(id: Int): Flow<Result<Complex?>> {
        return repository.getComplexById(id).map { Result.success(it) }
    }
}