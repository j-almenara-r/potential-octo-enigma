# Getting Started with Vehicle Dynamics Collector

This guide will help you get the Vehicle Dynamics Collector app up and running.

## Prerequisites

Before you begin, ensure you have the following installed:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from: https://adoptium.net/
   - Verify installation: `java -version`

2. **Android Studio**
   - Download from: https://developer.android.com/studio
   - Latest stable version (Hedgehog | 2023.1.1 or newer)

3. **Android SDK**
   - Android SDK 34 (included with Android Studio)
   - Can be managed via Android Studio's SDK Manager

## Quick Start

### 1. Clone the Repository

```bash
git clone https://github.com/j-almenara-r/potential-octo-enigma.git
cd potential-octo-enigma
```

### 2. Open in Android Studio

1. Launch Android Studio
2. Click "Open" (not "New Project")
3. Navigate to the cloned repository folder
4. Click "OK"

Android Studio will automatically:
- Sync Gradle dependencies
- Index the project
- Download required Android SDK components

### 3. Connect an Android Device

#### Option A: Physical Device
1. Enable Developer Options on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect your device via USB
4. Accept the USB debugging prompt on your device

#### Option B: Android Emulator
1. In Android Studio, click Tools → Device Manager
2. Click "Create Device"
3. Select a device definition (e.g., Pixel 6)
4. Select a system image (API 34 recommended)
5. Click "Finish"

### 4. Build and Run

#### Using Android Studio
1. Click the green "Run" button (▶) in the toolbar
2. Select your device/emulator
3. Wait for the build to complete
4. The app will launch automatically

#### Using Command Line
```bash
# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Or build and install in one step
./gradlew installDebug
```

The built APK will be located at:
```
app/build/outputs/apk/debug/app-debug.apk
```

## Testing the App

### Basic Functionality Test

1. **Launch the app**
   - You should see the main screen with "Vehicle Dynamics Collector" title
   
2. **Grant permissions**
   - When prompted, allow location permissions
   - These are required for GPS data collection

3. **Start data collection**
   - Tap "Start Collection" button
   - Status should change to "Status: Collecting Data"
   - Real-time sensor data should appear on screen

4. **Observe data**
   - Speed (from GPS)
   - Position (latitude/longitude)
   - Acceleration (3-axis)
   - Angular velocity (roll/pitch/yaw)
   - Sample count

5. **Stop collection**
   - Tap "Stop Collection"
   - Session data is automatically saved to device storage

### Testing Android Auto Integration

To test Android Auto functionality, you have two options:

#### Option 1: Desktop Head Unit (DHU) - Recommended for Testing

1. **Install Desktop Head Unit**
   ```bash
   # Install via Android SDK Manager in Android Studio
   # Or download directly
   sdkmanager --install "extras;google;desktop_head_unit"
   ```

2. **Enable Developer Mode in Android Auto**
   - On your Android device, install Android Auto from Google Play
   - Open Android Auto app
   - Tap the hamburger menu 10 times to enable developer mode
   - Go to Settings (three dots) → Developer settings
   - Enable "Unknown sources" and "Developer mode"

3. **Connect and Run DHU**
   ```bash
   # Connect device via USB with USB debugging enabled
   adb forward tcp:5277 tcp:5277
   
   # Launch DHU (path may vary)
   ~/Android/Sdk/extras/google/desktop_head_unit/desktop-head-unit
   ```

4. **Launch the app**
   - In DHU, navigate to the app drawer
   - Select "Vehicle Dynamics Collector"
   - You should see the Android Auto interface
   - Test start/stop collection buttons

#### Option 2: Physical Android Auto Head Unit

1. Connect your device to an Android Auto compatible car head unit
2. Enable unknown sources in Android Auto developer settings (as above)
3. Launch "Vehicle Dynamics Collector" from the Android Auto interface
4. Use the head unit controls to manage data collection

## Viewing Collected Data

Data is saved as JSON files in the app's external storage directory:

```bash
# Pull data from device using ADB
adb pull /sdcard/Android/data/com.vehicledynamics.collector/files/

# Files are named: vss_session_<uuid>.json
```

You can view these files with any text editor or JSON viewer.

## Troubleshooting

### Build Failures

**"SDK location not found"**
- Solution: Create `local.properties` file with:
  ```
  sdk.dir=/path/to/Android/Sdk
  ```
  (Android Studio usually creates this automatically)

**"Plugin with id 'com.android.application' not found"**
- Solution: Ensure you're using Android Studio (not just IntelliJ IDEA)
- Verify Android SDK is properly installed

### Runtime Issues

**App crashes immediately**
- Check logcat in Android Studio: View → Tool Windows → Logcat
- Look for stack traces indicating the issue

**No GPS data**
- Ensure location permissions are granted
- Test outdoors or near a window for better GPS signal
- On emulator: Use emulator controls to set location

**Android Auto not showing**
- Verify Android Auto app is installed on device
- Enable developer mode and unknown sources
- Check that device is connected properly to DHU/head unit

### Permission Issues

**Location permission denied**
- Go to device Settings → Apps → Vehicle Dynamics Collector → Permissions
- Manually enable Location permission

## Next Steps

Once you have the basic app running:

1. **Collect real-world data**: Take the device in a vehicle and collect driving data
2. **Analyze the data**: Use the JSON files for analysis with your preferred tools
3. **Customize the app**: Modify sensor sampling rates, add new VSS signals, etc.
4. **Explore COVESA VSS**: Learn more about the standard at https://covesa.github.io/vehicle_signal_specification/

## Getting Help

If you encounter issues:
1. Check the README.md for detailed documentation
2. Review the code comments for implementation details
3. Open an issue on GitHub with:
   - Description of the problem
   - Steps to reproduce
   - Logcat output (if applicable)
   - Device/emulator information

## Development Tips

- **Enable Auto Import**: Android Studio → Settings → Editor → General → Auto Import
- **Use Logcat filters**: Create filters for "com.vehicledynamics.collector" package
- **Instant Run**: Use for faster development iterations (may need to be enabled in settings)
- **Code inspection**: Analyze → Inspect Code for potential issues

Happy coding! 🚗📱
