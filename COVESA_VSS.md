# COVESA Vehicle Signal Specification Integration

## Overview

This document explains how the Vehicle Dynamics Collector app implements concepts from the [COVESA Vehicle Signal Specification (VSS)](https://github.com/COVESA/vehicle_signal_specification).

## What is COVESA VSS?

The COVESA Vehicle Signal Specification is a standard for defining vehicle signals and data in a structured, hierarchical manner. It's part of the broader Software-Defined Vehicle (SDV) initiative, which aims to:

- Create standardized vehicle data models
- Enable interoperability between different automotive systems
- Facilitate data exchange between vehicles, cloud services, and applications
- Support the evolution toward software-defined vehicles

## VSS Implementation in This App

### Data Model Structure

The app implements VSS-compliant data structures in `VSSDataModel.kt`:

```kotlin
VSSVehicle
├── speed (Vehicle.Speed)
├── acceleration (Vehicle.Acceleration)
├── position (Vehicle.CurrentLocation)
├── angularVelocity (Vehicle.AngularVelocity)
└── timestamp
```

### VSS Signals Implemented

#### 1. Vehicle.Speed
- **Type**: Sensor
- **Unit**: km/h (kilometers per hour)
- **Source**: GPS location data
- **Description**: Vehicle speed calculated from GPS

```json
{
  "value": 60.5,
  "unit": "km/h"
}
```

#### 2. Vehicle.Acceleration
- **Type**: Sensor
- **Unit**: m/s² (meters per second squared)
- **Source**: Smartphone accelerometer
- **Components**:
  - `longitudinal`: Forward/backward acceleration
  - `lateral`: Left/right acceleration
  - `vertical`: Up/down acceleration

```json
{
  "longitudinal": 2.3,
  "lateral": 0.1,
  "vertical": -9.8,
  "unit": "m/s^2"
}
```

**Note**: The coordinate system is converted from device coordinates to vehicle coordinates:
- Device X-axis → Vehicle lateral
- Device Y-axis → Vehicle longitudinal
- Device Z-axis → Vehicle vertical

#### 3. Vehicle.CurrentLocation
- **Type**: Sensor
- **Source**: GPS
- **Components**:
  - `latitude`: Geographic latitude (decimal degrees)
  - `longitude`: Geographic longitude (decimal degrees)
  - `altitude`: Height above sea level (meters)
  - `heading`: Direction of travel (0-360 degrees)
  - `speed`: GPS-measured speed (m/s)

```json
{
  "latitude": 37.7749,
  "longitude": -122.4194,
  "altitude": 10.0,
  "heading": 90.0,
  "speed": 16.8
}
```

#### 4. Vehicle.AngularVelocity
- **Type**: Sensor
- **Unit**: degrees/s
- **Source**: Smartphone gyroscope
- **Components**:
  - `roll`: Rotation around longitudinal axis
  - `pitch`: Rotation around lateral axis
  - `yaw`: Rotation around vertical axis

```json
{
  "roll": 0.5,
  "pitch": 1.2,
  "yaw": 0.3,
  "unit": "degrees/s"
}
```

## VSS Naming Conventions

The app follows VSS naming conventions:

1. **Hierarchical structure**: Signals are organized in a tree structure (Vehicle.* hierarchy)
2. **Camel case**: Property names use camelCase (e.g., `angularVelocity`)
3. **Standard units**: Uses SI units and common automotive units
4. **Metadata**: Each signal includes its unit of measurement

## Data Collection Sessions

The app extends VSS with a session concept for data collection campaigns:

```kotlin
VSSDataSession {
  sessionId: String          // Unique identifier
  startTime: Long           // Unix timestamp (ms)
  endTime: Long?            // Unix timestamp (ms)
  samples: List<VSSVehicle> // Time-series data
  metadata: Map             // Custom session metadata
}
```

## Future VSS Extensions

The current implementation can be extended to include additional VSS signals:

### Powertrain Signals
- `Vehicle.Powertrain.FuelSystem.Level`
- `Vehicle.Powertrain.Range`
- `Vehicle.Powertrain.Type` (electric, hybrid, combustion)

### Chassis Signals
- `Vehicle.Chassis.SteeringWheel.Angle`
- `Vehicle.Chassis.Brake.PedalPosition`
- `Vehicle.Chassis.Accelerator.PedalPosition`

### ADAS Signals
- `Vehicle.ADAS.CruiseControl.IsActive`
- `Vehicle.ADAS.LaneDepartureWarning.IsActive`

### Body Signals
- `Vehicle.Body.Lights.IsHighBeamOn`
- `Vehicle.Body.Windshield.Wipers.Mode`

### Cabin Signals
- `Vehicle.Cabin.Infotainment.Media.Volume`
- `Vehicle.Cabin.HVAC.Station.Temperature`

## Integration with VSS Ecosystem

### Data Export

The JSON format used by this app can be easily converted to standard VSS formats:

1. **VSS JSON**: Already compatible
2. **VSS CSV**: Can be flattened for time-series analysis
3. **VSS Parquet**: For big data analytics
4. **VSS Protocol Buffers**: For efficient transmission

### VSS Tools Compatibility

The data structure is designed to be compatible with COVESA VSS tools:

- **VSS Tools**: Python tools for VSS specification processing
- **KUKSA.val**: VSS databroker for in-vehicle applications
- **VSS Data Expert**: VSS visualization and analysis tools

### Example Integration with KUKSA.val

```python
# Example: Converting app data to KUKSA.val format
import json
from kuksa_client import KuksaClientThread

# Load session data
with open('vss_session_uuid.json', 'r') as f:
    session = json.load(f)

# Connect to KUKSA.val databroker
client = KuksaClientThread()
client.start()

# Push each sample to databroker
for sample in session['samples']:
    client.setValue('Vehicle.Speed', sample['speed']['value'])
    client.setValue('Vehicle.Acceleration.Longitudinal', 
                   sample['acceleration']['longitudinal'])
    # ... etc
```

## VSS Benefits for This Application

1. **Standardization**: Uses industry-standard signal definitions
2. **Interoperability**: Data can be easily shared with other VSS-compliant systems
3. **Extensibility**: Easy to add new signals following VSS hierarchy
4. **Documentation**: Well-defined signal meanings and units
5. **Tooling**: Can leverage existing VSS tools and libraries

## Software-Defined Vehicle Concepts

This app demonstrates SDV principles:

1. **Sensor abstraction**: Smartphone sensors as vehicle sensors
2. **Data standardization**: VSS-compliant data format
3. **Software flexibility**: Easy to add/modify data collection logic
4. **Cloud connectivity**: Ready for cloud data synchronization
5. **Over-the-air updates**: App can be updated without hardware changes

## Additional Resources

- [COVESA VSS GitHub](https://github.com/COVESA/vehicle_signal_specification)
- [VSS Documentation](https://covesa.github.io/vehicle_signal_specification/)
- [KUKSA.val Project](https://github.com/eclipse/kuksa.val)
- [VSS Tools](https://github.com/COVESA/vss-tools)
- [COVESA Organization](https://covesa.global/)

## Contributing VSS Signals

If you add new VSS signals to this app:

1. Check the [official VSS specification](https://github.com/COVESA/vehicle_signal_specification/tree/master/spec)
2. Use the exact signal path and naming
3. Include the correct unit of measurement
4. Document the signal source (which sensor/API)
5. Update this documentation

## Conclusion

By implementing COVESA VSS, this app:
- Follows automotive industry standards
- Enables integration with modern vehicle platforms
- Demonstrates software-defined vehicle concepts
- Provides a foundation for advanced vehicle data analytics

The VSS integration makes this not just a data collection app, but a building block for the future of connected and software-defined vehicles.
