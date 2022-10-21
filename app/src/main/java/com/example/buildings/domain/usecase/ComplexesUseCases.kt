package com.example.buildings.domain.usecase

data class ComplexesUseCases(
    val getAreas: GetAreasUseCase,
    val getMinCost: GetMinCostUseCase,
    val getMaxCost: GetMaxCostUseCase,
    val getAllComplexes: GetAllComplexesUseCase,
    val getFilterComplexes: GetFilterComplexesUseCase,
    val getComplex: GetComplexUseCase,
    val insertComplex: InsertComplexUseCase,
    val removeComplex: RemoveComplexUseCase
)