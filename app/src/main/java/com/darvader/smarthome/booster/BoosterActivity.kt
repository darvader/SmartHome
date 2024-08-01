package com.darvader.smarthome.booster

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.databinding.ActivityBoosterBinding
import java.util.Timer
import java.util.TimerTask

class BoosterActivity : AppCompatActivity() {
    val booster = Booster(this)

    public lateinit var binding: ActivityBoosterBinding
    val timer = Timer()

    init {
        SmartHomeActivity.echoServer.register(booster)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoosterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.on.setOnClickListener { booster.on() }
        binding.off.setOnClickListener { booster.off() }
        binding.boosterBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                booster.changeBooster(progress)
            }
        })

        val task = object : TimerTask() {
            override fun run() {
                booster.status()
            }
        }

        timer.scheduleAtFixedRate(task, 0, 5000)

    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
}