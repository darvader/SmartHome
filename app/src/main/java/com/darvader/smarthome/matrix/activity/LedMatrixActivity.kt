package com.darvader.smarthome.matrix.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import kotlinx.android.synthetic.main.activity_matrix.*

class LedMatrixActivity : AppCompatActivity() {

    companion object {
        var ledMatrix = LedMatrix()
    }

    init {
        SmartHomeActivity.echoServer.register(ledMatrix)
    }

    abstract class ProgressChangedListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ledMatrix.detect()

        setContentView(R.layout.activity_matrix)
        scoreboard.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }

        videoPlayer.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

        time.setOnClickListener {
            val intent = Intent(this, TimeActivity::class.java)
            startActivity(intent)
        }

        animations.setOnClickListener {
            val intent = Intent(this, AnimationsActivity::class.java)
            startActivity(intent)
        }

        timer.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        detect.setOnClickListener {
            ledMatrix?.detect()
        }

        brightness.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledMatrix?.changeBrightness(progress)
            }
        })
    }
}