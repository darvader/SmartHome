package com.darvader.smarthome.ledstrip.christmas

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.databinding.ActivityChristmasTreeBinding
import com.darvader.smarthome.databinding.ActivityLedStripBinding


class ChristmasTreeActivity : AppCompatActivity() {

    companion object {
        val tree = ChristmasStrip()
    }
    init {
        SmartHomeActivity.echoServer.register(tree)
    }

    lateinit var binding: ActivityChristmasTreeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_christmas_tree)
        tree.christmasTreeActivity = this
        binding = ActivityChristmasTreeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        tree.detect()

        binding.christmasHor.setOnClickListener { tree.christmasHorizontal() }
        binding.christmasVer.setOnClickListener { tree.christmasVertical() }
        binding.christmasZ.setOnClickListener { tree.christmasZ() }
        binding.christmasSevenH.setOnClickListener { tree.christmasSevenH() }
        binding.christmasRotationY.setOnClickListener { tree.christmasRotationY() }
        binding.christmasRotationX.setOnClickListener { tree.christmasRotationX() }
        binding.christmasRotationZ.setOnClickListener { tree.christmasRotationZ() }
        binding.christmasSmoothRotationY.setOnClickListener { tree.christmasSmoothRotationY() }
        binding.christmasSmoothRotationX.setOnClickListener { tree.christmasSmoothRotationX() }
        binding.christmasSmoothRotationZ.setOnClickListener { tree.christmasSmoothRotationZ() }
        binding.calib.setOnClickListener {
            if (tree.currentAddress != "") {
                val intent = Intent(this, CalibrateActivity::class.java)
                startActivity(intent)
            }
        }

    }


}