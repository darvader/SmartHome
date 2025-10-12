# GitHub Copilot Instructions - Main Overview

## Project Overview
This is an Android application called SmartHome that includes LED matrix control functionality and live sports score tracking. The project consists of two main modules:

1. **Main App Module (`app`)** - Contains the core SmartHome functionality with LED matrix activities
2. **LiveScore Module (`livescore`)** - A library module that provides LiveScore functionality to be used by other modules

## Instruction Files Organization
Instructions are split into topic-specific files for better organization:

- [`architecture.md`](.github/architecture.md) - Project structure and architecture patterns
- [`coding-standards.md`](.github/coding-standards.md) - Code style guidelines and best practices
- [`threading.md`](.github/threading.md) - Threading and UI update patterns
- [`websocket.md`](.github/websocket.md) - WebSocket integration and real-time updates
- [`debugging.md`](.github/debugging.md) - Common issues and debugging techniques
- [`dependencies.md`](.github/dependencies.md) - Module dependencies and technology stack

## PowerShell Guidelines
- **Command Separation**: Always use semicolons (`;`) to separate multiple PowerShell commands on the same line
- **Example**: `cd "D:\dev\AndroidStudioProjects\SmartHome"; gradlew build` instead of `cd "D:\dev\AndroidStudioProjects\SmartHome" && gradlew build`

## Quick Reference
- **Target SDK**: 33, Min SDK: 26
- **Language**: Kotlin
- **Key Technologies**: WebSocket, CameraX, View Binding, Jetpack Compose
- **Module Structure**: Main app + LiveScore library module

See individual topic files for detailed guidance on each area.
