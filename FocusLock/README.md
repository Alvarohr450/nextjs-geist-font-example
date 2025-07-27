# FocusLock - Video Editor Android App

FocusLock is a comprehensive video editing application for Android that replicates the core functionalities of CapCut. It provides professional video editing tools with an intuitive, modern interface.

## Features

### Core Video Editing
- **Import & Record**: Import videos from gallery or record directly with camera
- **Cut & Split**: Precise video trimming and splitting capabilities
- **Speed Adjustment**: Variable speed control from 0.1x to 100x
- **Rotate & Flip**: 90°, 180°, 270° rotation and horizontal/vertical flipping
- **Crop**: Custom cropping with aspect ratio controls

### Effects & Filters
- **Pre-built Filters**: Vintage, Dramatic, Bright, Warm, Cool, Sepia, Black & White, Vivid, Soft
- **Real-time Preview**: Live filter preview before applying
- **Color Adjustments**: Brightness, contrast, saturation, temperature controls

### Audio Features
- **Background Music**: Add music from device library
- **Volume Control**: Independent volume controls for original audio and background music
- **Audio Sync**: Synchronization tools for perfect audio-video alignment
- **Sound Effects**: Library of built-in sound effects

### Text & Overlays
- **Text Overlays**: Add custom text with multiple fonts and sizes
- **Color Options**: Multiple text colors and styling options
- **Positioning**: Drag-and-drop text positioning
- **Stickers**: Built-in sticker library with emojis and graphics

### Export Options
- **Multiple Resolutions**: 720p, 1080p, 4K export options
- **Aspect Ratios**: 16:9, 9:16, 1:1 support
- **Quality Control**: Adjustable compression quality (10-100%)
- **Share Integration**: Direct sharing to social media platforms

## Technical Architecture

### Technology Stack
- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **UI Framework**: Android Jetpack with Material Design 3
- **Video Processing**: FFmpeg Kit for Android
- **Video Playback**: ExoPlayer
- **Async Operations**: Kotlin Coroutines
- **Dependency Injection**: Manual DI with ViewModels

### Project Structure
```
com.example.focuslock/
├── ui/                          # Activities and UI components
│   ├── VideoEditorActivity.kt   # Main editing interface
│   └── ExportActivity.kt        # Export configuration
├── viewmodels/                  # ViewModels for business logic
│   ├── VideoEditorViewModel.kt  # Main editing logic
│   └── ExportViewModel.kt       # Export handling
├── adapters/                    # RecyclerView adapters
│   ├── TimelineAdapter.kt       # Timeline clip management
│   └── FilterAdapter.kt         # Filter selection
├── data/models/                 # Data models
│   └── VideoClip.kt            # Video clip and related models
├── utils/                       # Utility classes
│   └── FFmpegUtil.kt           # FFmpeg command wrapper
└── FocusLockApplication.kt     # Application class
```

## Installation & Setup

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK API 21+ (Android 5.0+)
- Minimum 4GB RAM for development
- 2GB free storage space

### Setup Instructions

1. **Clone/Download the Project**
   ```bash
   # If using Git
   git clone <repository-url>
   cd FocusLock
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing Android Studio project"
   - Navigate to the FocusLock folder and select it

3. **Sync Dependencies**
   - Android Studio will automatically prompt to sync Gradle files
   - Click "Sync Now" when prompted
   - Wait for all dependencies to download

4. **Build the Project**
   ```bash
   # Via Android Studio: Build > Make Project
   # Or via command line:
   ./gradlew build
   ```

5. **Run on Device/Emulator**
   - Connect an Android device or start an emulator
   - Click the "Run" button in Android Studio
   - Select your target device

### Dependencies
The app uses the following key dependencies:
- `com.arthenica:ffmpeg-kit-full:5.1` - Video processing
- `com.google.android.exoplayer:exoplayer:2.19.1` - Video playback
- `com.google.android.material:material:1.11.0` - Material Design components
- `androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0` - ViewModel support

## Usage Guide

### Basic Video Editing Workflow

1. **Import Video**
   - Tap "Import Video" to select from gallery
   - Or tap "Record Video" to capture new footage

2. **Edit Timeline**
   - Use timeline at bottom to navigate through video
   - Tap clips to select and edit them
   - Drag timeline to scrub through video

3. **Apply Edits**
   - **Cut**: Select start/end points and tap "Cut"
   - **Split**: Position playhead and tap "Split"
   - **Speed**: Choose from preset speeds or custom value
   - **Rotate**: Tap "Rotate" for 90° clockwise rotation

4. **Add Effects**
   - Tap "Filters" to browse and apply video filters
   - Tap "Text" to add text overlays
   - Tap "Audio" to add background music

5. **Export Video**
   - Tap "Export" when editing is complete
   - Choose resolution, aspect ratio, and quality
   - Tap "Export Video" to render final output

### Advanced Features

#### Timeline Management
- Multiple clips are displayed horizontally
- Clip duration affects visual width
- Selected clips are highlighted
- Tap any clip to select and edit

#### Filter Application
- Preview filters before applying
- Reset to remove all filters
- Filters are applied to selected clip only

#### Audio Mixing
- Original audio volume: 0-100%
- Background music volume: 0-100%
- Automatic audio synchronization

#### Text Overlays
- Custom text input with live preview
- Font size slider (12-72pt)
- Color picker with preset colors
- Drag positioning on video preview

## Performance Optimization

### Memory Management
- Efficient video loading with ExoPlayer
- Automatic cleanup of temporary files
- Background processing for heavy operations

### Processing Optimization
- Asynchronous FFmpeg operations
- Progress feedback for long operations
- Error handling and recovery

### Storage Management
- Temporary files in app-specific directory
- Automatic cleanup on app exit
- Configurable output quality for size control

## Troubleshooting

### Common Issues

**App crashes on video import**
- Check storage permissions are granted
- Ensure sufficient free storage space
- Try with smaller video files first

**Export fails or takes too long**
- Reduce export quality setting
- Check available storage space
- Close other apps to free memory

**Video playback issues**
- Update to latest Android System WebView
- Clear app cache and data
- Restart the application

**FFmpeg processing errors**
- Check video file format compatibility
- Ensure input video is not corrupted
- Try with different source videos

### Performance Tips
- Close other apps while editing large videos
- Use lower preview quality during editing
- Export at lower quality for faster processing
- Keep app updated for latest optimizations

## Development Notes

### Adding New Features
1. Create new UI layouts in `res/layout/`
2. Add corresponding ViewModels for business logic
3. Implement FFmpeg commands in `FFmpegUtil.kt`
4. Update string resources in `strings.xml`
5. Test thoroughly on different devices

### Customization
- Colors: Modify `colors.xml` for theme changes
- Strings: Update `strings.xml` for localization
- Styles: Customize `themes.xml` for UI appearance
- Filters: Add new filters in `FFmpegUtil.applyFilter()`

## License

This project is for educational and development purposes. Please ensure compliance with FFmpeg licensing requirements when distributing.

## Support

For technical issues or feature requests, please refer to the project documentation or create an issue in the project repository.

---

**Version**: 1.0.0  
**Target SDK**: 34 (Android 14)  
**Minimum SDK**: 21 (Android 5.0)  
**Build Tools**: 34.0.0
