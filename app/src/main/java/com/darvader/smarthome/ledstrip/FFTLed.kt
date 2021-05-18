package com.darvader.smarthome.ledstrip

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.darvader.smarthome.R


class FFTLed : AppCompatActivity() {

    // Requesting permission to RECORD_AUDIO
    private var permissionToRecordAccepted = false
    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val audioRecordThread = AudioRecordThread()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fft_led)

        val drawView = DrawView(this)
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, Array(1){Manifest.permission.RECORD_AUDIO}, 0);
            return
        }
        audioRecordThread.stopped = false
        audioRecordThread.drawView = drawView

        Thread(audioRecordThread).start()

    }

    override fun onDestroy() {
        super.onDestroy()
        audioRecordThread.stopped = true
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 200
    }

}

