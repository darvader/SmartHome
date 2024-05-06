package com.darvader.smarthome.circulation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.darvader.smarthome.R
import com.darvader.smarthome.databinding.ActivityCirculationPumpBinding
import com.darvader.smarthome.databinding.ActivityLightsBinding

class CirculationPumpActivity : AppCompatActivity() {
    val circulationPump = CirculationPump()

    private lateinit var binding: ActivityCirculationPumpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCirculationPumpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.onCirculation.setOnClickListener { circulationPump.on() }
        binding.offCirculation.setOnClickListener { circulationPump.off() }
        binding.circ5Min.setOnClickListener { circulationPump.circ5Min() }

    }
}