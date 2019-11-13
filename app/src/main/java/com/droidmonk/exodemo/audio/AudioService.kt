package com.droidmonk.exodemo.audio

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.tracks.Track
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory

class AudioService : Service() {

    val PLAYBACK_CHANNEL_ID = "playback_channel"
    val PLAYBACK_NOTIFICATION_ID = 1

    private val binder = AudioServiceBinder()
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var player: SimpleExoPlayer? = null

    private lateinit var currentTrack:Track

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())

        playerNotificationManager= PlayerNotificationManager.createWithNotificationChannel(
            applicationContext,
            PLAYBACK_CHANNEL_ID,
            R.string.foreground_service_notification_channel,
            PLAYBACK_NOTIFICATION_ID,
            object:PlayerNotificationManager.MediaDescriptionAdapter{
                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    return null
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return currentTrack.trackArtist
                }

                override fun getCurrentContentTitle(player: Player?): String {
                    return currentTrack.trackTitle
                }

                override fun getCurrentLargeIcon(
                    player: Player?,
                    callback: PlayerNotificationManager.BitmapCallback?
                ): Bitmap? {
                    return null
                }
            }

        )

        playerNotificationManager?.setNotificationListener(object : PlayerNotificationManager.NotificationListener{
            override fun onNotificationCancelled(notificationId: Int) {
                stopSelf()
            }

            override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                startForeground(notificationId,notification)
            }
        })

        playerNotificationManager?.setPlayer(player)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val track= intent?.getParcelableExtra<Track>("track")!!

        playAudio(track)

        return super.onStartCommand(intent, flags, startId)
    }

    inner class AudioServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): AudioService = this@AudioService
    }

    fun playAudio(track: Track)
    {

        currentTrack=track

        val dataSourceFactory: DefaultDataSourceFactory = DefaultDataSourceFactory(this,"Media Player")
        val mediaSource: ExtractorMediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.parse(track?.path))

        player?.prepare(mediaSource)
        player?.setPlayWhenReady(true)

    }
}
