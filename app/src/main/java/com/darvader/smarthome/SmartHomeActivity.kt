package com.darvader.smarthome

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darvader.smarthome.booster.BoosterActivity
import com.darvader.smarthome.circulation.CirculationPumpActivity
import com.darvader.smarthome.databinding.ActivitySmartHomeBinding
import com.darvader.smarthome.ledstrip.LedStrip
import com.darvader.smarthome.matrix.activity.LedMatrixActivity
import com.darvader.smarthome.ledstrip.LedStripActivity
import com.darvader.smarthome.ledstrip.christmas.ChristmasTreeActivity
import com.darvader.smarthome.livingroomlight.LightsActivity

class SmartHomeActivity : AppCompatActivity() {

    companion object {
        val echoServer = EchoServer()
        val echoClient = EchoClient()
        init {
            echoServer.start()
        }

        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    }

    private lateinit var binding: ActivitySmartHomeBinding
    /** The request code for requesting [Manifest.permission.READ_EXTERNAL_STORAGE] permission. */
    private val READ_EXTERNAL_STORAGE_REQUEST = 0x1045



    private fun haveStoragePermission() =
        ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_home)
        if (haveStoragePermission()) {
        } else {
            requestPermission()
        }

        if (allPermissionsGranted()) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }



        binding = ActivitySmartHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.ledstrips.setOnClickListener {
            val intent = Intent(this, LedStripActivity::class.java)
            startActivity(intent)
        }

        binding.ledMatrix.setOnClickListener {
            val intent = Intent(this, LedMatrixActivity::class.java)
            startActivity(intent)
        }

        binding.livingRoomLamp.setOnClickListener {
            val intent = Intent(this, LightsActivity::class.java)
            startActivity(intent)
        }

        binding.christmasTree.setOnClickListener {
            val intent = Intent(this, ChristmasTreeActivity::class.java)
            startActivity(intent)
        }

        binding.circulationPump.setOnClickListener {
            val intent = Intent(this, CirculationPumpActivity::class.java)
            startActivity(intent)
        }

        binding.booster.setOnClickListener {
            val intent = Intent(this, BoosterActivity::class.java)
            startActivity(intent)
        }

        binding.allOff.setOnClickListener { allOff() }

    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allOff() {
        println("All off called.")
        LedStrip.echoClient.sendBroadCast("off")
    }
}