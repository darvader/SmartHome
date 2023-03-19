package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.widget.SeekBar
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

        binding.timeBar.setOnSeekBarChangeListener(object : LedMatrixActivity.ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledMatrix?.setTime(progress * 5)
                binding.timeText.text = "${progress * 5}"
            }
        })

        binding.start.setOnClickListener {
            ledMatrix.startTimer()
        }

        binding.pause.setOnClickListener {
            ledMatrix.pauseTimer()
        }

        binding.stopWatch.setOnClickListener {
            ledMatrix.stopWatch()
        }

        binding.stopWatchStart.setOnClickListener {
            ledMatrix.stopWatchStart()
        }

        binding.stopWatchStop.setOnClickListener {
            ledMatrix.stopWatchStop()
        }

    }
}
