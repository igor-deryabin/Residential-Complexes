package com.example.buildings.di

import com.example.buildings.data.repository.ComplexesRepositoryImpl
import com.example.buildings.domain.repository.ComplexesRepository
import com.example.buildings.domain.usecase.*
import org.koin.dsl.module

val domainAppModule = module {
    single<ComplexesRepository> { ComplexesRepositoryImpl(dao = get()) }

    single<ComplexesUseCases> {
        ComplexesUseCases(
            getAreas = GetAreasUseCase(repository = get()),
            getMinCost = GetMinCostUseCase(repository = get()),
            getMaxCost = GetMaxCostUseCase(repository = get()),
            getAllComplexes = GetAllComplexesUseCase(repository = get()),
            getFilterComplexes = GetFilterComplexesUseCase(repository = get()),
            getComplex = GetComplexUseCase(repository = get()),
            insertComplex = InsertComplexUseCase(repository = get()),
            removeComplex = RemoveComplexUseCase(repository = get())
        )
    }
}
