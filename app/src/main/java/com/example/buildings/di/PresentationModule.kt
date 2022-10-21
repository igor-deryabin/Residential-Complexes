package com.example.buildings.di

import com.example.buildings.presentation.fragments.complex.ComplexViewModel
import com.example.buildings.presentation.fragments.complexes.ComplexesViewModel
import com.example.buildings.presentation.fragments.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationAppModule = module {
    viewModel { SplashViewModel() }
    viewModel { ComplexesViewModel(complexesUseCases = get()) }
    viewModel { ComplexViewModel(complexesUseCases = get()) }
}