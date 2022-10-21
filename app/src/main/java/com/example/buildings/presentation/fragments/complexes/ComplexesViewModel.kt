package com.example.buildings.presentation.fragments.complexes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buildings.domain.data.Complex
import com.example.buildings.domain.usecase.ComplexesUseCases
import com.example.buildings.utils.Event
import com.example.buildings.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ComplexesViewModel(
    private val complexesUseCases: ComplexesUseCases
) : ViewModel() {

    private var _error = MutableLiveData<Event<String>>()
    var error: LiveData<Event<String>> = _error

    private var _areasLoaded = MutableLiveData<List<String>>()
    var areasLoaded: LiveData<List<String>> = _areasLoaded

    private var _minCost = MutableLiveData<Double>()
    var minCost: LiveData<Double> = _minCost

    private var _maxCost = MutableLiveData<Double>()
    var maxCost: LiveData<Double> = _maxCost

    private var _complexesLoaded = MutableLiveData<Result<List<Complex>>>()
    var complexesLoaded: LiveData<Result<List<Complex>>> = _complexesLoaded

    var filterCostMin = -1.0
    var filterCostMax = -1.0
    var filterArea: String = ""

    init {
        getFilters()
        getComplexesList()
    }

    private fun getFilters() {
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.getAreas()
                .collect {
                    withContext(Dispatchers.Main) {
                        _areasLoaded.value = it
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.getMinCost()
                .collect {
                    withContext(Dispatchers.Main) {
                        _minCost.value = it
                    }
                }
        }
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.getMaxCost()
                .collect {
                    withContext(Dispatchers.Main) {
                        _maxCost.value = it
                    }
                }
        }
    }

    fun getComplexesList() {
        if (filterArea.isEmpty() && filterCostMin == -1.0 && filterCostMax == -1.0) {
            getAllComplexesList()
        } else {
            getFilterComplexesList()
        }
    }

    private fun getAllComplexesList() {
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.getAllComplexes()
                .onStart { emit(Result.loading()) }
                .catch { e ->
                    _error.value = Event(e.message ?: "")
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        _complexesLoaded.value = it
                    }
                }
        }
    }

    fun getFilterComplexesList() {
        viewModelScope.launch(Dispatchers.IO) {
            val hashMap = emptyMap<String, Any>().toMutableMap()
            if (filterArea.isNotEmpty()) {
                hashMap["area"] = filterArea
            }
            if (filterCostMin != -1.0) {
                hashMap["costMin"] = filterCostMin
            }
            if (filterCostMax != -1.0) {
                hashMap["costMax"] = filterCostMax
            }
            complexesUseCases.getFilterComplexes(hashMap.toMap())
                .onStart { emit(Result.loading()) }
                .catch { e ->
                    _error.value = Event(e.message ?: "")
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        _complexesLoaded.value = it
                    }
                }
        }
    }
}