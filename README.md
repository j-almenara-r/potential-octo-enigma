# Vehicle Dynamics Collector

An Android application with Android Auto capabilities for vehicle dynamics data collection and analysis using smartphone sensors.

## Overview

This application leverages smartphone sensors to collect vehicle dynamics data following the [COVESA Vehicle Signal Specification (VSS)](https://github.com/COVESA/vehicle_signal_specification) standard. It provides both a standalone Android app and Android Auto integration for safe data collection while driving.

## Features

### Core Capabilities
- **Sensor Data Collection**: Utilizes accelerometer, gyroscope, and GPS sensors
- **Android Auto Integration**: Control data collection campaigns safely from your vehicle's head unit
- **COVESA VSS Compliance**: Data structure follows Vehicle Signal Specification standards
- **Real-time Monitoring**: View live sensor data during collection
- **Session Management**: Automatic saving of data collection sessions in JSON format

### Collected Data (VSS Format)
- **Vehicle.Speed**: Speed in km/h from GPS
- **Vehicle.Acceleration**: Longitudinal, lateral, and vertical acceleration (m/s²)
- **Vehicle.CurrentLocation**: Latitude, longitude, altitude, heading, and speed
- **Vehicle.AngularVelocity**: Roll, pitch, and yaw rates (degrees/s)

## Requirements

- Android 9.0 (API level 28) or higher
- Android Studio Hedgehog | 2023.1.1 or newer
- Android SDK 34
- Location permissions for GPS data
- Android Auto compatible head unit (optional, for in-vehicle use)

## Building the Project

### Prerequisites

1. Install Android Studio from [developer.android.com](https://developer.android.com/studio)
2. Ensure you have Android SDK 34 installed
3. Clone this repository:
   ```bash
   git clone https://github.com/j-almenara-r/potential-octo-enigma.git
   cd potential-octo-enigma
   ```

### Build Steps

1. **Open the project in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository directory

2. **Sync Gradle**
   - Android Studio should automatically sync Gradle dependencies
   - If not, click "File" → "Sync Project with Gradle Files"

3. **Build the APK**
   ```bash
   # Using Gradle wrapper (from project root)
   ./gradlew assembleDebug
   
   # Or for release build
   ./gradlew assembleRelease
   ```
   
   The APK will be generated in: `app/build/outputs/apk/debug/app-debug.apk`

4. **Install on Device**
   ```bash
   # Via ADB
   adb install app/build/outputs/apk/debug/app-debug.apk
   
   # Or use Android Studio's Run button (green triangle)
   ```

## Usage

### Standalone Mode
1. Launch the app on your Android device
2. Grant location permissions when prompted
3. Tap "Start Collection" to begin recording sensor data
4. Tap "Stop Collection" to end the session
5. Data is automatically saved to device storage in JSON format

### Android Auto Mode
1. Connect your phone to an Android Auto compatible head unit
2. Launch "Vehicle Dynamics Collector" from Android Auto
3. Use the on-screen controls to start/stop data collection
4. Data collection continues even when viewing other Android Auto apps

## Data Format

Collected data follows the COVESA VSS specification and is saved in JSON format:

```json
{
  "sessionId": "uuid",
  "startTime": 1234567890,
  "endTime": 1234567900,
  "samples": [
    {
      "speed": {
        "value": 60.5,
        "unit": "km/h"
      },
      "acceleration": {
        "longitudinal": 2.3,
        "lateral": 0.1,
        "vertical": -9.8,
        "unit": "m/s^2"
      },
      "position": {
        "latitude": 37.7749,
        "longitude": -122.4194,
        "altitude": 10.0,
        "heading": 90.0,
        "speed": 16.8
      },
      "angularVelocity": {
        "roll": 0.5,
        "pitch": 1.2,
        "yaw": 0.3,
        "unit": "degrees/s"
      },
      "timestamp": 1234567890123
    }
  ]
}
```

## Architecture

### Project Structure
```
app/
├── src/main/
│   ├── java/com/vehicledynamics/collector/
│   │   ├── MainActivity.kt              # Main application activity
│   │   ├── auto/
│   │   │   ├── VehicleDynamicsCarAppService.kt  # Android Auto service
│   │   │   └── VehicleDynamicsScreen.kt         # Android Auto UI
│   │   ├── sensors/
│   │   │   └── SensorDataCollector.kt   # Sensor management
│   │   └── vss/
│   │       └── VSSDataModel.kt          # COVESA VSS data models
│   ├── res/
│   │   ├── layout/
│   │   │   └── activity_main.xml        # Main UI layout
│   │   ├── values/
│   │   │   └── strings.xml              # String resources
│   │   └── xml/
│   │       └── automotive_app_desc.xml  # Android Auto descriptor
│   └── AndroidManifest.xml              # App manifest with permissions
└── build.gradle                         # App dependencies
```

### Key Components

1. **VSSDataModel**: Data classes following COVESA Vehicle Signal Specification
2. **SensorDataCollector**: Manages accelerometer, gyroscope, and GPS sensors
3. **MainActivity**: Main UI for data collection control and monitoring
4. **VehicleDynamicsCarAppService**: Android Auto service integration
5. **VehicleDynamicsScreen**: Android Auto UI implementation

## COVESA VSS Integration

This application implements a subset of the [COVESA Vehicle Signal Specification](https://github.com/COVESA/vehicle_signal_specification):

- Follows VSS naming conventions (Vehicle.Speed, Vehicle.Acceleration, etc.)
- Uses VSS standard units (km/h, m/s², degrees/s)
- Structured data format compatible with VSS tools
- Extensible architecture for additional VSS signals

## Permissions

The app requires the following permissions:
- `ACCESS_FINE_LOCATION`: For GPS data collection
- `ACCESS_COARSE_LOCATION`: For approximate location data
- `INTERNET`: For potential future cloud integration
- `WRITE_EXTERNAL_STORAGE`: For saving data sessions (Android 9-10)

## Future Enhancements

Potential improvements for future versions:
- Cloud synchronization of collected data
- Advanced data visualization and analysis
- Integration with vehicle OBD-II systems
- Machine learning for driving behavior analysis
- Support for additional VSS signals
- Export to standard formats (CSV, Parquet)

## Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

## License

See [LICENSE](LICENSE) file for details.

## References

- [COVESA Vehicle Signal Specification](https://github.com/COVESA/vehicle_signal_specification)
- [Android Auto Developer Guide](https://developer.android.com/training/cars)
- [Android Sensor Documentation](https://developer.android.com/guide/topics/sensors/sensors_overview)