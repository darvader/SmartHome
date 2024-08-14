package com.darvader.smarthome

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darvader.smarthome.booster.BoosterActivity
import com.darvader.smarthome.circulation.CirculationPumpActivity
import com.darvader.smarthome.databinding.ActivitySmartHomeBinding
import com.darvader.smarthome.ledstrip.LedStrip
import com.darvader.smarthome.ledstrip.LedStripActivity
import com.darvader.smarthome.ledstrip.christmas.ChristmasTreeActivity
import com.darvader.smarthome.livingroomlight.LightsActivity
import com.darvader.smarthome.matrix.activity.LedMatrixActivity

class SmartHomeActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 123
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

    private fun requestPermissionStorage() {
        if (!haveStoragePermission()) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    private fun hasPermissionsHotspot(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissionsHotspot() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.WRITE_SETTINGS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE)
    }

    private fun createHotspot() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // For Android 8.0 and above
            enableHotspotOreo(wifiManager)
        } else {
            // For Android 7.1 and below
            enableHotspotLegacy(wifiManager)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun enableHotspotOreo(wifiManager: WifiManager) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = "andi_hotspot"
            preSharedKey = "1q2w3e4r"
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        }

        try {
            val method = wifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(wifiManager, wifiConfig, true)
        } catch (e: Exception) {
            Log.e("Hotspot", "Failed to enable hotspot", e)
        }
    }

    private fun enableHotspotLegacy(wifiManager: WifiManager) {
        val wifiConfig = WifiConfiguration().apply {
            SSID = "andi_hotspot"
            preSharedKey = "1q2w3e4r"
            allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
        }

        try {
            val method = wifiManager.javaClass.getMethod("setWifiApEnabled", WifiConfiguration::class.java, Boolean::class.javaPrimitiveType)
            method.invoke(wifiManager, wifiConfig, true)
        } catch (e: Exception) {
            Log.e("Hotspot", "Failed to enable hotspot", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_smart_home)

        if (!hasPermissionsHotspot()) {
            requestPermissionsHotspot()
        } else {
            // Permissions are already granted, proceed with creating hotspot
            createHotspot()
        }

        if (haveStoragePermission()) {
        } else {
            requestPermissionStorage()
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
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with creating hotspot
                createHotspot()
            } else {
                // Handle the case where permissions are not granted
            }
        }
    }

    private fun allOff() {
        println("All off called.")
        LedStrip.echoClient.sendBroadCast("off")
    }
}