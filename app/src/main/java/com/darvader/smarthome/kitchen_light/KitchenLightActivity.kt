package com.darvader.smarthome.kitchen_light

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import kotlinx.android.synthetic.main.activity_kitchen_light.*

class KitchenLightActivity : AppCompatActivity() {
    val kitchenLight = KitchenLight()

    init {
        SmartHomeActivity.echoServer.register(kitchenLight)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen_light)

        kitchenLight.detect()
    }
}