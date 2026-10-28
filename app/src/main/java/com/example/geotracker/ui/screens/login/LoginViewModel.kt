package com.example.geotracker.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.geotracker.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val usernameOrEmail: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        if (authRepository.isLoggedIn()) {
            _uiState.value = _uiState.value.copy(isSuccess = true)
        }
    }

    fun onUsernameOrEmailChanged(value: String) {
        _uiState.value = _uiState.value.copy(usernameOrEmail = value, errorMessage = null)
    }

    fun onPasswordChanged(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun login() {
        val current = _uiState.value
        if (current.usernameOrEmail.isBlank() || current.password.isBlank()) {
            _uiState.value = current.copy(errorMessage = "Por favor completa todos los campos.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(current.usernameOrEmail.trim(), current.password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } else {
                val errorMsg = result.exceptionOrNull()?.message ?: "Error al iniciar sesión"
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
