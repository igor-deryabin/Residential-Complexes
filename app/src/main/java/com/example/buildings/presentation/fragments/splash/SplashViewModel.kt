package com.example.buildings.presentation.fragments.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SplashViewModel() : ViewModel() {

    private var _error = MutableLiveData<Error>()
    var error: LiveData<Error> = _error
}