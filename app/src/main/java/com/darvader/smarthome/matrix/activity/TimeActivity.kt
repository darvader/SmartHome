package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.R
import com.darvader.smarthome.databinding.ActivityTimeSampleBinding
import com.darvader.smarthome.matrix.LedMatrix

class TimeActivity() : AppCompatActivity() {

    private var ledMatrix: LedMatrix? = null
    lateinit var binding: ActivityTimeSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_sample)
        binding = ActivityTimeSampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        this.ledMatrix = LedMatrixActivity.ledMatrix

        binding.time1.setOnClickListener { ledMatrix?.startTime1() }
        binding.time2.setOnClickListener { ledMatrix?.startTime2() }
        binding.time3.setOnClickListener { ledMatrix?.startTime3() }
        binding.time4.setOnClickListener { ledMatrix?.startTime4() }
        binding.snow.setOnClickListener { ledMatrix?.startSnow() }
        binding.csnow.setOnClickListener { ledMatrix?.startColoredSnow() }
        binding.plasma.setOnClickListener { ledMatrix?.startPlasma() }
        binding.mandelbrot.setOnClickListener { ledMatrix?.mandelbrot() }
        binding.gameOfLife.setOnClickListener { ledMatrix?.gameOfLife() }

        binding.brightness.setOnSeekBarChangeListener(object : LedMatrixActivity.ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                LedMatrixActivity.ledMatrix.changeBrightness(progress)
            }
        })
    }
}