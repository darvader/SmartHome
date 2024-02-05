package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.databinding.ActivityScoreboardBinding
import com.darvader.smarthome.matrix.LedMatrix
import java.util.*
import kotlin.concurrent.schedule


class ScoreboardActivity : AppCompatActivity() {
    lateinit var ledMatrix : LedMatrix
    private var timer: TimerTask? = null

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


        ledMatrix.scoreboardActivity = this
        binding.pointsUpLeft.setOnClickListener { ledMatrix.pointsLeftUp() }
        binding.pointsDownLeft.setOnClickListener { ledMatrix.pointsLeftDown() }
        binding.pointsUpRight.setOnClickListener { ledMatrix.pointsRightUp() }
        binding.pointsDownRight.setOnClickListener { ledMatrix.pointsRightDown() }
        binding.ballLeft.setOnClickListener { ledMatrix.ballLeft() }
        binding.ballRight.setOnClickListener { ledMatrix.ballRight() }

        binding.resetPoints.setOnClickListener { ledMatrix.clearPoints() }

        binding.setsUpLeft.setOnClickListener { ledMatrix.setsLeftUp() }
        binding.setsDownLeft.setOnClickListener { ledMatrix.setsLeftDown() }
        binding.setsUpRight.setOnClickListener { ledMatrix.setsRightUp() }
        binding.setsDownRight.setOnClickListener { ledMatrix.setsRightDown() }
        binding.switchButton.setOnClickListener { ledMatrix.switch() }
        binding.reconnect.setOnClickListener {
            LiveScoreActivity.livescoreActivity?.reInitWebSocket()
            ledMatrix.updateScore()
        }

        binding.timeout.setOnClickListener { ledMatrix.timeout() }
        binding.invert.setOnClickListener {
            ledMatrix.invert()
            inform()
        }

        binding.reset.setOnClickListener { ledMatrix.reset() }
        binding.off.setOnClickListener { ledMatrix.off() }



        binding.brightness.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledMatrix.changeBrightness(progress)
            }
        })
        binding.scrollText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ledMatrix.setScrollText(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        LiveScoreActivity.scoreboardActivity = this

        ledMatrix.startScoreboard()
        if (LiveScoreActivity.webSocketClient != null) {
            inform()
        }
        timer = Timer("informer", true).schedule(1000, 1000 / 1) {
            runOnUiThread {
                ledMatrix.updateScore()
            }
        }
    }

    fun inform() {
        if (LiveScoreActivity.match == null)
            return
        val match = LiveScoreActivity.match!!
        val size = match.matchSets.size

        if (size>0) {
            val lastSet = match.matchSets[size - 1]
            // switch on a new set
            if ((ledMatrix.pointsLeft > 0 || ledMatrix.pointsRight > 0) && (lastSet.team1 == 0 && lastSet.team2 == 0))
                ledMatrix.switch = !ledMatrix.switch
            // switch on tie-break at 8 points in set 5
            if ((lastSet.team1 == 8 || lastSet.team2.toInt() == 8) && size == 5 && (ledMatrix.pointsLeft < 8 && ledMatrix.pointsRight < 8))
                ledMatrix.switch = !ledMatrix.switch

            ledMatrix.pointsLeft = if (ledMatrix.switch) lastSet.team2.toByte() else lastSet.team1.toByte()
            ledMatrix.pointsRight = if (ledMatrix.switch) lastSet.team1.toByte() else lastSet.team2.toByte()
            ledMatrix.leftTeamServes = if (match.leftTeamServes && !ledMatrix.switch) 1 else 0
        }
        ledMatrix.setsLeft = if (ledMatrix.switch) match.setPointsTeam2.toByte() else match.setPointsTeam1.toByte()
        ledMatrix.setsRight = if (ledMatrix.switch) match.setPointsTeam1.toByte() else match.setPointsTeam2.toByte()

        val text = if (!ledMatrix.invert xor ledMatrix.switch) "${match.teamDescription1}:${match.teamDescription2}" else "${match.teamDescription2}:${match.teamDescription1}"
        runOnUiThread {
            binding.scrollText.setText(text)
        }
        ledMatrix.updateScore()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel();
    }

}
