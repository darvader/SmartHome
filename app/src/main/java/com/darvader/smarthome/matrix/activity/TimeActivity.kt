package com.darvader.smarthome.matrix.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.databinding.ActivitySampleBinding
import com.darvader.smarthome.databinding.ActivityTimerBinding
import com.darvader.smarthome.matrix.activity.LedMatrixActivity

class TimeActivity() : AppCompatActivity() {

    private var ledMatrix: LedMatrix? = null
    lateinit var binding: ActivitySampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        binding = ActivitySampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        this.ledMatrix = LedMatrixActivity.ledMatrix

        binding.time1.setOnClickListener { ledMatrix?.startTime1() }
        binding.time2.setOnClickListener { ledMatrix?.startTime2() }
        binding.time3.setOnClickListener { ledMatrix?.startTime3() }
        binding.time4.setOnClickListener { ledMatrix?.startTime4() }
        binding.snow.setOnClickListener { ledMatrix?.startSnow() }
        binding.plasma.setOnClickListener { ledMatrix?.startPlasma() }
        binding.mandelbrot.setOnClickListener { ledMatrix?.mandelbrot() }
    }
}