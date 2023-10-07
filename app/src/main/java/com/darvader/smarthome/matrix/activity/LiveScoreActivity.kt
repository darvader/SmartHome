package com.darvader.smarthome.matrix.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.databinding.ActivityLiveScoreBinding

class LiveScoreActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLiveScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}