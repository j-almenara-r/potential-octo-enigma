package com.vehicledynamics.collector.auto

import android.content.Intent
import androidx.car.app.CarAppService
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

/**
 * Android Auto Car App Service
 * This service enables the app to run on Android Auto head units
 */
class VehicleDynamicsCarAppService : CarAppService() {

    override fun createHostValidator(): HostValidator {
        // ALLOW_ALL_HOSTS_VALIDATOR is used for development and testing.
        // WARNING: In production, implement proper host validation using
        // HostValidator.Builder().addAllowedHosts() with specific trusted hosts.
        // This prevents unauthorized apps from connecting to this service.
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }

    override fun onCreateSession(): Session {
        return VehicleDynamicsSession()
    }
}

/**
 * Session for the Android Auto app
 */
class VehicleDynamicsSession : Session() {
    
    override fun onCreateScreen(intent: Intent): Screen {
        return VehicleDynamicsScreen(carContext)
    }
}
