package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import com.darvader.smarthome.databinding.ActivityScoreboardBinding
import com.darvader.smarthome.matrix.activity.LedMatrixActivity


class ScoreboardActivity : AppCompatActivity() {
    var ledMatrix : LedMatrix? = null

    abstract class ProgressChangedListener : SeekBar.OnSeekBarChangeListener {
        override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    }

    lateinit var binding: ActivityScoreboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.ledMatrix = LedMatrixActivity.ledMatrix

        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        ledMatrix?.scoreboardActivity = this
        setContentView(R.layout.activity_scoreboard)
        binding.pointsUpLeft.setOnClickListener { ledMatrix?.pointsLeftUp() }
        binding.pointsDownLeft.setOnClickListener { ledMatrix?.pointsLeftDown() }
        binding.pointsUpRight.setOnClickListener { ledMatrix?.pointsRightUp() }
        binding.pointsDownRight.setOnClickListener { ledMatrix?.pointsRightDown() }
        binding.ballLeft.setOnClickListener { ledMatrix?.ballLeft() }
        binding.ballRight.setOnClickListener { ledMatrix?.ballRight() }

        binding.resetPoints.setOnClickListener { ledMatrix?.clearPoints() }

        binding.setsUpLeft.setOnClickListener { ledMatrix?.setsLeftUp() }
        binding.setsDownLeft.setOnClickListener { ledMatrix?.setsLeftDown() }
        binding.setsUpRight.setOnClickListener { ledMatrix?.setsRightUp() }
        binding.setsDownRight.setOnClickListener { ledMatrix?.setsRightDown() }
        binding.switchButton.setOnClickListener { ledMatrix?.switch() }

        binding.timeout.setOnClickListener { ledMatrix?.timeout() }
        binding.invert.setOnClickListener { ledMatrix?.invert() }

        binding.reset.setOnClickListener { ledMatrix?.reset() }
        binding.off.setOnClickListener { ledMatrix?.off() }



        binding.scrollText.addTextChangedListener(object : TextWatcher {
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
