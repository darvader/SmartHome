package com.darvader.smarthome

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
        // Modern hotspot creation requires directing user to system settings
        // The deprecated WifiConfiguration API is no longer reliable on modern Android
        try {
            val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "Please enable hotspot manually in settings", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.e("Hotspot", "Failed to open hotspot settings", e)
            Toast.makeText(this, "Unable to access hotspot settings", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySmartHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Check and request all permissions at startup
        checkAndRequestAllPermissions()

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

    private fun checkAndRequestAllPermissions() {

        // Check hotspot permissions
        if (!hasPermissionsHotspot()) {
            showPermissionExplanationDialog(
                "Network Permissions Required",
                "This app needs network and location permissions to create a WiFi hotspot for your smart home devices. This allows your devices to connect and communicate with each other.",
                "Grant Permissions"
            ) {
                requestPermissionsHotspot()
            }
            return // Request one at a time for better UX
        }

        // Check storage permissions
        if (!haveStoragePermission()) {
            showPermissionExplanationDialog(
                "Storage Permissions Required",
                "This app needs storage access to save and load smart home configurations, LED matrix patterns, and other app data.",
                "Grant Storage Access"
            ) {
                requestPermissionStorage()
            }
            return
        }

        // Check camera permissions
        if (!allPermissionsGranted()) {
            showPermissionExplanationDialog(
                "Camera Permission Required",
                "Camera access is needed for features like QR code scanning and visual device setup.",
                "Grant Camera Access"
            ) {
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
            }
            return
        }

        // All permissions granted, initialize hotspot
        createHotspot()
    }

    private fun showPermissionExplanationDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositiveAction: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveAction()
            }
            .setNegativeButton("Not Now") { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(this, "Some features may not work without permissions", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_PERMISSIONS -> {
                if (allPermissionsGranted()) {
                    Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT).show()
                    // All permissions granted, continue with app initialization
                    createHotspot()
                } else {
                    showPermissionDeniedDialog(
                        "Camera Permission Denied",
                        "Camera features will not be available. You can grant this permission later in Settings.",
                        "Open Settings"
                    ) {
                        openAppSettings()
                    }
                }
            }
            PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Network permissions granted!", Toast.LENGTH_SHORT).show()
                    // Continue with next permission check
                    checkAndRequestAllPermissions()
                } else {
                    showPermissionDeniedDialog(
                        "Network Permissions Denied",
                        "Hotspot and device connectivity features may not work properly. You can grant these permissions later in Settings.",
                        "Open Settings"
                    ) {
                        openAppSettings()
                    }
                }
            }
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Storage permission granted!", Toast.LENGTH_SHORT).show()
                    Log.d("Permissions", "Storage permissions granted")
                    // Continue with next permission check
                    checkAndRequestAllPermissions()
                } else {
                    showPermissionDeniedDialog(
                        "Storage Permission Denied",
                        "App configurations and LED patterns cannot be saved. You can grant this permission later in Settings.",
                        "Open Settings"
                    ) {
                        openAppSettings()
                    }
                }
            }
        }
    }

    private fun showPermissionDeniedDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        onPositiveAction: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ ->
                onPositiveAction()
            }
            .setNegativeButton("Continue") { dialog, _ ->
                dialog.dismiss()
                // Continue with next permission check even if this one was denied
                checkAndRequestAllPermissions()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun allOff() {
        println("All off called.")
        LedStrip.echoClient.sendBroadCast("off")
    }
}