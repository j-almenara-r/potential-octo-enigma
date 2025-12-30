package com.vehicledynamics.collector

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.RECEIVER_NOT_EXPORTED
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.vehicledynamics.collector.sensors.SensorDataCollector
import com.vehicledynamics.collector.vss.VSSDataSession
import com.vehicledynamics.collector.vss.VSSVehicle
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var sensorCollector: SensorDataCollector
    private var currentSession: VSSDataSession? = null
    private var isCollecting = false
    
    private lateinit var statusText: TextView
    private lateinit var dataText: TextView
    private lateinit var toggleButton: Button
    
    private val gson = Gson()
    
    private val collectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "com.vehicledynamics.collector.TOGGLE_COLLECTION") {
                val shouldCollect = intent.getBooleanExtra("collecting", false)
                if (shouldCollect && !isCollecting) {
                    startDataCollection()
                } else if (!shouldCollect && isCollecting) {
                    stopDataCollection()
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        statusText = findViewById(R.id.statusText)
        dataText = findViewById(R.id.dataText)
        toggleButton = findViewById(R.id.toggleButton)
        
        sensorCollector = SensorDataCollector(this)
        
        toggleButton.setOnClickListener {
            if (isCollecting) {
                stopDataCollection()
            } else {
                startDataCollection()
            }
        }
        
        // Register broadcast receiver for Android Auto commands
        val filter = IntentFilter("com.vehicledynamics.collector.TOGGLE_COLLECTION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(collectionReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(collectionReceiver, filter)
        }
        
        // Check and request permissions
        checkPermissions()
        
        updateUI()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(collectionReceiver)
        if (isCollecting) {
            stopDataCollection()
        }
    }
    
    private fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this,
                    "Permissions required for data collection",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    private fun startDataCollection() {
        if (!hasRequiredPermissions()) {
            Toast.makeText(
                this,
                "Please grant location permissions",
                Toast.LENGTH_SHORT
            ).show()
            checkPermissions()
            return
        }
        
        isCollecting = true
        currentSession = VSSDataSession(
            sessionId = UUID.randomUUID().toString(),
            startTime = System.currentTimeMillis()
        )
        
        sensorCollector.startCollection { vssData ->
            handleVSSData(vssData)
        }
        
        updateUI()
        Toast.makeText(this, "Data collection started", Toast.LENGTH_SHORT).show()
    }
    
    private fun stopDataCollection() {
        isCollecting = false
        sensorCollector.stopCollection()
        
        currentSession?.let { session ->
            val updatedSession = session.copy(endTime = System.currentTimeMillis())
            saveSession(updatedSession)
            currentSession = null
        }
        
        updateUI()
        Toast.makeText(this, "Data collection stopped", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleVSSData(vssData: VSSVehicle) {
        currentSession?.samples?.add(vssData)
        
        runOnUiThread {
            dataText.text = """
                Speed: ${String.format("%.2f", vssData.speed.value)} km/h
                Position: ${String.format("%.6f", vssData.position.latitude)}, ${String.format("%.6f", vssData.position.longitude)}
                Acceleration:
                  Longitudinal: ${String.format("%.2f", vssData.acceleration.longitudinal)} m/s²
                  Lateral: ${String.format("%.2f", vssData.acceleration.lateral)} m/s²
                  Vertical: ${String.format("%.2f", vssData.acceleration.vertical)} m/s²
                Angular Velocity:
                  Roll: ${String.format("%.2f", vssData.angularVelocity.roll)}°/s
                  Pitch: ${String.format("%.2f", vssData.angularVelocity.pitch)}°/s
                  Yaw: ${String.format("%.2f", vssData.angularVelocity.yaw)}°/s
                Samples: ${currentSession?.samples?.size ?: 0}
            """.trimIndent()
        }
    }
    
    private fun saveSession(session: VSSDataSession) {
        try {
            val fileName = "vss_session_${session.sessionId}.json"
            val file = File(getExternalFilesDir(null), fileName)
            file.writeText(gson.toJson(session))
            
            Toast.makeText(
                this,
                "Session saved: ${session.samples.size} samples",
                Toast.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving session: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun updateUI() {
        statusText.text = if (isCollecting) {
            "Status: Collecting Data"
        } else {
            "Status: Ready"
        }
        
        toggleButton.text = if (isCollecting) {
            "Stop Collection"
        } else {
            "Start Collection"
        }
        
        if (!isCollecting && currentSession == null) {
            dataText.text = "Press Start to begin data collection\n\nThis app collects vehicle dynamics data using your smartphone sensors and follows COVESA Vehicle Signal Specification (VSS) standards."
        }
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }
}
