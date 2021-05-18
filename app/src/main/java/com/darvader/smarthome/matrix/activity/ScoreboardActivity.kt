package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import kotlinx.android.synthetic.main.activity_scoreboard.*


class ScoreboardActivity : AppCompatActivity() {
    var ledMatrix : LedMatrix? = null

    abstract class ProgressChangedListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.ledMatrix = LedMatrixActivity.ledMatrix

        ledMatrix?.scoreboardActivity = this
        setContentView(R.layout.activity_scoreboard)
        pointsUpLeft.setOnClickListener { ledMatrix?.pointsLeftUp() }
        pointsDownLeft.setOnClickListener { ledMatrix?.pointsLeftDown() }
        pointsUpRight.setOnClickListener { ledMatrix?.pointsRightUp() }
        pointsDownRight.setOnClickListener { ledMatrix?.pointsRightDown() }
        ballLeft.setOnClickListener { ledMatrix?.ballLeft() }
        ballRight.setOnClickListener { ledMatrix?.ballRight() }

        reset_points.setOnClickListener { ledMatrix?.clearPoints() }

        setsUpLeft.setOnClickListener { ledMatrix?.setsLeftUp() }
        setsDownLeft.setOnClickListener { ledMatrix?.setsLeftDown() }
        setsUpRight.setOnClickListener { ledMatrix?.setsRightUp() }
        setsDownRight.setOnClickListener { ledMatrix?.setsRightDown() }
        switchButton.setOnClickListener { ledMatrix?.switch() }

        timeout.setOnClickListener { ledMatrix?.timeout() }
        invert.setOnClickListener { ledMatrix?.invert() }

        reset.setOnClickListener { ledMatrix?.reset() }
        off.setOnClickListener { ledMatrix?.off() }



        scrollText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ledMatrix?.setScrollText(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        ledMatrix?.startScoreboard()
    }
}
