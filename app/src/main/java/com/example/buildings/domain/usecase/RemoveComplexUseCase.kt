package com.example.buildings.domain.usecase

import com.example.buildings.domain.data.Complex
import com.example.buildings.domain.repository.ComplexesRepository
import com.example.buildings.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RemoveComplexUseCase(
    private val repository: ComplexesRepository
) {
    suspend operator fun invoke(complex: Complex): Flow<Result<Boolean>> {
        return repository.deleteComplex(complex).map { Result.success(it) }
    }
}