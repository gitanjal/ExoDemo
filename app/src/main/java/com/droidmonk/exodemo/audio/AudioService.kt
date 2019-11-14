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
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class AudioService : Service() {

    companion object{
         val KEY_TRACK="track"
         val KEY_ADD_TO_PLAYLIST="add_to_playlist"
    }

    val PLAYBACK_CHANNEL_ID = "playback_channel"
    val PLAYBACK_NOTIFICATION_ID = 1

    private val binder = AudioServiceBinder()
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var player: SimpleExoPlayer? = null
    private lateinit var concatenatedSource: ConcatenatingMediaSource

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


        val track= intent?.getParcelableExtra<Track>(KEY_TRACK)!!
        val addToPlayList=intent.getBooleanExtra(KEY_ADD_TO_PLAYLIST,false)
        if(addToPlayList) {

            if(isPlaying())
                addAudioToPlaylist(track)
            else
                playAudio(track)
        }
        else
            playAudio(track)

        return START_STICKY
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


        concatenatedSource = ConcatenatingMediaSource(mediaSource)

        player?.prepare(concatenatedSource)
        player?.setPlayWhenReady(true)

    }


    fun addAudioToPlaylist(track: Track)
    {


        val dataSourceFactory: DefaultDataSourceFactory = DefaultDataSourceFactory(this,"Media Player")
        val mediaSource: ExtractorMediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(
            Uri.parse(track?.path))



        concatenatedSource.addMediaSource(mediaSource)


    }

    fun isPlaying(): Boolean {
        return player?.getPlaybackState() === Player.STATE_READY && player?.getPlayWhenReady()?:false
    }
}
