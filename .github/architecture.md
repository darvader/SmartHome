# Project Architecture Guidelines

## Project Structure

### Main App Module (`app`)
- **Package**: `com.darvader.smarthome`
- **Key Activities**:
  - `SmartHomeActivity` - Main entry point with hotspot creation and device controls
  - `LiveScoreActivity` - Displays live sports scores with WebSocket connectivity
  - `ScoreboardActivity` - Manual scoreboard control for LED matrix
  - `LedMatrixActivity` - Main LED matrix control interface
  - Other matrix activities (Timer, Counter, Animations, etc.)

### LiveScore Module (`livescore`)
- **Package**: `com.darvader.livescore`
- **Purpose**: Library module that provides LiveScore and Scoreboard functionality
- **Dependencies**: Uses api dependency on the main app module to expose shared activities

## Module Dependencies
```gradle
// In livescore module
api project(':app')
```

## Architecture Patterns

### Activity Communication
- Activities communicate through companion object references
- Use static references for cross-activity communication
- Example: `LiveScoreActivity.scoreboardActivity` for accessing ScoreboardActivity

### Service Layer Architecture
- **MatchDataService**: Handles HTTP requests for match data
- **MatchManager**: Processes and manages match state
- **LiveScoreWebSocketManager**: Handles real-time WebSocket connections
- **LedMatrix**: Core LED matrix control logic

### Data Flow
1. MatchDataService fetches initial data via HTTP
2. MatchManager parses and stores match data
3. WebSocket provides real-time updates
4. UI components update via listener patterns
5. LED matrix receives updates through companion object references

## Key Classes & Patterns
- **LedMatrix.kt** - Core LED matrix control logic
- **Match.kt, League.kt, MatchSet.kt** - Data models for sports scores
- **WebSocket Integration** - Real-time score updates from backend
- **Activity Communication** - Activities communicate through companion object references

## Development Notes
- LiveScoreActivity uses static references for communication with ScoreboardActivity
- WebSocket connections are managed through companion objects
- LED matrix operations are centralized in the LedMatrix class
- The livescore module acts as a library that exposes LiveScore functionality
