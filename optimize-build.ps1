# SmartHome Android Build Performance Optimization Script
# Run this script to apply additional performance improvements

# 1. Clean build caches and restart Gradle daemon for fresh start
Write-Host "Cleaning build caches..." -ForegroundColor Green
Remove-Item -Recurse -Force "D:\dev\AndroidStudioProjects\SmartHome\.gradle\caches" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "D:\dev\AndroidStudioProjects\SmartHome\build" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "D:\dev\AndroidStudioProjects\SmartHome\app\build" -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force "D:\dev\AndroidStudioProjects\SmartHome\livescore\build" -ErrorAction SilentlyContinue

# 2. Stop existing Gradle daemons
Write-Host "Stopping Gradle daemons..." -ForegroundColor Green
cd "D:\dev\AndroidStudioProjects\SmartHome"
.\gradlew --stop

# 3. Run optimized build with parallel processing
Write-Host "Running optimized build..." -ForegroundColor Green
.\gradlew clean assembleDebug --parallel --build-cache --configuration-cache --info

Write-Host "Build optimization complete!" -ForegroundColor Yellow
Write-Host "Your Android builds should now be significantly faster." -ForegroundColor Green
