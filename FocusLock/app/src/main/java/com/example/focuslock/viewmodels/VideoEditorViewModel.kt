package com.example.focuslock.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.focuslock.data.models.VideoClip
import com.example.focuslock.utils.FFmpegUtil
import kotlinx.coroutines.launch

class VideoEditorViewModel(application: Application) : AndroidViewModel(application) {
    
    enum class PanelType {
        FILTERS, AUDIO, TEXT, SPEED, CUT, SPLIT, CROP
    }
    
    private val _currentVideoUri = MutableLiveData<Uri?>()
    val currentVideoUri: LiveData<Uri?> = _currentVideoUri
    
    private val _timelineClips = MutableLiveData<List<VideoClip>>()
    val timelineClips: LiveData<List<VideoClip>> = _timelineClips
    
    private val _selectedClip = MutableLiveData<VideoClip?>()
    val selectedClip: LiveData<VideoClip?> = _selectedClip
    
    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> = _isProcessing
    
    private val _progressText = MutableLiveData<String>()
    val progressText: LiveData<String> = _progressText
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _currentPanel = MutableLiveData<PanelType?>()
    val currentPanel: LiveData<PanelType?> = _currentPanel
    
    private val clips = mutableListOf<VideoClip>()
    private var currentClipIndex = 0
    
    fun loadVideo(uri: Uri) {
        _currentVideoUri.value = uri
        
        // Create initial clip
        val initialClip = VideoClip(
            id = generateClipId(),
            uri = uri,
            startTime = 0f,
            endTime = 0f, // Will be set after getting video duration
            name = "Main Video"
        )
        
        clips.clear()
        clips.add(initialClip)
        _timelineClips.value = clips.toList()
        _selectedClip.value = initialClip
    }
    
    fun selectClip(clip: VideoClip) {
        _selectedClip.value = clip
        currentClipIndex = clips.indexOf(clip)
    }
    
    // Panel management
    fun showFiltersPanel() {
        _currentPanel.value = PanelType.FILTERS
    }
    
    fun showAudioPanel() {
        _currentPanel.value = PanelType.AUDIO
    }
    
    fun showTextPanel() {
        _currentPanel.value = PanelType.TEXT
    }
    
    fun showSpeedPanel() {
        _currentPanel.value = PanelType.SPEED
    }
    
    fun showCutPanel() {
        _currentPanel.value = PanelType.CUT
    }
    
    fun showSplitPanel() {
        _currentPanel.value = PanelType.SPLIT
    }
    
    fun showCropPanel() {
        _currentPanel.value = PanelType.CROP
    }
    
    fun hidePanel() {
        _currentPanel.value = null
    }
    
