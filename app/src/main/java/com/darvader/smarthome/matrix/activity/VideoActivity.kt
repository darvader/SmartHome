package com.darvader.smarthome.matrix.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darvader.smarthome.databinding.ActivityVideoBinding
import com.darvader.smarthome.matrix.LedMatrix
import wseemann.media.FFmpegMediaMetadataRetriever
import java.util.*
import kotlin.concurrent.schedule


class VideoActivity : AppCompatActivity() {
    var cameraFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Environment.DIRECTORY_DCIM + "/Camera/"
    var uri = cameraFolderPath + "VID_20230730_162943.mp4"
    var ledMatrix: LedMatrix? = null
    private var timer: TimerTask? = null

    lateinit var binding: ActivityVideoBinding
    private lateinit var videoView: VideoView
    private val REQUEST_VIDEO_TRIMMER = 0x01


    private val selectVideoResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedUri: Uri? = result.data?.data
            if (selectedUri != null) {
                this.sendToMatrix(selectedUri)
                // videoView.setVideoURI(selectedUri)
                // videoView.start()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        videoView = binding.videoView
        openVideo()
        // sendToMatrix()
    }

    private fun sendToMatrix(selectedUri: Uri) {
        ledMatrix = LedMatrixActivity.ledMatrix
        ledMatrix?.pictureMode()


        val mmr = FFmpegMediaMetadataRetriever()
        println(selectedUri)
        mmr.setDataSource(this, selectedUri)
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM)
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST)

        // var frame = 0L
        val startTime = System.currentTimeMillis()

        val bitmaps = ArrayList<Bitmap>()

        binding.start.setOnClickListener {
            println("Start caching bitmaps")
            for (i in 0L..10000L) {
                val b: Bitmap? = mmr.getScaledFrameAtTime(
                    (i * 30000L) + 3000000,
                    FFmpegMediaMetadataRetriever.OPTION_CLOSEST,
                    128,
                    64
                )

                if (b != null) {
                    bitmaps.add(b)
                    println("Bitmap $i added.")
                }
            }

            var i = 0
            timer = Timer("drawViewUpdater", true).schedule(1000, 1000 / 1) {
                ledMatrix?.sendBitmap(bitmaps[i++])
                if (i >= bitmaps.size) {
                    i = 0
                }
            }
        }
    }


    private fun requestPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            openVideo()
        }
    }

    private fun openVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        selectVideoResultLauncher.launch(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO_TRIMMER) {
            val selectedUri: Uri? = data?.data
            if (selectedUri != null) {
                videoView.setVideoURI(selectedUri)
                videoView.start()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel();
    }
}