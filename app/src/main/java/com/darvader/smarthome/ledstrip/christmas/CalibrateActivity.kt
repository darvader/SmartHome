package com.darvader.smarthome.ledstrip.christmas

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darvader.smarthome.R
import com.darvader.smarthome.databinding.ActivityCalibrateBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CalibrateActivity : AppCompatActivity() {
    private lateinit var imageCapture: ImageCapture

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var calibrate: Calibrate

    lateinit var binding: ActivityCalibrateBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calibrate)
        binding = ActivityCalibrateBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.front.setOnClickListener {
            Thread {
                calibrate.perspective = "front"
                calibrate.setImageCapture(imageCapture)
                calibrate.calibrate()
            }.start()
        }
        binding.left.setOnClickListener {
            Thread {
                calibrate.perspective = "left"
                calibrate.setImageCapture(imageCapture)
                calibrate.calibrate()
            }.start()
        }
        binding.back.setOnClickListener {
            Thread {
                calibrate.perspective = "back"
                calibrate.setImageCapture(imageCapture)
                calibrate.calibrate()
            }.start()
        }
        binding.right.setOnClickListener {
            Thread {
                calibrate.perspective = "right"
                calibrate.setImageCapture(imageCapture)
                calibrate.calibrate()
            }.start()
        }
        binding.showTree.setOnClickListener {
            calibrate.collectPoints()
            val intent = Intent(this, OpenGLES20Activity::class.java)
            startActivity(intent)
        }
        binding.collectPoints.setOnClickListener { Thread { calibrate.collectPoints() }.start() }
        startCamera()
        calibrate = Calibrate(this)

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()


            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXBasic"
    }
}