    // Video editing operations
    fun cutVideo(startTime: Float, endTime: Float) {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Cutting video..."
            
            try {
                val outputPath = FFmpegUtil.cut(
                    selectedClip.uri.toString(),
                    startTime,
                    endTime
                )
                
                if (outputPath != null) {
                    val newClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(outputPath),
                        startTime = startTime,
                        endTime = endTime,
                        name = "Cut ${clips.size + 1}"
                    )
                    
                    clips[currentClipIndex] = newClip
                    _timelineClips.value = clips.toList()
                    _selectedClip.value = newClip
                } else {
                    _errorMessage.value = "Failed to cut video"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error cutting video: ${e.message}"
            } finally {
                _isProcessing.value = false
                hidePanel()
            }
        }
    }
    
    fun splitVideo(splitTime: Float) {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Splitting video..."
            
            try {
                // Create first part (0 to splitTime)
                val firstPartPath = FFmpegUtil.cut(
                    selectedClip.uri.toString(),
                    0f,
                    splitTime
                )
                
                // Create second part (splitTime to end)
                val secondPartPath = FFmpegUtil.cut(
                    selectedClip.uri.toString(),
                    splitTime,
                    selectedClip.endTime
                )
                
                if (firstPartPath != null && secondPartPath != null) {
                    val firstClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(firstPartPath),
                        endTime = splitTime,
                        name = "Part 1"
                    )
                    
                    val secondClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(secondPartPath),
                        startTime = splitTime,
                        name = "Part 2"
                    )
                    
                    clips[currentClipIndex] = firstClip
                    clips.add(currentClipIndex + 1, secondClip)
                    _timelineClips.value = clips.toList()
                } else {
                    _errorMessage.value = "Failed to split video"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error splitting video: ${e.message}"
            } finally {
                _isProcessing.value = false
                hidePanel()
            }
        }
    }
    
    fun adjustSpeed(speedMultiplier: Float) {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Adjusting speed..."
            
            try {
                val outputPath = FFmpegUtil.adjustSpeed(
                    selectedClip.uri.toString(),
                    speedMultiplier
                )
                
                if (outputPath != null) {
                    val newClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(outputPath),
                        name = "Speed ${speedMultiplier}x"
                    )
                    
                    clips[currentClipIndex] = newClip
                    _timelineClips.value = clips.toList()
                    _selectedClip.value = newClip
                } else {
                    _errorMessage.value = "Failed to adjust speed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adjusting speed: ${e.message}"
            } finally {
                _isProcessing.value = false
                hidePanel()
            }
        }
    }
    
    fun rotateVideo() {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Rotating video..."
            
            try {
                val outputPath = FFmpegUtil.rotate(selectedClip.uri.toString(), 90)
                
                if (outputPath != null) {
                    val newClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(outputPath),
                        name = "Rotated"
                    )
                    
                    clips[currentClipIndex] = newClip
                    _timelineClips.value = clips.toList()
                    _selectedClip.value = newClip
                } else {
                    _errorMessage.value = "Failed to rotate video"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error rotating video: ${e.message}"
            } finally {
                _isProcessing.value = false
            }
        }
    }
    
    fun applyFilter(filterName: String) {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Applying filter..."
            
            try {
                val outputPath = FFmpegUtil.applyFilter(
                    selectedClip.uri.toString(),
                    filterName
                )
                
                if (outputPath != null) {
                    val newClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(outputPath),
                        name = "Filtered"
                    )
                    
                    clips[currentClipIndex] = newClip
                    _timelineClips.value = clips.toList()
                    _selectedClip.value = newClip
                } else {
                    _errorMessage.value = "Failed to apply filter"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error applying filter: ${e.message}"
            } finally {
                _isProcessing.value = false
                hidePanel()
            }
        }
    }
    
    fun addTextOverlay(text: String, fontSize: Int, color: String, x: Int, y: Int) {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Adding text..."
            
            try {
                val outputPath = FFmpegUtil.addTextOverlay(
                    selectedClip.uri.toString(),
                    text,
                    fontSize,
                    color,
                    x,
                    y
                )
                
                if (outputPath != null) {
                    val newClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(outputPath),
                        name = "With Text"
                    )
                    
                    clips[currentClipIndex] = newClip
                    _timelineClips.value = clips.toList()
                    _selectedClip.value = newClip
                } else {
                    _errorMessage.value = "Failed to add text"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding text: ${e.message}"
            } finally {
                _isProcessing.value = false
                hidePanel()
            }
        }
    }
    
    fun addAudio(audioUri: Uri, volume: Float) {
        val selectedClip = _selectedClip.value ?: return
        
        viewModelScope.launch {
            _isProcessing.value = true
            _progressText.value = "Adding audio..."
            
            try {
                val outputPath = FFmpegUtil.mergeAudio(
                    selectedClip.uri.toString(),
                    audioUri.toString(),
                    volume
                )
                
                if (outputPath != null) {
                    val newClip = selectedClip.copy(
                        id = generateClipId(),
                        uri = Uri.parse(outputPath),
                        name = "With Audio"
                    )
                    
                    clips[currentClipIndex] = newClip
                    _timelineClips.value = clips.toList()
                    _selectedClip.value = newClip
                } else {
                    _errorMessage.value = "Failed to add audio"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error adding audio: ${e.message}"
            } finally {
                _isProcessing.value = false
                hidePanel()
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    private fun generateClipId(): String {
        return "clip_${System.currentTimeMillis()}"
    }
    
    fun getFinalVideoPath(): String? {
        return if (clips.isNotEmpty()) {
            clips.last().uri.toString()
        } else {
            null
        }
    }
}
