package com.example.focuslock.utils

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FFmpegUtil {
    
    private fun generateOutputPath(context: Context, suffix: String = ""): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "focuslock_${timestamp}${suffix}.mp4"
        return File(context.getExternalFilesDir(null), fileName).absolutePath
    }
    
    suspend fun cut(inputPath: String, startTime: Float, endTime: Float): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_cut")
            val command = "-i $inputPath -ss $startTime -to $endTime -c copy $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun adjustSpeed(inputPath: String, speedMultiplier: Float): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_speed")
            val videoSpeed = 1.0f / speedMultiplier
            val audioSpeed = speedMultiplier
            
            val command = "-i $inputPath -filter_complex \"[0:v]setpts=${videoSpeed}*PTS[v];[0:a]atempo=${audioSpeed}[a]\" -map \"[v]\" -map \"[a]\" $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun rotate(inputPath: String, degrees: Int): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_rotated")
            val transposeValue = when (degrees) {
                90 -> "1"
                180 -> "2,transpose=2"
                270 -> "2"
                else -> "0"
            }
            
            val command = "-i $inputPath -vf \"transpose=$transposeValue\" $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun applyFilter(inputPath: String, filterName: String): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_filtered")
            
            val filterCommand = when (filterName.lowercase()) {
                "vintage" -> "curves=vintage"
                "dramatic" -> "eq=contrast=1.5:brightness=0.1:saturation=1.2"
                "bright" -> "eq=brightness=0.2:contrast=1.1"
                "warm" -> "colortemperature=4000"
                "cool" -> "colortemperature=7000"
                "sepia" -> "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131"
                "black_white" -> "hue=s=0"
                "vivid" -> "eq=saturation=1.5:contrast=1.2"
                "soft" -> "gblur=sigma=1"
                else -> "eq=contrast=1.0" // No filter
            }
            
            val command = "-i $inputPath -vf \"$filterCommand\" $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun addTextOverlay(
        inputPath: String,
        text: String,
        fontSize: Int,
        color: String,
        x: Int,
        y: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_text")
            val escapedText = text.replace(":", "\\:")
            
            val command = "-i $inputPath -vf \"drawtext=text='$escapedText':fontsize=$fontSize:fontcolor=$color:x=$x:y=$y\" $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun mergeAudio(
        videoPath: String,
        audioPath: String,
        audioVolume: Float
    ): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_audio")
            
            val command = "-i $videoPath -i $audioPath -filter_complex \"[1:a]volume=${audioVolume}[a1];[0:a][a1]amix=inputs=2:duration=first:dropout_transition=3\" -c:v copy $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun crop(
        inputPath: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_cropped")
            
            val command = "-i $inputPath -vf \"crop=$width:$height:$x:$y\" $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun exportVideo(
        inputPath: String,
        resolution: String,
        aspectRatio: String,
        quality: Int
    ): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_export")
            
            // Convert resolution string to dimensions
            val (width, height) = when (resolution) {
                "720p" -> when (aspectRatio) {
                    "16:9" -> Pair(1280, 720)
                    "9:16" -> Pair(720, 1280)
                    "1:1" -> Pair(720, 720)
                    else -> Pair(1280, 720)
                }
                "1080p" -> when (aspectRatio) {
                    "16:9" -> Pair(1920, 1080)
                    "9:16" -> Pair(1080, 1920)
                    "1:1" -> Pair(1080, 1080)
                    else -> Pair(1920, 1080)
                }
                "4k" -> when (aspectRatio) {
                    "16:9" -> Pair(3840, 2160)
                    "9:16" -> Pair(2160, 3840)
                    "1:1" -> Pair(2160, 2160)
                    else -> Pair(3840, 2160)
                }
                else -> Pair(1920, 1080)
            }
            
            // Calculate bitrate based on quality (10-100)
            val bitrate = when {
                quality >= 80 -> "5000k"
                quality >= 60 -> "3000k"
                quality >= 40 -> "2000k"
                quality >= 20 -> "1000k"
                else -> "500k"
            }
            
            val command = "-i $inputPath -vf \"scale=$width:$height\" -b:v $bitrate -c:v libx264 -preset medium -c:a aac -b:a 128k $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    suspend fun getVideoDuration(inputPath: String): Float = withContext(Dispatchers.IO) {
        try {
            val command = "-i $inputPath -f null -"
            val session = FFmpegKit.execute(command)
            
            // Parse duration from FFmpeg output
            val output = session.allLogsAsString
            val durationRegex = "Duration: (\\d{2}):(\\d{2}):(\\d{2})\\.(\\d{2})".toRegex()
            val matchResult = durationRegex.find(output)
            
            if (matchResult != null) {
                val hours = matchResult.groupValues[1].toInt()
                val minutes = matchResult.groupValues[2].toInt()
                val seconds = matchResult.groupValues[3].toInt()
                val centiseconds = matchResult.groupValues[4].toInt()
                
                return@withContext (hours * 3600 + minutes * 60 + seconds + centiseconds / 100.0f)
            }
            
            return@withContext 0f
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }
    }
    
    suspend fun concatenateVideos(videoPaths: List<String>): String? = withContext(Dispatchers.IO) {
        try {
            val outputPath = generateOutputPath(getContext(), "_merged")
            
            // Create input list for FFmpeg
            val inputList = videoPaths.joinToString(" ") { "-i $it" }
            val filterComplex = videoPaths.indices.joinToString("") { "[$it:v][$it:a]" } + 
                "concat=n=${videoPaths.size}:v=1:a=1[outv][outa]"
            
            val command = "$inputList -filter_complex \"$filterComplex\" -map \"[outv]\" -map \"[outa]\" $outputPath"
            
            val session = FFmpegKit.execute(command)
            
            return@withContext if (ReturnCode.isSuccess(session.returnCode)) {
                outputPath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    // Helper function to get context - this should be initialized in Application class
    private lateinit var appContext: Context
    
    fun initialize(context: Context) {
        appContext = context.applicationContext
    }
    
    private fun getContext(): Context = appContext
}
