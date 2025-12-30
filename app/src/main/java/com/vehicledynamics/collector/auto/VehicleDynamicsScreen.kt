package com.vehicledynamics.collector.auto

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.*

/**
 * Android Auto screen for vehicle dynamics data collection
 */
class VehicleDynamicsScreen(carContext: CarContext) : Screen(carContext) {

    private var isCollecting = false
    
    override fun onGetTemplate(): Template {
        return PaneTemplate.Builder(
            Pane.Builder()
                .addRow(
                    Row.Builder()
                        .setTitle("Vehicle Dynamics Collector")
                        .addText("Control your data collection campaign")
                        .build()
                )
                .addRow(
                    Row.Builder()
                        .setTitle("Status")
                        .addText(if (isCollecting) "Collecting data..." else "Ready to collect")
                        .build()
                )
                .addAction(
                    Action.Builder()
                        .setTitle(if (isCollecting) "Stop Collection" else "Start Collection")
                        .setOnClickListener { toggleCollection() }
                        .build()
                )
                .build()
        )
            .setHeaderAction(Action.APP_ICON)
            .setTitle("Vehicle Dynamics")
            .build()
    }
    
    private fun toggleCollection() {
        isCollecting = !isCollecting
        
        // Send broadcast to main app to start/stop collection
        val intent = android.content.Intent("com.vehicledynamics.collector.TOGGLE_COLLECTION")
        intent.putExtra("collecting", isCollecting)
        carContext.sendBroadcast(intent)
        
        // Refresh the screen
        invalidate()
    }
}
