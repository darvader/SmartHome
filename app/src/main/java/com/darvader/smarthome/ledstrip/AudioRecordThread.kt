package com.darvader.smarthome.ledstrip

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlin.math.abs
import kotlin.math.roundToInt


class AudioRecordThread : Runnable {
    var drawView: DrawView? = null
    var stopped = false

    val fft = FFT(512)

    private val echoClient = LedStrip.echoClient

    @Override
    override fun run() {
        var buffers = Array(256) {
            ShortArray(
                512
            )
        }
        var ix = 0

        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        var recorder: AudioRecord
        var N= AudioRecord.getMinBufferSize(
            20000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        );
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            20000,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            N * 10
        );

        recorder.startRecording()

        // ... loop
        while (!stopped) {
            val buffer: ShortArray = buffers.get(ix++ % buffers.size)
            N = recorder.read(buffer, 0, buffer.size)
            //process is what you will do with the data...not defined here
            process(buffer)
        }
        recorder.stop()

    }

    var factor = 1f

    fun process(buffer: ShortArray) {
        val x = DoubleArray(512)
        val y = DoubleArray(512)
        buffer.forEachIndexed { i, b -> x[i] = b.toDouble()
            y[i] = 0.0}
        fft.fft(x, y)

        x[0] = 0.0
        x[1] = 0.0

        val f = FloatArray(512)
        var max = 0.0f
        x.forEachIndexed { i, d -> val fl = d.toFloat()
            if (i>=x.size/2) return@forEachIndexed
            f[i] = fl
            // get max but ignoring first 20 calculations
            if (fl > max && i > 20) max = fl
        }
        if (max*factor < 200) factor *= 1.01f
        else if (max*factor > 255) factor = 255f / max

        f.forEachIndexed { i, fl -> f[i] = fl * factor }

        drawView?.setBuffer(f)
        val bytes = ByteArray(129)

        for (i in 0..127) {
            bytes[i] = abs(f[i*2]).roundToInt().toByte()
        }
        echoClient.send(bytes, LedStrip.currentAddress)
    }
}