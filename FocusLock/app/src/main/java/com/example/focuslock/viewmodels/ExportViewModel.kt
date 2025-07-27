package com.example.focuslock.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.focuslock.data.models.ExportSettings
import com.example.focuslock.utils.FFmpegUtil
import kotlinx.coroutines.launch

class ExportViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _exportSettings = MutableLiveData<ExportSettings>()
    val exportSettings: LiveData<ExportSettings> = _exportSettings
    
    private val _isExporting = MutableLiveData<Boolean>()
    val isExporting: LiveData<Boolean> = _isExporting
    
    private val _exportProgress = MutableLiveData<Int>()
    val exportProgress: LiveData<Int> = _exportProgress
    
    private val _exportComplete = MutableLiveData<String?>()
    val exportComplete: LiveData<String?> = _exportComplete
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private var currentSettings = ExportSettings()
    
    init {
        _exportSettings.value = currentSettings
    }
    
    fun setResolution(resolution: String) {
        currentSettings = currentSettings.copy(resolution = resolution)
        _exportSettings.value = currentSettings
    }
    
    fun setAspectRatio(aspectRatio: String) {
        currentSettings = currentSettings.copy(aspectRatio = aspectRatio)
        _exportSettings.value = currentSettings
    }
    
    fun setQuality(quality: Int) {
        currentSettings = currentSettings.copy(quality = quality)
        _exportSettings.value = currentSettings
    }
    
    fun startExport() {
        // Get the video path from the previous activity or shared preferences
        val videoPath = getVideoPathToExport()
        
        if (videoPath == null) {
            _errorMessage.value = "No video to export"
            return
        }
        
        viewModelScope.launch {
            _isExporting.value = true
            _exportProgress.value = 0
            
            try {
                // Simulate progress updates
                updateProgress(10)
                
                val outputPath = FFmpegUtil.exportVideo(
                    videoPath,
                    currentSettings.resolution,
                    currentSettings.aspectRatio,
                    currentSettings.quality
                )
                
                updateProgress(50)
                
                if (outputPath != null) {
                    updateProgress(100)
                    _exportComplete.value = outputPath
                } else {
                    _errorMessage.value = "Export failed. Please try again."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Export error: ${e.message}"
            } finally {
                _isExporting.value = false
            }
        }
    }
    
    private fun updateProgress(progress: Int) {
        _exportProgress.value = progress
    }
    
    private fun getVideoPathToExport(): String? {
        // This should get the final video path from the editing session
        // For now, return a placeholder - in a real app, this would come from
        // shared preferences, intent extras, or a repository
        return null
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
