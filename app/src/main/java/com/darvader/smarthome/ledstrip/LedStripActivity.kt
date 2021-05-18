package com.darvader.smarthome.ledstrip

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.ProgressChangedListener
import com.darvader.smarthome.R
import com.darvader.smarthome.SmartHomeActivity
import kotlinx.android.synthetic.main.activity_led_strip.*


class LedStripActivity : AppCompatActivity() {

    val ledStrip = LedStrip(this)
    init {
        SmartHomeActivity.echoServer.register(ledStrip)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_led_strip)
        detect.setOnClickListener { ledStrip.detect() }
        seven.setOnClickListener { ledStrip.seven() }
        white.setOnClickListener { ledStrip.white() }
        dot.setOnClickListener { ledStrip.dot() }
        rain.setOnClickListener { ledStrip.rain() }
        rainbow.setOnClickListener { ledStrip.rainbow() }
        off.setOnClickListener { ledStrip.off() }
        fft.setOnClickListener { ledStrip.fft() }
        fft_row.setOnClickListener { ledStrip.fftRow() }
        fft_row_remote.setOnClickListener {
            ledStrip.fftRowRemote()
            val intent = Intent(this, FFTLed::class.java)
            startActivity(intent)
        }
        fft_remote.setOnClickListener {
            ledStrip.fftRemote()
            // Thread.sleep(1000)
            val intent = Intent(this, FFTLed::class.java)
            startActivity(intent)
        }


        redSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeRed(progress.toByte())
            }
        })

        greenSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeGreen(progress.toByte())
            }
        })

        blueSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeBlue(progress.toByte())
            }
        })

        whiteSeekBar.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeWhite(progress.toByte())
            }
        })

        divisor.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeDivisor(progress)
            }
        })

        speed.setOnSeekBarChangeListener(object : ProgressChangedListener() {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                ledStrip.changeSpeed(progress)
            }
        })
        ledStrip.detect()

    }
}
