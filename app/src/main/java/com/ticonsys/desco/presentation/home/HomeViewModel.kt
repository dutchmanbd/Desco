package com.ticonsys.desco.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ticonsys.desco.util.SharedPref
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sharedPref: SharedPref
) : ViewModel() {

    companion object {
        private const val PREF_ACCOUNT_NUMBER = "pref_account_number"
    }

    private val _uiState = MutableStateFlow(
        HomeUiState(
        )
    )
    val uiState: StateFlow<HomeUiState>
        get() = _uiState.asStateFlow()


    init {
        val accountNumber = sharedPref.read(PREF_ACCOUNT_NUMBER, "")
        _uiState.update { state ->
            state.copy(
                accountNumber = accountNumber,
                isSubmitted = accountNumber.isNotEmpty()
            )
        }
    }

    fun updateAccountNumber(accountNumber: String) {
        _uiState.update { state ->
            state.copy(
                accountNumber = accountNumber
            )
        }
    }

    fun saveAccountNumber() {
        sharedPref.write(PREF_ACCOUNT_NUMBER, uiState.value.accountNumber)
        _uiState.update { state -> state.copy(isSubmitted = true) }
    }


}