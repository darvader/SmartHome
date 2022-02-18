package com.darvader.smarthome.livingroomlight

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import kotlinx.android.synthetic.main.activity_kitchen_light.*
import kotlinx.android.synthetic.main.activity_lights.*

class LightsActivity : AppCompatActivity() {
    val lights = Lights()

    init {
        SmartHomeActivity.echoServer.register(lights)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lights)

        numLamps.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lights.turnLivLightsOn(progress)
            }
        })
        bedLightSwitch.setOnClickListener { lights.switchBedLight() }
        bedLightDimmer.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lights.changeBed(progress)
            }
        })

        kitchenLightDimmer.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                lights.changeKitchen(progress)
            }
        })
        kitchenLightSwitch.setOnClickListener { lights.switchKitchenLight() }

        lights.detect()

    }
}