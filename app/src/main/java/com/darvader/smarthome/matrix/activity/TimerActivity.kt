package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.databinding.ActivityTimerBinding
import com.darvader.smarthome.matrix.activity.LedMatrixActivity

class TimerActivity : AppCompatActivity() {
    private lateinit var ledMatrix: LedMatrix
    lateinit var binding: ActivityTimerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        this.ledMatrix = LedMatrixActivity.ledMatrix

        binding.minutesSlider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val totalSeconds = (value.toInt() * 60) + binding.secondsSlider.value.toInt()
                ledMatrix.setTime(totalSeconds)
                binding.timeValue.text = "${totalSeconds}s"
            }
        }

        binding.secondsSlider.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val totalSeconds = (binding.minutesSlider.value.toInt() * 60) + value.toInt()
                ledMatrix.setTime(totalSeconds)
                binding.timeValue.text = "${totalSeconds}s"
            }
        }

        binding.start.setOnClickListener {
            ledMatrix.startTimer()
        }

        binding.pause.setOnClickListener {
            ledMatrix.pauseTimer()
        }

        binding.reset.setOnClickListener {
            ledMatrix.reset()
        }
    }
}
