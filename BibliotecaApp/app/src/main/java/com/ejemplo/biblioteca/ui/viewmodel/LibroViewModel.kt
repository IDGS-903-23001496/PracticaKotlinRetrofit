package com.ejemplo.biblioteca.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.biblioteca.data.model.Libro
import com.ejemplo.biblioteca.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class LibroViewModel : ViewModel() {
    private val api = RetrofitClient.apiService

    private val _librosState = MutableStateFlow<UiState<List<Libro>>>(UiState.Idle)
    val librosState: StateFlow<UiState<List<Libro>>> = _librosState

    private val _operacionState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val operacionState: StateFlow<UiState<String>> = _operacionState

    init {
        obtenerLibros()
    }

    fun obtenerLibros() {
        viewModelScope.launch {
            _librosState.value = UiState.Loading
            try {
                val response = api.obtenerLibros()
                if (response.isSuccessful && response.body() != null) {
                    _librosState.value = UiState.Success(response.body()!!)
                } else {
                    _librosState.value = UiState.Error("Error en servidor: ${response.code()}")
                }
            } catch (e: IOException) {
                _librosState.value = UiState.Error("No hay conexión con el servidor Flask.")
            } catch (e: Exception) {
                _librosState.value = UiState.Error("Error inesperado al obtener los datos.")
            }
        }
    }

    fun registrarLibro(libro: Libro, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operacionState.value = UiState.Loading
            try {
                val response = api.crearLibro(libro)
                if (response.isSuccessful) {
                    _operacionState.value = UiState.Success("Libro guardado")
                    obtenerLibros()
                    onSuccess()
                } else {
                    _operacionState.value = UiState.Error("Error al guardar: ${response.code()}")
                }
            } catch (e: Exception) {
                _operacionState.value = UiState.Error("Error de conexión.")
            }
        }
    }

    fun actualizarLibro(id: Int, libro: Libro, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _operacionState.value = UiState.Loading
            try {
                val response = api.actualizarLibro(id, libro)
                if (response.isSuccessful) {
                    _operacionState.value = UiState.Success("Libro actualizado")
                    obtenerLibros()
                    onSuccess()
                } else {
                    _operacionState.value = UiState.Error("Error al actualizar: ${response.code()}")
                }
            } catch (e: Exception) {
                _operacionState.value = UiState.Error("Error de conexión.")
            }
        }
    }

    fun eliminarLibro(id: Int) {
        viewModelScope.launch {
            _operacionState.value = UiState.Loading
            try {
                val response = api.eliminarLibro(id)
                if (response.isSuccessful) {
                    _operacionState.value = UiState.Success("Libro eliminado")
                    obtenerLibros()
                } else {
                    _operacionState.value = UiState.Error("No se pudo eliminar.")
                }
            } catch (e: Exception) {
                _operacionState.value = UiState.Error("Error de conexión.")
            }
        }
    }

    fun resetOperacionState() {
        _operacionState.value = UiState.Idle
    }
}