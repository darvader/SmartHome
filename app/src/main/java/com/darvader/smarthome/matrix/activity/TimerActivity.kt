package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {
    private var ledMatrix: LedMatrix? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        this.ledMatrix = LedMatrixActivity.ledMatrix

        timeBar.setOnSeekBarChangeListener(object : LedMatrixActivity.ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledMatrix?.setTime(progress * 5)
                timeText.setText("${progress * 5}")
            }
        })

        start.setOnClickListener {
            ledMatrix?.startTimer()
        }

        pause.setOnClickListener {
            ledMatrix?.pauseTimer()
        }

        stopWatch.setOnClickListener {
            ledMatrix?.stopWatch()
        }

        stopWatchStart.setOnClickListener {
            ledMatrix?.stopWatchStart()
        }

        stopWatchStop.setOnClickListener {
            ledMatrix?.stopWatchStop()
        }

    }
}
