# Dependencies and Technology Stack

## Module Dependencies

### Main App Module (`app`)
```gradle
dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'com.google.android.material:material:1.8.0'
    
    // WebSocket support
    implementation 'org.java-websocket:Java-WebSocket:1.5.3'
    
    // JSON processing
    implementation 'org.json:json:20210307'
    
    // HTTP client
    implementation 'com.squareup.okhttp3:okhttp:4.10.0'
    
    // View Binding (enabled in build.gradle)
    viewBinding true
    
    // CameraX dependencies
    implementation "androidx.camera:camera-core:1.2.2"
    implementation "androidx.camera:camera-camera2:1.2.2"
    implementation "androidx.camera:camera-lifecycle:1.2.2"
    implementation "androidx.camera:camera-view:1.2.2"
}
```

### LiveScore Module (`livescore`)
```gradle
dependencies {
    // Expose main app functionality
    api project(':app')
    
    // Core Android libraries
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.core:core-ktx:1.9.0'
}
```

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 33 (Android 13)
- **Build System**: Gradle with Kotlin DSL

### Networking
- **HTTP Client**: OkHttp3 for REST API calls
- **WebSocket**: Java-WebSocket library for real-time updates
- **Backend URLs**:
  - DVV: `https://backend.sams-ticker.de/live/indoor/tickers/dvv`
  - TVV: `https://backend.sams-ticker.de/live/indoor/tickers/tvv`
  - WebSocket DVV: `wss://backend.sams-ticker.de/dvv`
  - WebSocket TVV: `wss://backend.sams-ticker.de/tvv`

### UI Framework
- **View Binding**: Modern view access pattern
- **Material Design**: Google's design system
- **Custom LED Matrix UI**: Hardware-specific control interfaces

### Hardware Integration
- **LED Matrix Control**: Custom TCP/UDP communication
- **WiFi Hotspot**: Programmatic hotspot creation and management
- **Camera**: CameraX for camera functionality
- **Smart Home Devices**: Custom protocol for device control

### Architecture Components
- **Activities**: Traditional Android activity-based navigation
- **Services**: Background processing for network operations
- **Companion Objects**: Cross-activity communication pattern
- **Listeners/Callbacks**: Event-driven architecture for real-time updates

### Development Tools
- **Android Studio**: Primary IDE
- **Gradle**: Build automation
- **ProGuard**: Code obfuscation and optimization

## Permission Requirements
```xml
<!-- Network permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />

<!-- WiFi hotspot permissions -->
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.WRITE_SETTINGS" />

<!-- Storage permissions -->
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

<!-- Camera permission -->
<uses-permission android:name="android.permission.CAMERA" />
```

## Third-party Libraries
- **OkHttp**: HTTP client for REST API calls
- **Java-WebSocket**: WebSocket client implementation
- **JSON**: JSON parsing and manipulation
- **CameraX**: Modern camera API
