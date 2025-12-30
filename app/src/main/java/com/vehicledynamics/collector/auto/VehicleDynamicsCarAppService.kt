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
        // Allow all hosts for development
        // In production, should validate specific hosts
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
