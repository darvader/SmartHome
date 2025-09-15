# GitHub Copilot Instructions for SmartHome Android Project

## Project Overview
This is an Android application called SmartHome that includes LED matrix control functionality and live sports score tracking. The project consists of two main modules:

1. **Main App Module (`app`)** - Contains the core SmartHome functionality with LED matrix activities
2. **LiveScore Module (`livescore`)** - A library module that provides LiveScore functionality to be used by other modules

## Project Structure

### Main App Module
- **Package**: `com.darvader.smarthome`
- **Key Activities**:
  - `LiveScoreActivity` - Displays live sports scores with WebSocket connectivity
  - `ScoreboardActivity` - Manual scoreboard control for LED matrix
  - `LedMatrixActivity` - Main LED matrix control interface
  - Other matrix activities (Timer, Counter, Animations, etc.)

### LiveScore Module
- **Package**: `com.darvader.livescore`
- **Purpose**: Library module that provides LiveScore and Scoreboard functionality
- **Dependencies**: Uses api dependency on the main app module to expose shared activities

## Key Technologies & Dependencies
- **Language**: Kotlin
- **UI**: Android View Binding, Jetpack Compose
- **WebSocket**: `org.java-websocket:Java-WebSocket:1.5.3`
- **HTTP Client**: `com.squareup.okhttp3:okhttp:4.11.0`
- **JSON Parsing**: `com.squareup.moshi:moshi:1.15.0`
- **Camera**: CameraX libraries
- **Target SDK**: 33, Min SDK: 26

## Code Style Guidelines
1. Use Kotlin coroutines for asynchronous operations
2. Follow Android architecture components patterns
3. Use view binding for UI interactions
4. Implement proper error handling for network operations
5. Use companion objects for constants and static references

## Important Classes & Patterns
- **LedMatrix.kt** - Core LED matrix control logic
- **Match.kt, League.kt, MatchSet.kt** - Data models for sports scores
- **WebSocket Integration** - Real-time score updates from backend
- **Activity Communication** - Activities communicate through companion object references

## Module Dependencies
The livescore module depends on the main app module using:
```gradle
api project(':app')
```

## Network Configuration
- DVV Backend: `https://backend.sams-ticker.de/live/tickers/dvv`
- TVV Backend: `https://backend.sams-ticker.de/live/tickers/tvv`
- WebSocket endpoints for real-time updates

## Development Notes
- LiveScoreActivity uses static references for communication with ScoreboardActivity
- WebSocket connections are managed through companion objects
- LED matrix operations are centralized in the LedMatrix class
- The livescore module acts as a library that exposes LiveScore functionality

## PowerShell Commands
When working with PowerShell commands, use semicolon (;) to concatenate multiple commands:
```powershell
cd "project-directory"; .\gradlew build
```

## When Contributing
1. Maintain module separation - don't copy code between modules, use dependencies
2. Keep network operations on background threads
3. Handle WebSocket connection lifecycle properly
4. Follow existing naming conventions for activities and packages
5. Update both modules when making changes to shared activities
6. Use semicolon (;) to chain PowerShell commands instead of && which is for bash
