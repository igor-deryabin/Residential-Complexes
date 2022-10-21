package com.example.buildings.presentation.fragments.complex

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


class ComplexViewModel(
    private val complexesUseCases: ComplexesUseCases
) : ViewModel() {

    private var _error = MutableLiveData<Event<String>>()
    var error: LiveData<Event<String>> = _error

    private var _complexLoaded = MutableLiveData<Result<Complex?>>()
    var complexLoaded: LiveData<Result<Complex?>> = _complexLoaded

    private var _complexInserted = MutableLiveData<Result<Int>>()
    var complexInserted: LiveData<Result<Int>> = _complexInserted

    private var _complexRemoved = MutableLiveData<Result<Boolean>>()
    var complexRemoved: LiveData<Result<Boolean>> = _complexRemoved

    private var currentComplexId: Int? = null

    fun getComplexFromId(id: Int) {
        currentComplexId = id
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.getComplex(id)
                .onStart { emit(Result.loading()) }
                .catch { e ->
                    _error.value = Event(e.message ?: "")
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        _complexLoaded.value = it
                    }
                }
        }
    }

    fun insertComplex(complex: Complex) {
        currentComplexId?.let { complex.id = it }
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.insertComplex(complex)
                .onStart { emit(Result.loading()) }
                .catch { e ->
                    _error.value = Event(e.message ?: "")
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        _complexInserted.value = it
                    }
                }
        }
    }

    fun removeComplex(complex: Complex) {
        viewModelScope.launch(Dispatchers.IO) {
            complexesUseCases.removeComplex(complex)
                .onStart { emit(Result.loading()) }
                .catch { e ->
                    _error.value = Event(e.message ?: "")
                }
                .collect {
                    withContext(Dispatchers.Main) {
                        _complexRemoved.value = it
                    }
                }
        }
    }
}