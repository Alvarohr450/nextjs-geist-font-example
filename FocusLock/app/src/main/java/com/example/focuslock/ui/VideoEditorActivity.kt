package com.example.focuslock.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focuslock.R
import com.example.focuslock.adapters.TimelineAdapter
import com.example.focuslock.databinding.ActivityVideoEditorBinding
import com.example.focuslock.viewmodels.VideoEditorViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView

class VideoEditorActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityVideoEditorBinding
    private val viewModel: VideoEditorViewModel by viewModels()
    private var exoPlayer: ExoPlayer? = null
    private lateinit var timelineAdapter: TimelineAdapter
    
    // Activity result launchers
    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { 
            viewModel.loadVideo(it)
            hideImportButtons()
        }
    }
    
    private val videoRecorderLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                viewModel.loadVideo(uri)
                hideImportButtons()
            }
        }
    }
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (!allGranted) {
            Toast.makeText(this, getString(R.string.error_permission_denied), Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupPlayer()
        setupTimeline()
        setupControls()
        observeViewModel()
        checkPermissions()
    }
    
    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build()
        binding.playerView.player = exoPlayer
        binding.playerView.useController = true
    }
    
    private fun setupTimeline() {
        timelineAdapter = TimelineAdapter { clip ->
            viewModel.selectClip(clip)
        }
        
        binding.timelineRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@VideoEditorActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = timelineAdapter
        }
    }
    
    private fun setupControls() {
        binding.btnImportVideo.setOnClickListener {
            if (hasStoragePermission()) {
                videoPickerLauncher.launch("video/*")
            } else {
                requestPermissions()
            }
        }
        
        binding.btnRecordVideo.setOnClickListener {
            if (hasCameraPermission()) {
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                videoRecorderLauncher.launch(intent)
            } else {
                requestPermissions()
            }
        }
        
        binding.btnCut.setOnClickListener { viewModel.showCutPanel() }
        binding.btnSplit.setOnClickListener { viewModel.showSplitPanel() }
        binding.btnCrop.setOnClickListener { viewModel.showCropPanel() }
        binding.btnSpeed.setOnClickListener { viewModel.showSpeedPanel() }
        binding.btnRotate.setOnClickListener { viewModel.rotateVideo() }
        binding.btnFilters.setOnClickListener { viewModel.showFiltersPanel() }
        binding.btnText.setOnClickListener { viewModel.showTextPanel() }
        binding.btnAudio.setOnClickListener { viewModel.showAudioPanel() }
        binding.btnExport.setOnClickListener { 
            startActivity(Intent(this, ExportActivity::class.java))
        }
    }
    
    private fun observeViewModel() {
        viewModel.currentVideoUri.observe(this) { uri ->
            uri?.let { loadVideoInPlayer(it) }
        }
        
        viewModel.timelineClips.observe(this) { clips ->
            timelineAdapter.submitList(clips)
        }
        
        viewModel.isProcessing.observe(this) { isProcessing ->
            binding.progressOverlay.visibility = if (isProcessing) View.VISIBLE else View.GONE
        }
        
        viewModel.progressText.observe(this) { text ->
            binding.progressText.text = text
        }
        
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
        
        viewModel.currentPanel.observe(this) { panel ->
            showEditingPanel(panel)
        }
    }
    
    private fun loadVideoInPlayer(uri: Uri) {
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }
    
    private fun hideImportButtons() {
        binding.importButtonsLayout.visibility = View.GONE
    }
    
    private fun showEditingPanel(panelType: VideoEditorViewModel.PanelType?) {
        binding.editingPanelContainer.removeAllViews()
        
        when (panelType) {
            VideoEditorViewModel.PanelType.FILTERS -> {
                val filtersPanel = layoutInflater.inflate(R.layout.panel_filters, binding.editingPanelContainer, false)
                binding.editingPanelContainer.addView(filtersPanel)
                binding.editingPanelContainer.visibility = View.VISIBLE
            }
            VideoEditorViewModel.PanelType.AUDIO -> {
                val audioPanel = layoutInflater.inflate(R.layout.panel_audio, binding.editingPanelContainer, false)
                binding.editingPanelContainer.addView(audioPanel)
                binding.editingPanelContainer.visibility = View.VISIBLE
            }
            VideoEditorViewModel.PanelType.TEXT -> {
                val textPanel = layoutInflater.inflate(R.layout.panel_text_stickers, binding.editingPanelContainer, false)
                binding.editingPanelContainer.addView(textPanel)
                binding.editingPanelContainer.visibility = View.VISIBLE
            }
            null -> {
                binding.editingPanelContainer.visibility = View.GONE
            }
            else -> {
                // Handle other panel types
                binding.editingPanelContainer.visibility = View.GONE
            }
        }
    }
    
    private fun checkPermissions() {
        if (!hasStoragePermission() || !hasCameraPermission()) {
            requestPermissions()
        }
    }
    
    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
        permissionLauncher.launch(permissions)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.release()
    }
    
    override fun onPause() {
        super.onPause()
        exoPlayer?.pause()
    }
}
