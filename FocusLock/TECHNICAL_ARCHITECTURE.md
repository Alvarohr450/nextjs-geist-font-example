# FocusLock - Technical Architecture Documentation

## Overview

FocusLock is a comprehensive Android video editing application built using modern Android development practices. This document outlines the technical architecture, design patterns, and implementation details.

## Architecture Pattern

### MVVM (Model-View-ViewModel)

The application follows the MVVM architectural pattern to ensure separation of concerns and maintainability:

- **Model**: Data classes and business logic (`data/models/`)
- **View**: Activities, Fragments, and XML layouts (`ui/`, `res/layout/`)
- **ViewModel**: Business logic and UI state management (`viewmodels/`)

### Component Diagram

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   UI Layer      │    │  ViewModel      │    │  Data Layer     │
│                 │    │                 │    │                 │
│ • Activities    │◄──►│ • State Mgmt    │◄──►│ • Models        │
│ • Layouts       │    │ • Business      │    │ • Repositories  │
│ • Adapters      │    │   Logic         │    │ • Utils         │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

## Core Components

### 1. UI Layer

#### VideoEditorActivity
- **Purpose**: Main editing interface
- **Responsibilities**:
  - Video import/recording
  - Timeline management
  - Control panel interactions
  - Permission handling
- **Key Features**:
  - ExoPlayer integration for video preview
  - Dynamic panel loading
  - Real-time progress feedback

#### ExportActivity
- **Purpose**: Video export configuration
- **Responsibilities**:
  - Export settings management
  - Progress tracking
  - File sharing integration

### 2. ViewModel Layer

#### VideoEditorViewModel
- **State Management**:
  - Current video URI
  - Timeline clips list
  - Selected clip tracking
  - Processing status
  - Error handling
- **Business Logic**:
  - Video editing operations
  - Panel state management
  - FFmpeg command coordination

#### ExportViewModel
- **Export Configuration**:
  - Resolution settings
  - Quality parameters
  - Aspect ratio management
- **Process Management**:
  - Export progress tracking
  - Error handling
  - File output management

### 3. Data Layer

#### Models
```kotlin
data class VideoClip(
    val id: String,
    val uri: Uri,
    val startTime: Float,
    val endTime: Float,
    val name: String,
    val duration: Float,
    val isSelected: Boolean
)

data class ExportSettings(
    val resolution: String,
    val aspectRatio: String,
    val quality: Int,
    val format: String
)
```

#### Utilities
- **FFmpegUtil**: Video processing operations
- **FileUtil**: File management operations
- **PermissionUtil**: Runtime permission handling

## Video Processing Pipeline

### FFmpeg Integration

The application uses FFmpeg Kit for all video processing operations:

```kotlin
object FFmpegUtil {
    suspend fun cut(inputPath: String, startTime: Float, endTime: Float): String?
    suspend fun adjustSpeed(inputPath: String, speedMultiplier: Float): String?
    suspend fun rotate(inputPath: String, degrees: Int): String?
    suspend fun applyFilter(inputPath: String, filterName: String): String?
    suspend fun addTextOverlay(inputPath: String, text: String, ...): String?
    suspend fun mergeAudio(videoPath: String, audioPath: String, volume: Float): String?
}
```

### Processing Flow

1. **Input Validation**: Check file format and accessibility
2. **Command Generation**: Build FFmpeg command string
3. **Async Execution**: Run FFmpeg in background thread
4. **Progress Monitoring**: Track processing progress
5. **Output Handling**: Manage result files and cleanup
6. **Error Recovery**: Handle failures gracefully

## UI/UX Design System

### Material Design 3

The application implements Material Design 3 principles:

- **Color System**: Dark theme with accent colors
- **Typography**: Consistent font hierarchy
- **Components**: Material buttons, cards, sliders
- **Motion**: Smooth transitions and animations

### Theme Configuration

```xml
<style name="Theme.FocusLock" parent="Theme.Material3.DayNight.NoActionBar">
    <item name="colorPrimary">@color/primary</item>
    <item name="colorSecondary">@color/accent</item>
    <item name="android:colorBackground">@color/background</item>
</style>
```

### Layout Strategy

- **ConstraintLayout**: Primary layout container
- **RecyclerView**: Timeline and filter lists
- **ViewBinding**: Type-safe view references
- **Responsive Design**: Adaptive layouts for different screen sizes

## Performance Optimizations

### Memory Management

