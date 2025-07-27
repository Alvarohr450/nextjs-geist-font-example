package com.example.focuslock

import android.app.Application
import com.example.focuslock.utils.FFmpegUtil

class FocusLockApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize FFmpegUtil with application context
        FFmpegUtil.initialize(this)
    }
}
