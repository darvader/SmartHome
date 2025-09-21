# Debugging and Common Issues

## Recent Issues and Solutions

### Compilation Errors Fixed

#### 1. Unresolved Method References
**Problem**: Calling non-existent methods on services
```kotlin
// Wrong - setListener doesn't exist on MatchDataService
matchDataService.setListener(this)
```
**Solution**: Check service interface before using
```kotlin
// Correct - MatchDataService uses listener in method parameter
matchDataService.fetchMatchData(selectedRegion, this)
```

#### 2. Variable Scope Issues
**Problem**: Accessing wrong variable scope
```kotlin
// Wrong - accessing local variable instead of companion object
if (this.match?.id == match.id)
```
**Solution**: Use explicit companion object reference
```kotlin
// Correct
if (Companion.match?.id == match.id)
```

#### 3. Property Accessibility
**Problem**: Private properties can't be accessed from other activities
```kotlin
// Wrong - private properties inaccessible
private lateinit var webSocketManager: LiveScoreWebSocketManager
private var selectedRegion: String = URL_TVV
```
**Solution**: Make properties accessible when needed
```kotlin
// Correct - accessible from other activities
lateinit var webSocketManager: LiveScoreWebSocketManager
var selectedRegion: String = URL_TVV
```

### UI Update Issues

#### WebSocket Updates Not Showing
**Problem**: UI updates from WebSocket callbacks not appearing
**Root Cause**: WebSocket callbacks run on background threads
**Solution**: Wrap UI updates in `runOnUiThread`

#### Companion Object Variables Not Updated
**Problem**: Match selection not persisting across activities
**Root Cause**: Assigning to wrong variable scope
**Solution**: Use `Companion.match =` instead of local assignment

### Debugging Techniques

#### 1. Thread Debugging
```kotlin
// Add logging to identify thread issues
Log.d(TAG, "Current thread: ${Thread.currentThread().name}")
Log.d(TAG, "Is main thread: ${Looper.myLooper() == Looper.getMainLooper()}")
```

#### 2. WebSocket Connection Status
```kotlin
// Log WebSocket state changes
override fun onWebSocketConnected() {
    Log.d(TAG, "WebSocket connected to: $currentWebSocketUrl")
}

override fun onWebSocketError(error: String) {
    Log.e(TAG, "WebSocket error: $error")
}
```

#### 3. Variable State Tracking
```kotlin
// Log companion object state
Log.d(TAG, "Current match ID: ${Companion.match?.id}")
Log.d(TAG, "Selected league: ${selectedLeague?.name}")
```

### Performance Issues
- Timer cleanup in onDestroy() to prevent memory leaks
- WebSocket disconnection to free resources
- Null out static references when activities are destroyed

### Testing Real-time Features
1. Test WebSocket connection with actual backend
2. Verify UI updates appear immediately
3. Test region switching functionality
4. Verify LED matrix updates with live data
5. Test activity communication across the app
