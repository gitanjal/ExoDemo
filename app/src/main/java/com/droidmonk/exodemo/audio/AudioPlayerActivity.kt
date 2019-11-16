package com.droidmonk.exodemo.audio

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.tracks.Track
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_audio_player.*
import java.io.IOException


class AudioPlayerActivity : AppCompatActivity() {


    private lateinit var mMediaBrowserCompat: MediaBrowserCompat
    private lateinit var mediaController: MediaControllerCompat
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

    private val mMediaControllerCompatCallback = object : MediaControllerCompat.Callback() {

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)

        }
    }

    private val mediaBrowserConnectionCallback = object : MediaBrowserCompat.ConnectionCallback(){
        override fun onConnected() {
            super.onConnected()

            try {
/*                mediaController = MediaControllerCompat(this@AudioPlayerActivity, mMediaBrowserCompat.sessionToken).apply {
                    registerCallback(mMediaControllerCompatCallback)
                }

                MediaControllerCompat.setMediaController(this@AudioPlayerActivity, mediaController)*/


                mMediaBrowserCompat.sessionToken.also{token->
                     mediaController = MediaControllerCompat(
                        this@AudioPlayerActivity, // Context
                        token
                    )
                }

                MediaControllerCompat.setMediaController(this@AudioPlayerActivity, mediaController)

                buildTransportControls()

            } catch (e: RemoteException) {

                Log.d("tag","Remote exception")

            }
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        mMediaBrowserCompat = MediaBrowserCompat(
            this, ComponentName(this, AudioService::class.java),
            mediaBrowserConnectionCallback, intent.extras
        )

        mMediaBrowserCompat.connect()



        btn_pause.setOnClickListener({

            mediaController.transportControls.pause()
        })

        btn_play.setOnClickListener({

            mediaController.transportControls.play()
        })

        btn_repeat.setOnClickListener({
            mediaController.transportControls.setRepeatMode(Player.REPEAT_MODE_ALL)
        })

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


    fun buildTransportControls() {
        val mediaController = MediaControllerCompat.getMediaController(this@AudioPlayerActivity)
        // Grab the view for the play/pause button
        btn_play.apply {
            setOnClickListener {
                // Since this is a play/pause button, you'll need to test the current state
                // and choose the action accordingly

                val pbState = mediaController.playbackState.state
                if (pbState == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.transportControls.pause()
                } else {
                    mediaController.transportControls.play()
                }
            }
        }

        // Display the initial state
        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        // Register a Callback to stay in sync
        mediaController.registerCallback(controllerCallback)
    }

    private var controllerCallback = object : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {}

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {}
    }





}
