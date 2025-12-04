package com.ucb.proyectofinalgamerteca.core.data

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await

class RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        // Configuración para desarrollo: fetch cada 0 segundos (para ver cambios al instante)
        // En producción, esto debería ser 3600 (1 hora)
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        // Valor por defecto: Mantenimiento APAGADO
        remoteConfig.setDefaultsAsync(mapOf(KEY_MAINTENANCE_MODE to false))
    }

    // Función para pedir la config a la nube
    suspend fun fetchAndActivate(): Boolean {
        return try {
            // Intentamos obtener y activar
            remoteConfig.fetchAndActivate().await()

            // Leemos el valor
            val isActive = remoteConfig.getBoolean(KEY_MAINTENANCE_MODE)
            isActive
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla, devolvemos false (dejar pasar al usuario)
            false
        }
    }

    // Verifica si la bandera está activa
    fun isMaintenanceMode(): Boolean {
        return remoteConfig.getBoolean(KEY_MAINTENANCE_MODE)
    }

    companion object {
        const val KEY_MAINTENANCE_MODE = "is_maintenance_mode"
    }

}