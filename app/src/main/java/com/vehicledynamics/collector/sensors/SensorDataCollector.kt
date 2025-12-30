package com.vehicledynamics.collector.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import com.vehicledynamics.collector.vss.*

/**
 * Sensor data collector that manages all smartphone sensors
 * for vehicle dynamics data collection
 */
class SensorDataCollector(private val context: Context) : SensorEventListener {

    private val sensorManager: SensorManager = 
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    
    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    
    // Current sensor data
    private var currentAcceleration = VSSAcceleration()
    private var currentAngularVelocity = VSSAngularVelocity()
    private var currentPosition = VSSPosition()
    private var currentSpeed = VSSSpeed()
    
    private var dataListener: ((VSSVehicle) -> Unit)? = null
    private var isCollecting = false
    
    init {
        // Initialize sensors
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        
        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
    
    /**
     * Start data collection from all sensors
     */
    fun startCollection(listener: (VSSVehicle) -> Unit) {
        if (isCollecting) return
        
        isCollecting = true
        dataListener = listener
        
        // Register sensor listeners
        accelerometerSensor?.let {
            sensorManager.registerListener(
                this, 
                it, 
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        
        gyroscopeSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        
        // Start location updates
        startLocationUpdates()
    }
    
    /**
     * Stop data collection
     */
    fun stopCollection() {
        if (!isCollecting) return
        
        isCollecting = false
        sensorManager.unregisterListener(this)
        stopLocationUpdates()
        dataListener = null
    }
    
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000 // Update interval: 1 second
        ).build()
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }
        
        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle missing permission
        }
    }
    
    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
    }
    
    private fun updateLocation(location: Location) {
        currentPosition = VSSPosition(
            latitude = location.latitude,
            longitude = location.longitude,
            altitude = location.altitude,
            heading = location.bearing,
            speed = location.speed
        )
        
        // Convert m/s to km/h
        currentSpeed = VSSSpeed(
            value = location.speed * 3.6f
        )
        
        notifyDataUpdate()
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    // Device coordinate system: X=right, Y=forward, Z=up
                    // Convert to vehicle coordinate system
                    currentAcceleration = VSSAcceleration(
                        longitudinal = it.values[1],  // Forward/backward
                        lateral = it.values[0],       // Left/right
                        vertical = it.values[2]       // Up/down
                    )
                }
                Sensor.TYPE_GYROSCOPE -> {
                    // Gyroscope values in rad/s, convert to degrees/s
                    currentAngularVelocity = VSSAngularVelocity(
                        roll = Math.toDegrees(it.values[0].toDouble()).toFloat(),
                        pitch = Math.toDegrees(it.values[1].toDouble()).toFloat(),
                        yaw = Math.toDegrees(it.values[2].toDouble()).toFloat()
                    )
                }
            }
            notifyDataUpdate()
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle accuracy changes if needed
    }
    
    private fun notifyDataUpdate() {
        if (!isCollecting) return
        
        val vssData = VSSVehicle(
            speed = currentSpeed,
            acceleration = currentAcceleration,
            position = currentPosition,
            angularVelocity = currentAngularVelocity,
            timestamp = System.currentTimeMillis()
        )
        
        dataListener?.invoke(vssData)
    }
    
    fun getCurrentData(): VSSVehicle {
        return VSSVehicle(
            speed = currentSpeed,
            acceleration = currentAcceleration,
            position = currentPosition,
            angularVelocity = currentAngularVelocity,
            timestamp = System.currentTimeMillis()
        )
    }
}
