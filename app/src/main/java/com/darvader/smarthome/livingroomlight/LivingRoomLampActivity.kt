package com.darvader.smarthome.livingroomlight

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.darvader.smarthome.R
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.SmartHomeActivity
import kotlinx.android.synthetic.main.activity_led_strip.*
import kotlinx.android.synthetic.main.activity_living_room_light.*

class LivingRoomLampActivity : AppCompatActivity() {
    val livingRoomLamp = LivingRoomLamp()

    init {
        SmartHomeActivity.echoServer.register(livingRoomLamp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_living_room_light)

        numLamps.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                livingRoomLamp.turnLampsOn(progress)
            }
        })
        livingRoomLamp.detect()

    }
}