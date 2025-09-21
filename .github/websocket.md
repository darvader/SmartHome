# WebSocket Integration Guidelines

## WebSocket Architecture

### Service Structure
- **LiveScoreWebSocketManager**: Handles WebSocket connections
- **WebSocketListener Interface**: Defines callback methods for WebSocket events
- **Connection Management**: Per-region WebSocket URLs (DVV, TVV)

### WebSocket URLs
```kotlin
const val WEB_SOCKET_URL_DVV = "wss://backend.sams-ticker.de/dvv"
const val WEB_SOCKET_URL_TVV = "wss://backend.sams-ticker.de/tvv"
```

### Implementation Pattern
```kotlin
class LiveScoreActivity : LiveScoreWebSocketManager.WebSocketListener {
    
    // Initialize WebSocket manager
    private fun initializeServices() {
        webSocketManager = LiveScoreWebSocketManager()
        webSocketManager.setListener(this)
    }
    
    // Handle region changes
    private fun switchRegion(region: String) {
        webSocketManager.disconnect()
        webSocketManager.connect(region)
    }
    
    // WebSocket callbacks
    override fun onMatchUpdate(payload: JSONObject) {
        matchManager.updateMatch(payload) // Process data
    }
    
    override fun onWebSocketConnected() {
        Log.d(TAG, "WebSocket connected")
    }
    
    override fun onWebSocketDisconnected() {
        Log.d(TAG, "WebSocket disconnected")
    }
    
    override fun onWebSocketError(error: String) {
        Log.e(TAG, "WebSocket error: $error")
    }
}
```

### Real-time Update Flow
1. WebSocket receives match update message
2. `onMatchUpdate(payload)` called on background thread
3. MatchManager processes payload and updates match data
4. MatchManager calls `onMatchUpdated(match)` listener
5. UI updates via `runOnUiThread` for thread safety

### Connection Management
- **Automatic Reconnection**: Handle in onWebSocketDisconnected
- **Region Switching**: Disconnect before connecting to new region
- **Lifecycle Management**: Disconnect in onDestroy()
- **Error Handling**: Retry logic for connection failures

### Cross-Activity Communication
```kotlin
// Reconnect from ScoreboardActivity
binding.reconnect.setOnClickListener {
    LiveScoreActivity.livescoreActivity?.let { activity ->
        activity.webSocketManager.disconnect()
        activity.webSocketManager.connect(activity.selectedRegion)
    }
}
```

### Best Practices
- Always disconnect WebSocket in activity onDestroy()
- Handle connection state changes gracefully
- Provide user feedback for connection status
- Test with real WebSocket server for accurate behavior
- Log WebSocket events for debugging purposes
