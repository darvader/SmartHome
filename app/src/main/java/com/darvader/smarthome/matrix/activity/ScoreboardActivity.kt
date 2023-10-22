package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.databinding.ActivityScoreboardBinding
import com.darvader.smarthome.matrix.LedMatrix


class ScoreboardActivity : AppCompatActivity() {
    lateinit var ledMatrix : LedMatrix

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
        binding.reconnect.setOnClickListener {
            LiveScoreActivity.livescoreActivity?.reInitWebSocket()
        }

        binding.timeout.setOnClickListener { ledMatrix?.timeout() }
        binding.invert.setOnClickListener {
            ledMatrix?.invert()
            inform()
        }

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

        LiveScoreActivity.scoreboardActivity = this

        ledMatrix?.startScoreboard()
        if (LiveScoreActivity.webSocketClient != null) {
            inform()
        }
    }

    fun inform() {
        val match = LiveScoreActivity.match!!
        ledMatrix.setsLeft = if (ledMatrix.switch) match.setPointsTeam2.toByte() else match.setPointsTeam1.toByte()
        ledMatrix.setsRight = if (ledMatrix.switch) match.setPointsTeam1.toByte() else match.setPointsTeam2.toByte()
        val size = match.matchSets.size
        if (size>0) {
            val lastSet = match.matchSets[size - 1]
            ledMatrix.pointsLeft = if (ledMatrix.switch) lastSet.team2.toByte() else lastSet.team1.toByte()
            ledMatrix.pointsRight = if (ledMatrix.switch) lastSet.team1.toByte() else lastSet.team2.toByte()
            ledMatrix.leftTeamServes = if (match.leftTeamServes && !ledMatrix.switch) 1 else 0
        }

        val text = if (!ledMatrix.invert xor ledMatrix.switch) "${match.teamDescription1}:${match.teamDescription2}" else "${match.teamDescription2}:${match.teamDescription1}"
        if (text != binding.scrollText.text.toString()) {
            runOnUiThread {
                binding.scrollText.setText(text)
            }
        }
        ledMatrix.updateScore()
    }
}
