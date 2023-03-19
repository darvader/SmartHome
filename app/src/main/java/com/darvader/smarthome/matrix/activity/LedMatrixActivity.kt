package com.darvader.smarthome.matrix.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.databinding.ActivityLedMatrixBinding
import com.darvader.smarthome.databinding.ActivityMatrixBinding

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

    lateinit var binding: ActivityMatrixBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ledMatrix.detect()
        binding = ActivityMatrixBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.scoreboard.setOnClickListener {
            val intent = Intent(this, ScoreboardActivity::class.java)
            startActivity(intent)
        }

        binding.videoPlayer.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            startActivity(intent)
        }

        binding.time.setOnClickListener {
            val intent = Intent(this, TimeActivity::class.java)
            startActivity(intent)
        }

        binding.animations.setOnClickListener {
            val intent = Intent(this, AnimationsActivity::class.java)
            startActivity(intent)
        }

        binding.timer.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        binding.detect.setOnClickListener {
            ledMatrix?.detect()
        }

        binding.brightness.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledMatrix?.changeBrightness(progress)
            }
        })
    }
}