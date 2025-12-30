package com.vehicledynamics.collector.vss

/**
 * Vehicle Signal Specification (VSS) data model
 * Based on COVESA VSS specification: https://github.com/COVESA/vehicle_signal_specification
 * 
 * This implements a simplified version of VSS for vehicle dynamics data collection
 */
data class VSSVehicle(
    val speed: VSSSpeed = VSSSpeed(),
    val acceleration: VSSAcceleration = VSSAcceleration(),
    val position: VSSPosition = VSSPosition(),
    val angularVelocity: VSSAngularVelocity = VSSAngularVelocity(),
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Vehicle speed signal (VSS: Vehicle.Speed)
 * Unit: km/h
 */
data class VSSSpeed(
    val value: Float = 0f,
    val unit: String = "km/h"
)

/**
 * Vehicle acceleration signals (VSS: Vehicle.Acceleration)
 * Units: m/s²
 */
data class VSSAcceleration(
    val longitudinal: Float = 0f,  // Forward/backward acceleration
    val lateral: Float = 0f,        // Left/right acceleration
    val vertical: Float = 0f,       // Up/down acceleration
    val unit: String = "m/s^2"
)

/**
 * Vehicle position signals (VSS: Vehicle.CurrentLocation)
 */
data class VSSPosition(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val altitude: Double = 0.0,
    val heading: Float = 0f,        // Degrees (0-360)
    val speed: Float = 0f           // Speed from GPS (m/s)
)

/**
 * Angular velocity signals (VSS: Vehicle.AngularVelocity)
 * Unit: degrees/s
 */
data class VSSAngularVelocity(
    val roll: Float = 0f,   // Rotation around longitudinal axis
    val pitch: Float = 0f,  // Rotation around lateral axis
    val yaw: Float = 0f,    // Rotation around vertical axis
    val unit: String = "degrees/s"
)

/**
 * VSS Data Session - represents a data collection campaign
 */
data class VSSDataSession(
    val sessionId: String,
    val startTime: Long,
    val endTime: Long? = null,
    val samples: MutableList<VSSVehicle> = mutableListOf(),
    val metadata: Map<String, String> = emptyMap()
)
