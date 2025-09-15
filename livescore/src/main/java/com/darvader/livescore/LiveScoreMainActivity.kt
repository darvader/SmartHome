package com.darvader.livescore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.matrix.activity.LiveScoreActivity

class LiveScoreMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Directly launch the LiveScoreActivity from the main app module
        val intent = Intent(this, LiveScoreActivity::class.java)
        startActivity(intent)

        // Finish this activity so the user doesn't see it
        finish()
    }
}
