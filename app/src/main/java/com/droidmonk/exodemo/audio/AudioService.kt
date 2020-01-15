package com.droidmonk.exodemo.audio

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.method.TextKeyListener.clear
import androidx.media.MediaBrowserServiceCompat
import com.droidmonk.exodemo.App
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.tracks.Track
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector.PlaybackPreparer
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory


class AudioService : MediaBrowserServiceCompat() {

    val LOG_TAG="AudioService"

    val PLAYBACK_CHANNEL_ID = "audio_channel"
    val PLAYBACK_NOTIFICATION_ID = 1

    private var playerNotificationManager: PlayerNotificationManager? = null
    private var player: SimpleExoPlayer? = null
    private var concatenatedSource: ConcatenatingMediaSource= ConcatenatingMediaSource()

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    private var currentTrack:Track? = null
    private val playlist= ArrayList<Track>()

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        playerNotificationManager= PlayerNotificationManager.createWithNotificationChannel(
            applicationContext,
            PLAYBACK_CHANNEL_ID,
            R.string.foreground_service_notification_channel,
            R.string.foreground_service_notification_channel_description,
            PLAYBACK_NOTIFICATION_ID,
            object:PlayerNotificationManager.MediaDescriptionAdapter{
                override fun createCurrentContentIntent(player: Player?): PendingIntent? {
                    var intent=AudioPlayerActivity.getCallingIntent(this@AudioService,null)
                    return PendingIntent.getActivity(this@AudioService,0,intent,PendingIntent.FLAG_UPDATE_CURRENT)
                }

                override fun getCurrentContentText(player: Player?): String? {
                    return playlist[player!!.currentWindowIndex].trackArtist
                }

                override fun getCurrentContentTitle(player: Player?): String {
                    return playlist[player!!.currentWindowIndex].trackTitle?:"Unknown"
                }

                override fun getCurrentLargeIcon(
                    player: Player?,
                    callback: PlayerNotificationManager.BitmapCallback?
                ): Bitmap? {

                    try {
                        var mediaDataRetriever = MediaMetadataRetriever()
                        mediaDataRetriever.setDataSource(
                            this@AudioService,
                            Uri.parse(playlist[player!!.currentWindowIndex].path)
                        )

                        var songImage: Bitmap? = null
                        mediaDataRetriever.embeddedPicture?.let {

                            val albumArt: ByteArray = mediaDataRetriever.embeddedPicture
                            songImage = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size);
                        }
                        return songImage
                    }catch (e:Exception)
                    {
                        return null
                    }
                }
            },
            object : PlayerNotificationManager.NotificationListener{
                override fun onNotificationCancelled(notificationId: Int) {
                    stopSelf()
                }

                override fun onNotificationStarted(notificationId: Int, notification: Notification?) {
                    startForeground(notificationId,notification)
                }
            }
        )

        playerNotificationManager?.setPlayer(player)

        mediaSession = MediaSessionCompat(baseContext, LOG_TAG)
        sessionToken=mediaSession.sessionToken
        mediaSession.isActive=true

        playerNotificationManager?.setMediaSessionToken(mediaSession.sessionToken)
        mediaSessionConnector= MediaSessionConnector(mediaSession)
        mediaSessionConnector.setQueueNavigator(object : TimelineQueueNavigator(mediaSession ){
            override fun getMediaDescription(
                player: Player?,
                windowIndex: Int
            ): MediaDescriptionCompat {
                return MediaDescriptionCompat.Builder()
                    .setTitle(playlist[windowIndex].trackTitle)
                    .setIconUri(playlist[windowIndex].iconUri)
                    .build()
            }
        })


        mediaSessionConnector.setPlayer(player)
        mediaSessionConnector.setPlaybackPreparer(object: MediaSessionConnector.PlaybackPreparer{
            override fun onPrepareFromSearch(
                query: String?,
                playWhenReady: Boolean,
                extras: Bundle?
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCommand(
                player: Player?,
                controlDispatcher: ControlDispatcher?,
                command: String?,
                extras: Bundle?,
                cb: ResultReceiver?
            ): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


            }

            override fun getSupportedPrepareActions(): Long {
                return PlaybackStateCompat.ACTION_PLAY_FROM_URI
            }

            override fun onPrepareFromMediaId(
                mediaId: String?,
                playWhenReady: Boolean,
                extras: Bundle?
            ) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPrepareFromUri(uri: Uri?, playWhenReady: Boolean, extras: Bundle?) {

                val trackToPlay:Track?=extras?.getParcelable("track")
                val addToPlayList= extras?.getBoolean("playlist")?:false


                val dataSourceFactory =
                    DefaultDataSourceFactory(this@AudioService, "ExoDemo")

                val cachedDataSourceFactory =CacheDataSourceFactory(
                    (application as App).appContainer.downloadCache, dataSourceFactory);

                val mediaSource: ProgressiveMediaSource =
                    ProgressiveMediaSource.Factory(cachedDataSourceFactory).createMediaSource(
                        uri
                    )



                if(addToPlayList)
                {
                    trackToPlay?.let { playlist.add(it) }

                    //add to playlist
                    concatenatedSource.addMediaSource(mediaSource)

                    //check if it has only one item
                    if(concatenatedSource.size==1)
                    {

                        player?.prepare(concatenatedSource)
                        player?.setPlayWhenReady(true)
                    }
                }
                else {
                    //clear our playlist
                    playlist.clear()
                    trackToPlay?.let { playlist.add(it) }

                    //start playback immediately
                    if (trackToPlay != null && !trackToPlay?.path.equals(currentTrack?.path)) {
                        currentTrack = extras?.getParcelable("track")

                        concatenatedSource = ConcatenatingMediaSource(mediaSource)

                        player?.prepare(concatenatedSource)
                        player?.setPlayWhenReady(true)
                    }
                }
            }

            override fun onPrepare(playWhenReady: Boolean) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })




    }



    fun playAudio(track: Track)
    {

       // currentTrack=track

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

        return BrowserRoot("empty_root", null)

    }


    override fun onDestroy() {
        mediaSession.release()
        mediaSessionConnector.setPlayer(null)
        playerNotificationManager?.setPlayer(null)
        player?.release()
        player=null
        super.onDestroy()
    }




}
