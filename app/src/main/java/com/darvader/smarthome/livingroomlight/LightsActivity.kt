package com.darvader.smarthome.livingroomlight

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.databinding.ActivityLightsBinding

class LightsActivity : AppCompatActivity() {
    val lights = Lights()

    init {
        SmartHomeActivity.echoServer.register(lights)
    }

    private lateinit var binding: ActivityLightsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLightsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.numLamps.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lights.turnLivLightsOn(progress)
            }
        })
        binding.bedLightSwitch.setOnClickListener { lights.switchBedLight() }
        binding.bedLightDimmer.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lights.changeBed(progress)
            }
        })

        binding.kitchenLightDimmer.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lights.changeKitchen(progress)
            }
        })
        binding.kitchenLightSwitch.setOnClickListener { lights.switchKitchenLight() }

        lights.detect()

    }
}