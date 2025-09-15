package com.darvader.livescore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.livescore.databinding.ActivityLiveScoreMainBinding

class LiveScoreMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLiveScoreMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize view binding
        binding = ActivityLiveScoreMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: Implement LiveScore functionality
        // This should contain the actual LiveScore logic that can be used by the main app
    }
}
