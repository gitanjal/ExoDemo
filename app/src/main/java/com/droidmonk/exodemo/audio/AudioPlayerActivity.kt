package com.droidmonk.exodemo.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.tracks.Track
import com.droidmonk.exodemo.tracks.TracksFragment
import com.google.android.exoplayer2.util.Util

class AudioPlayerActivity : AppCompatActivity() {


    private lateinit var mService: AudioService
    private var mBound: Boolean = false

    companion object {
        private val KEY_TRACK="track"
        fun getCallingIntent(context: Context,track: Track):Intent
        {
            var intent=Intent(context,AudioPlayerActivity::class.java)
            intent.putExtra(KEY_TRACK,track)
            return intent
        }
    }

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AudioService.AudioServiceBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
    }

    override fun onStart() {
        super.onStart()

        val intent = Intent(this, AudioService::class.java)
        intent.putExtras(getIntent())
        Util.startForegroundService(this, intent)

        bindService(intent, connection, Context.BIND_AUTO_CREATE)

    }

    override fun onStop() {
        super.onStop()

        unbindService(connection)
        mBound = false
    }
}
