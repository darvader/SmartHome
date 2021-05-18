package com.darvader.smarthome.matrix.activity

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.DrawView
import com.darvader.smarthome.R
import java.util.*
import kotlin.concurrent.schedule

class AnimationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_animations)

        val drawView = DrawView(this)
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);

        Timer("drawViewUpdater", false).schedule(0, 1000/10) {
            drawView.invalidate()
            print("invalidate called")
        }
    }
}