package com.example.buildings.domain.usecase

import com.example.buildings.domain.data.Complex
import com.example.buildings.domain.repository.ComplexesRepository
import com.example.buildings.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFilterComplexesUseCase(
    private val repository: ComplexesRepository
) {
    operator fun invoke(filter: Map<String, Any>): Flow<Result<List<Complex>>> {
        return repository.getFilterComplexes(filter).map { Result.success(it) }
    }
}