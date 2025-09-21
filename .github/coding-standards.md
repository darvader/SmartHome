# Coding Standards and Best Practices

## Kotlin Style Guidelines

### Variable and Property Management
- **Companion Object Variables**: Use for cross-activity communication
  ```kotlin
  companion object {
      var scoreboardActivity: ScoreboardActivity? = null
      var match: Match? = null
  }
  ```
- **Property Visibility**: Make properties accessible when needed across activities
  ```kotlin
  // Instead of private if used by other activities
  lateinit var webSocketManager: LiveScoreWebSocketManager
  var selectedRegion: String = URL_TVV
  ```

### Common Kotlin Issues to Avoid
1. **Redundant Type Conversions**
   ```kotlin
   // Wrong - redundant toInt()
   if (lastSet.team2.toInt() == 8)
   
   // Correct - team2 is already Int
   if (lastSet.team2 == 8)
   ```

2. **Unnecessary Semicolons**
   ```kotlin
   // Wrong - Java style
   timer?.cancel();
   
   // Correct - Kotlin style
   timer?.cancel()
   ```

### Method References and Scope
- **Companion Object Access**: Use explicit `Companion.` prefix for clarity
  ```kotlin
  // Clear and explicit
  Companion.match = selectedMatch
  
  // In methods, reference companion variables explicitly
  Companion.match?.matchSets?.forEach { ... }
  ```

### Service Initialization Patterns
- **Service Setup**: Understand service interfaces before calling methods
  ```kotlin
  // Wrong - assuming setListener exists
  matchDataService.setListener(this)
  
  // Correct - check service interface first
  matchDataService.fetchMatchData(region, this) // Pass listener directly
  ```

### Error Handling
- Always wrap service calls in try-catch blocks
- Log errors with meaningful context
- Provide user feedback for network errors

## View Binding Best Practices
- Use view binding consistently across all activities
- Initialize binding in onCreate() before setContentView()
- Access views through binding object: `binding.buttonName`

## Activity Lifecycle Management
- Clean up resources in onDestroy()
- Cancel timers and close WebSocket connections
- Null out static references to prevent memory leaks
