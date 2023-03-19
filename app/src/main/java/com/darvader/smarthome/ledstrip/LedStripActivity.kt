package com.darvader.smarthome.ledstrip

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import com.darvader.smarthome.databinding.ActivityLedStripBinding
import com.darvader.smarthome.ledstrip.christmas.CalibrateActivity


class LedStripActivity : AppCompatActivity() {

    val ledStrip = LedStrip(this)
    init {
        SmartHomeActivity.echoServer.register(ledStrip)
    }

    lateinit var binding: ActivityLedStripBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led_strip)
        binding = ActivityLedStripBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.detect.setOnClickListener { ledStrip.detect() }
        binding.seven.setOnClickListener { ledStrip.seven() }
        binding.white.setOnClickListener { ledStrip.white() }
        binding.dot.setOnClickListener { ledStrip.dot() }
        binding.rain.setOnClickListener { ledStrip.rain() }
        binding.rainbow.setOnClickListener { ledStrip.rainbow() }
        binding.off.setOnClickListener { ledStrip.off() }
        binding.fft.setOnClickListener { ledStrip.fft() }
        binding.fftRow.setOnClickListener { ledStrip.fftRow() }
     /*   christmasHor.setOnClickListener { ledStrip.christmasHorizontal() }
        christmasVer.setOnClickListener { ledStrip.christmasVertical() }
        christmasZ.setOnClickListener { ledStrip.christmasZ() }
        christmasSevenH.setOnClickListener { ledStrip.christmasSevenH() }
        christmasRotationY.setOnClickListener { ledStrip.christmasRotationY() }
        christmasRotationX.setOnClickListener { ledStrip.christmasRotationX() }
        christmasRotationZ.setOnClickListener { ledStrip.christmasRotationZ() }
        christmasSmoothRotationY.setOnClickListener { ledStrip.christmasSmoothRotationY() }
        christmasSmoothRotationX.setOnClickListener { ledStrip.christmasSmoothRotationX() }
        christmasSmoothRotationZ.setOnClickListener { ledStrip.christmasSmoothRotationZ() }
        calib.setOnClickListener {
            if (LedStrip.currentAddress != "") {
                val intent = Intent(this, CalibrateActivity::class.java)
                startActivity(intent)
            }
        }*/
        binding.fftRowRemote.setOnClickListener {
            ledStrip.fftRowRemote()
            val intent = Intent(this, FFTLed::class.java)
            startActivity(intent)
        }
        binding.fftRemote.setOnClickListener {
            ledStrip.fftRemote()
            // Thread.sleep(1000)
            val intent = Intent(this, FFTLed::class.java)
            startActivity(intent)
        }


        binding.redSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeRed(progress.toByte())
            }
        })

        binding.greenSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeGreen(progress.toByte())
            }
        })

        binding.blueSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeBlue(progress.toByte())
            }
        })

        binding.whiteSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeWhite(progress.toByte())
            }
        })

        binding.divisor.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeDivisor(progress)
            }
        })

        binding.speed.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeSpeed(progress)
            }
        })
        ledStrip.detect()

    }
}
