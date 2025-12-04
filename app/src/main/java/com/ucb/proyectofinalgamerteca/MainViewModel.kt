package com.ucb.proyectofinalgamerteca

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.proyectofinalgamerteca.core.data.RemoteConfigManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainViewModel(
    private val remoteConfigManager: RemoteConfigManager
) : ViewModel() {

    private val _isMaintenance = MutableStateFlow(false)
    val isMaintenance: StateFlow<Boolean> = _isMaintenance

    private val _isLoadingConfig = MutableStateFlow(true)
    val isLoadingConfig: StateFlow<Boolean> = _isLoadingConfig

    init {
        checkMaintenanceStatus()
    }

    private fun checkMaintenanceStatus() {
        viewModelScope.launch {
            _isLoadingConfig.value = true
            Log.d("MainVM", "🔹 Iniciando chequeo de mantenimiento...")

            // Usamos un TIMEOUT de seguridad (ej: 4 segundos)
            // Si Firebase no responde en 4s, dejamos pasar al usuario para no bloquearlo.
            val result = withTimeoutOrNull(4000) {
                remoteConfigManager.fetchAndActivate()
            }

            if (result == null) {
                Log.w("MainVM", "⚠️ Timeout: Firebase tardó mucho. Asumimos sin mantenimiento.")
                _isMaintenance.value = false
            } else {
                Log.d("MainVM", "✅ Config recibida. Mantenimiento activo: $result")
                _isMaintenance.value = result
            }

            // IMPORTANTE: Un pequeño delay visual para evitar parpadeos si es muy rápido
            // y asegurar que la UI de carga se quite.
            delay(500)
            _isLoadingConfig.value = false
        }
    }
}