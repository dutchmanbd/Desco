package com.ticonsys.desco.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ticonsys.desco.util.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPref: SharedPref
) : ViewModel() {

    companion object {
        private const val PREF_ACCOUNT_NUMBER = "pref_account_number"
    }

    private val _hasSubmitted = mutableStateOf(false)
    val hasSubmitted: State<Boolean>
        get() = _hasSubmitted

    private val _accountNumber = mutableStateOf(sharedPref.read(PREF_ACCOUNT_NUMBER, ""))
    val accountNumber: State<String>
        get() = _accountNumber


    fun updateAccountNumber(accountNumber: String) {
        _accountNumber.value = accountNumber
    }

    fun submit(hasSubmit: Boolean) {
        sharedPref.write(PREF_ACCOUNT_NUMBER, _accountNumber.value)
        _hasSubmitted.value = hasSubmit
    }

}