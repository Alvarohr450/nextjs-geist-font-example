package com.example.focuslock.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.focuslock.R
import com.example.focuslock.databinding.ActivityExportBinding
import com.example.focuslock.viewmodels.ExportViewModel
import java.io.File

class ExportActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityExportBinding
    private val viewModel: ExportViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Resolution selection
        binding.resolutionRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val resolution = when (checkedId) {
                R.id.resolution720p -> "720p"
                R.id.resolution1080p -> "1080p"
                R.id.resolution4k -> "4k"
                else -> "1080p"
            }
            viewModel.setResolution(resolution)
        }
        
        // Aspect ratio selection
        binding.aspectRatioRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val aspectRatio = when (checkedId) {
                R.id.aspect16_9 -> "16:9"
                R.id.aspect9_16 -> "9:16"
                R.id.aspect1_1 -> "1:1"
                else -> "16:9"
            }
            viewModel.setAspectRatio(aspectRatio)
        }
        
        // Quality slider
        binding.qualitySlider.addOnChangeListener { _, value, _ ->
            viewModel.setQuality(value.toInt())
            updateQualityText(value.toInt())
        }
        
        // Buttons
        binding.btnCancel.setOnClickListener {
            finish()
        }
        
        binding.btnStartExport.setOnClickListener {
            viewModel.startExport()
        }
        
        // Initialize quality text
        updateQualityText(binding.qualitySlider.value.toInt())
    }
    
    private fun observeViewModel() {
        viewModel.exportSettings.observe(this) { settings ->
            updateExportSummary(settings)
        }
        
        viewModel.isExporting.observe(this) { isExporting ->
            binding.exportProgressOverlay.visibility = if (isExporting) View.VISIBLE else View.GONE
            binding.btnStartExport.isEnabled = !isExporting
        }
        
        viewModel.exportProgress.observe(this) { progress ->
            binding.exportProgressPercentage.text = "${progress}%"
        }
        
        viewModel.exportComplete.observe(this) { filePath ->
            filePath?.let {
                showExportComplete(it)
            }
        }
        
        viewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun updateQualityText(quality: Int) {
        val qualityText = when {
            quality >= 80 -> "High ($quality%)"
            quality >= 60 -> "Medium ($quality%)"
            quality >= 40 -> "Low ($quality%)"
            else -> "Very Low ($quality%)"
        }
        binding.qualityValue.text = qualityText
    }
    
    private fun updateExportSummary(settings: com.example.focuslock.data.models.ExportSettings) {
        val estimatedSize = calculateEstimatedSize(settings)
        val summary = """
            Resolution: ${settings.resolution}
            Aspect Ratio: ${settings.aspectRatio}
            Quality: ${getQualityText(settings.quality)}
            Estimated Size: ~${estimatedSize} MB
        """.trimIndent()
        
        binding.exportSummary.text = summary
    }
    
    private fun calculateEstimatedSize(settings: com.example.focuslock.data.models.ExportSettings): Int {
        // Simple estimation based on resolution and quality
        val baseSize = when (settings.resolution) {
            "720p" -> 15
            "1080p" -> 25
            "4k" -> 100
            else -> 25
        }
        
        return (baseSize * (settings.quality / 100.0)).toInt()
    }
    
    private fun getQualityText(quality: Int): String {
        return when {
            quality >= 80 -> "High ($quality%)"
            quality >= 60 -> "Medium ($quality%)"
            quality >= 40 -> "Low ($quality%)"
            else -> "Very Low ($quality%)"
        }
    }
    
    private fun showExportComplete(filePath: String) {
        binding.exportProgressOverlay.visibility = View.GONE
        
        Toast.makeText(this, getString(R.string.export_complete), Toast.LENGTH_LONG).show()
        
        // Show share dialog
        val file = File(filePath)
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
        
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "video/mp4"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        
        startActivity(Intent.createChooser(shareIntent, getString(R.string.share)))
        finish()
    }
}
