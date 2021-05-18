package com.darvader.smarthome

import android.widget.SeekBar

abstract class ProgressChangedListener : SeekBar.OnSeekBarChangeListener {
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}