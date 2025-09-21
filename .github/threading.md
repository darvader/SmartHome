# Threading and UI Update Guidelines

## Critical Threading Rules

### WebSocket Updates and UI Threading
**Problem**: WebSocket callbacks execute on background threads, but UI updates must happen on the main thread.

**Solution**: Always wrap UI updates in `runOnUiThread`:
```kotlin
override fun onMatchUpdated(match: Match) {
    if (Companion.match?.id == match.id) {
        runOnUiThread {
            showSets() // UI update must be on main thread
        }
        scoreboardActivity?.inform() // Non-UI operations can stay on background thread
    }
}
```

### Common Threading Scenarios

1. **WebSocket Message Processing**
   - WebSocket callbacks run on background threads
   - Any UI updates (TextView, ImageView, etc.) need `runOnUiThread`
   - Non-UI operations (data processing, logging) can remain on background thread

2. **HTTP Response Handling**
   - Network callbacks often run on background threads
   - Adapter updates, view visibility changes need main thread
   - Always check which thread you're on when updating UI

3. **Timer and Background Tasks**
   ```kotlin
   timer = Timer("informer", true).schedule(1000, 1000) {
       runOnUiThread {
           ledMatrix.updateScore() // UI-related updates
       }
   }
   ```

### UI Update Patterns
```kotlin
// Correct pattern for background thread UI updates
private fun updateUIFromBackground(data: String) {
    runOnUiThread {
        binding.textView.text = data
        binding.progressBar.visibility = View.GONE
    }
}

// Adapter updates also need main thread
private fun updateSpinner(items: List<String>) {
    runOnUiThread {
        val adapter = ArrayAdapter(this@ActivityName, layout, items)
        binding.spinner.adapter = adapter
    }
}
```

### Thread Safety Best Practices
- Use `runOnUiThread` for all View updates from background threads
- Avoid accessing UI components directly from WebSocket/network callbacks
- Consider using coroutines with `Dispatchers.Main` for complex async operations
- Always test real-time features to ensure UI updates work correctly
