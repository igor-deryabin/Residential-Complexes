package com.example.buildings.presentation.fragments.complexes.adapterComplexes

import com.example.buildings.domain.data.Complex


interface ComplexesListener {
    fun openComplex(item: Complex)
}