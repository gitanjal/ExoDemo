package com.droidmonk.exodemo.audio

import android.app.Notification
import android.app.PendingIntent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.tracks.Track
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory


class AudioService : MediaBrowserServiceCompat() {

    val LOG_TAG="AudioService"

    private var player: SimpleExoPlayer? = null

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    private var currentTrack:Track? = null

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG)
        sessionToken=mediaSession.sessionToken
        mediaSession.isActive=true

        mediaSessionConnector= MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(player)
        mediaSessionConnector.setPlaybackPreparer(object: MediaSessionConnector.PlaybackPreparer{
            override fun onPrepareFromSearch(query: String?,playWhenReady: Boolean,extras: Bundle?) {
            }
            override fun onCommand(player: Player?,controlDispatcher: ControlDispatcher?,command: String?,extras: Bundle?,cb: ResultReceiver?): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onPrepareFromMediaId(mediaId: String?,playWhenReady: Boolean,extras: Bundle?) {
            }
            override fun onPrepare(playWhenReady: Boolean) {
            }

            override fun getSupportedPrepareActions(): Long {
                return PlaybackStateCompat.ACTION_PLAY_FROM_URI
            }

            override fun onPrepareFromUri(uri: Uri?, playWhenReady: Boolean, extras: Bundle?) {

                val trackToPlay:Track?=extras?.getParcelable("track")
                if(trackToPlay!=null && !trackToPlay.path.equals(currentTrack?.path)) {
                    currentTrack = extras.getParcelable("track")
                    val dataSourceFactory: DefaultDataSourceFactory =
                        DefaultDataSourceFactory(this@AudioService, "Media Player")
                    val mediaSource: ProgressiveMediaSource =
                        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

                    player?.prepare(mediaSource)
                    player?.setPlayWhenReady(true)
                }
            }
        })
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
        player?.release()
        player=null
        super.onDestroy()
    }




}
