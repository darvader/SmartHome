package com.darvader.smarthome.matrix.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import kotlinx.android.synthetic.main.activity_sample.*

class TimeActivity() : AppCompatActivity() {

    private var ledMatrix: LedMatrix? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample)
        this.ledMatrix = LedMatrixActivity.ledMatrix

        time1.setOnClickListener { ledMatrix?.startTime1() }
        time2.setOnClickListener { ledMatrix?.startTime2() }
        time3.setOnClickListener { ledMatrix?.startTime3() }
        time4.setOnClickListener { ledMatrix?.startTime4() }
        snow.setOnClickListener { ledMatrix?.startSnow() }
        mandelbrot.setOnClickListener { ledMatrix?.mandelbrot() }
    }
}