1. **Video Loading**:
   - Lazy loading of video thumbnails
   - Efficient bitmap caching
   - Automatic memory cleanup

2. **Processing Operations**:
   - Background thread execution
   - Progress callbacks
   - Cancellation support

3. **UI Rendering**:
   - ViewHolder pattern in adapters
   - Efficient list updates with DiffUtil
   - Minimal layout passes

### Storage Management

1. **Temporary Files**:
   - App-specific directory usage
   - Automatic cleanup on exit
   - Size monitoring and limits

2. **Output Files**:
   - User-accessible storage
   - FileProvider for sharing
   - Compression options

## Security Considerations

### Permissions

- **Runtime Permissions**: Dynamic permission requests
- **Scoped Storage**: Android 10+ compatibility
- **File Access**: Secure file provider implementation

### Data Protection

- **No Network**: Offline-first approach
- **Local Processing**: All operations on-device
- **Privacy**: No data collection or analytics

## Testing Strategy

### Unit Testing

```kotlin
class VideoEditorViewModelTest {
    @Test
    fun `cut video updates timeline correctly`() {
        // Test video cutting functionality
    }
    
    @Test
    fun `filter application handles errors gracefully`() {
        // Test error handling
    }
}
```

### Integration Testing

- FFmpeg command execution
- File I/O operations
- Permission handling
- UI state management

### Performance Testing

- Memory usage monitoring
- Processing time benchmarks
- UI responsiveness metrics
- Battery usage analysis

## Build Configuration

### Gradle Setup

```gradle
android {
    compileSdk 34
    
    defaultConfig {
        minSdk 21
        targetSdk 34
    }
    
    buildFeatures {
        viewBinding true
    }
}

dependencies {
    // Core Android
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    
    // UI Components
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    
    // Architecture Components
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
    implementation 'androidx.activity:activity-ktx:1.8.2'
    
    // Video Processing
    implementation 'com.arthenica:ffmpeg-kit-full:5.1'
    implementation 'com.google.android.exoplayer:exoplayer:2.19.1'
}
```

### ProGuard Configuration

```proguard
# Keep FFmpeg Kit classes
-keep class com.arthenica.ffmpegkit.** { *; }

# Keep ExoPlayer classes
-keep class com.google.android.exoplayer2.** { *; }

# Keep data classes
-keep class com.example.focuslock.data.models.** { *; }
```

## Deployment Considerations

### APK Size Optimization

1. **FFmpeg Variants**: Use architecture-specific builds
2. **Resource Optimization**: Vector drawables, WebP images
3. **Code Shrinking**: ProGuard/R8 optimization
4. **Dynamic Delivery**: Feature modules for advanced tools

### Device Compatibility

- **Minimum API**: Android 5.0 (API 21)
- **Target API**: Android 14 (API 34)
- **Architecture**: ARM64, ARM32, x86_64 support
- **RAM Requirements**: Minimum 2GB, Recommended 4GB+

### Performance Targets

- **App Launch**: < 2 seconds cold start
- **Video Import**: < 5 seconds for 1080p/30fps
- **Filter Application**: < 10 seconds for 30-second clip
- **Export Speed**: Real-time or better for most operations

## Future Enhancements

### Planned Features

1. **Advanced Editing**:
   - Keyframe animations
   - Multi-track timeline
   - Advanced color grading

2. **Performance**:
   - Hardware acceleration
   - Background processing
   - Cloud rendering options

3. **User Experience**:
   - Gesture controls
   - Voice commands
   - AI-powered suggestions

### Technical Debt

1. **Code Quality**:
   - Increase test coverage to 80%+
   - Implement dependency injection
   - Add comprehensive logging

2. **Architecture**:
   - Migrate to Compose UI
   - Implement Repository pattern
   - Add offline caching layer

## Troubleshooting Guide

### Common Issues

1. **Memory Issues**:
   - Monitor heap usage
   - Implement proper cleanup
   - Use memory profiler

2. **Processing Failures**:
   - Validate input files
   - Check available storage
   - Handle FFmpeg errors

3. **UI Performance**:
   - Profile layout performance
   - Optimize RecyclerView usage
   - Reduce overdraw

### Debugging Tools

- **Android Studio Profiler**: Memory, CPU, Network analysis
- **Layout Inspector**: UI hierarchy debugging
- **Logcat**: Runtime logging and error tracking
- **ADB Commands**: Device state inspection

---

This technical architecture provides a solid foundation for a professional video editing application while maintaining code quality, performance, and user experience standards.
