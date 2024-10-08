package com.darvader.smarthome.matrix.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.darvader.smarthome.databinding.ActivityCounterBinding
import com.darvader.smarthome.matrix.LedMatrix

class CounterActivity : AppCompatActivity() {

    public lateinit var binding: ActivityCounterBinding
    lateinit var ledMatrix : LedMatrix

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.ledMatrix = LedMatrixActivity.ledMatrix
        this.ledMatrix.counterActivity = this
        this.ledMatrix.mode = LedMatrix.Mode.counter
        binding = ActivityCounterBinding.inflate(layoutInflater)
        val view = binding.root
        ledMatrix.counter = ledMatrix.counter
        setContentView(view)

        val sharedPreferences = getSharedPreferences("CounterPrefs", Context.MODE_PRIVATE)
        ledMatrix.counter = sharedPreferences.getInt("counter", 0)
        binding.counterText.setText(ledMatrix.counter.toString())
        val editor = sharedPreferences.edit()

        binding.plusCounter.setOnClickListener {
            ledMatrix.plusCounter()
            storeCounter(editor)
            binding.counterText.setText(ledMatrix.counter.toString())
        }

        binding.minusCounter.setOnClickListener {
            ledMatrix.minusCounter()
            storeCounter(editor)
            binding.counterText.setText(ledMatrix.counter.toString())
        }

        binding.resetCounter.setOnClickListener {
            ledMatrix.counter = 0
            storeCounter(editor)
            binding.counterText.setText(ledMatrix.counter.toString())
        }

        binding.detect.setOnClickListener {
            ledMatrix.detect()
        }

        binding.counterText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something while text is being changed
            }

            override fun afterTextChanged(s: Editable?) {
                // Do something after text has changed
                val number = s.toString().toIntOrNull()
                if (number != null) {
                    ledMatrix.counter = number
                }
                storeCounter(editor)
            }
        })

    }

    private fun storeCounter(editor: SharedPreferences.Editor) {
        editor.putInt("counter", ledMatrix.counter)
        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}