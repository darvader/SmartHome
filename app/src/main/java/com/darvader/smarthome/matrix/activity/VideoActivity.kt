package com.darvader.smarthome.matrix.activity

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.darvader.smarthome.matrix.LedMatrix
import com.darvader.smarthome.R
import kotlinx.android.synthetic.main.activity_video.*
import wseemann.media.FFmpegMediaMetadataRetriever
import java.util.*
import kotlin.concurrent.schedule


class VideoActivity : AppCompatActivity() {
     var uri = "content://media/external/video/media/23274"  // tie fighter
     //var uri = "content://media/external/video/media/23344"  // xwing
     //var uri = "content://media/external/video/media/21862" // trump
    // var uri = "content://media/external/video/media/8233"
    var ledMatrix: LedMatrix? = null

    fun checkPermissionForReadExtertalStorage(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val result: Int =
                applicationContext.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    @Throws(Exception::class)
    fun requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                0x3
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        ledMatrix = LedMatrixActivity.ledMatrix
        ledMatrix?.pictureMode()

        if (!checkPermissionForReadExtertalStorage())
            requestPermissionForReadExtertalStorage()

        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATA
        )

        val selection = "${MediaStore.Video.Media.DISPLAY_NAME} == ?"
        val selectionArgs = arrayOf("TIEFighter.mp4")
        val sortOrder = "${MediaStore.Video.Media.DISPLAY_NAME} ASC"


        applicationContext.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (cursor.moveToNext()) {

                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val duration = cursor.getInt(durationColumn)
                val size = cursor.getInt(sizeColumn)
                val data = cursor.getString(dataColumn)
                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                println("Id: $id, name: $name, duration: $duration, size: $size, uri: $contentUri, data: $data")
            }
        }

        val mmr = FFmpegMediaMetadataRetriever()
        mmr.setDataSource(this, Uri.parse(uri))
        //mmr.setDataSource("/storage/3535-3761/Movies/XWingSmall.mp4")
        //mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM)
        //mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST)

        // var frame = 0L
        val startTime = System.currentTimeMillis()

        val bitmaps = ArrayList<Bitmap>()

        start.setOnClickListener {
            println("Start caching bitmaps")
            for (i in 0L..10000L) {
                val b: Bitmap? = mmr.getScaledFrameAtTime(
                    (i * 30000L)+3000000,
                    FFmpegMediaMetadataRetriever.OPTION_CLOSEST,
                    64,
                    32
                )

                if (b != null) {
                    bitmaps.add(b)
                    println("Bitmap $i added.")
                }
            }

            var i = 0
            Timer("drawViewUpdater", true).schedule(1000, 1000 / 20) {
                ledMatrix?.sendBitmap(bitmaps[i++])
                if (i>=bitmaps.size) {
                    i = 0
                }
            }
        }
    }
}