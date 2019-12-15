package com.droidmonk.exodemo.audio

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.tracks.Track
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class AudioService : MediaBrowserServiceCompat() {

    val LOG_TAG="tag"

    companion object{
         val KEY_TRACK="track"
         val KEY_ADD_TO_PLAYLIST="add_to_playlist"
    }



    val PLAYBACK_CHANNEL_ID = "playback_channel"
    val PLAYBACK_NOTIFICATION_ID = 1

//    private val binder = AudioServiceBinder()
    private var playerNotificationManager: PlayerNotificationManager? = null
    private var player: SimpleExoPlayer? = null
    private lateinit var concatenatedSource: ConcatenatingMediaSource

    private lateinit var currentTrack:Track

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var stateBuilder: PlaybackStateCompat.Builder

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

        /*media session*/
      /*  mediaSession=MediaSessionCompat(this,"ExoDemo")
        mediaSession.isActive=true*/


        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(object: MediaSessionCompat.Callback() {
                override fun onPlay() {
                    val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager

                    player?.setPlayWhenReady(true);
                    player?.getPlaybackState();

                    // Request audio focus for playback, this registers the afChangeListener

                    /*audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                        setOnAudioFocusChangeListener(afChangeListener)
                        setAudioAttributes(AudioAttributes.Builder().run {
                            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            build()
                        })
                        build()
                    }
                    val result = am.requestAudioFocus(audioFocusRequest)
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        // Start the service
                        startService(Intent(this@AudioService, MediaBrowserService::class.java))
                        // Set the session active  (and update metadata and state)
                        mediaSession.isActive = true
                        // start the player (custom call)
                        player.start()
                        // Register BECOME_NOISY BroadcastReceiver
                        registerReceiver(myNoisyAudioStreamReceiver, intentFilter)
                        // Put the service in the foreground, post notification
                        service.startForeground(id, myPlayerNotification)
                    }*/
                }

                override fun onPause() {
                    super.onPause()

                    player?.setPlayWhenReady(false);
                    player?.getPlaybackState();
                }

                override fun onSetRepeatMode(repeatMode: Int) {
                    super.onSetRepeatMode(repeatMode)

                    player?.setRepeatMode(Player.REPEAT_MODE_ALL);
                }

                override fun onSetShuffleMode(shuffleMode: Int) {
                    super.onSetShuffleMode(shuffleMode)

                }


            })

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
        playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)
        mediaSessionConnector= MediaSessionConnector(mediaSession)
        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession ){
            override fun getMediaDescription(
                player: Player?,
                windowIndex: Int
            ): MediaDescriptionCompat {
                return player?.currentTimeline
                    ?.getWindow(windowIndex, Timeline.Window(), true)?.tag as MediaDescriptionCompat
            }
        })

        mediaSession.isActive=true
//        mediaSessionConnector.setPlayer(player,null)
    }

   /* override fun onBind(intent: Intent): IBinder {
        return binder
    }*/
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

   /* inner class AudioServiceBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): AudioService = this@AudioService
    }*/

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


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        return if (/*allowBrowsing(clientPackageName, clientUid)*/true) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot("Hi", null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot("hi", null)
        }
    }


    override fun onDestroy() {
        mediaSession.release()
//        mediaSessionConnector.setPlayer(null,null)
        playerNotificationManager?.setPlayer(null)
        player?.release()
        player=null
        super.onDestroy()
    }




}
