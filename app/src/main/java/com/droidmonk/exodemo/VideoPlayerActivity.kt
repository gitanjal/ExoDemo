package com.droidmonk.exodemo

import android.net.Uri
import android.os.Bundle
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.droidmonk.exodemo.tracks.Track
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*

class VideoPlayerActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private var adsLoader: ImaAdsLoader? = null
    private lateinit var trackToPlay:Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        adsLoader = ImaAdsLoader(this, Uri.parse(resources.getString(R.string.ad_tag)))

        trackToPlay=intent.getParcelableExtra("track")
    }

    override fun onStart() {
        super.onStart()

        player = ExoPlayerFactory.newSimpleInstance(this, DefaultTrackSelector())
        player_view.player = player
        adsLoader?.setPlayer(player)


        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            "ExoDemo"
        )

        val mediaSource: MediaSource = getMediaSource(trackToPlay,dataSourceFactory)

        val adsMediaSource = AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, player_view)


        player?.prepare(adsMediaSource)
        player?.setPlayWhenReady(true)


        btn_speed.setOnClickListener {
            //  Toast.makeText(it.context,"Click speed controll",Toast.LENGTH_LONG).show()

            val popup = PopupMenu(this, it)
            val inflater: MenuInflater = popup.menuInflater
            inflater.inflate(R.menu.playback_speed, popup.menu)
            popup.show()

            popup.setOnMenuItemClickListener {

                when (it.itemId) {
                    R.id.one_x -> {
                        player?.playbackParameters = PlaybackParameters(1f)
                        true
                    }
                    R.id.two_x -> {
                        player?.playbackParameters = PlaybackParameters(2f)
                        true
                    }
                    R.id.three_x -> {
                        player?.playbackParameters = PlaybackParameters(3f)
                        true
                    }
                    else -> {
                        Toast.makeText(this, "Invalid option ", Toast.LENGTH_LONG).show()
                        true
                    }
                }
            }

        }
    }

    private fun getMediaSource(
        trackToPlay: Track,
        dataSourceFactory: DefaultDataSourceFactory
    ): MediaSource {
        val uri:Uri=Uri.parse(trackToPlay.path)
        val type=Util.inferContentType(uri,trackToPlay.extension)
        when(type)
        {
            C.TYPE_DASH
                -> return DashMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            C.TYPE_OTHER
                -> return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else ->
                throw IllegalStateException("Unsupported type: $type")
        }
       // return ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
    }


    override fun onStop() {

        adsLoader?.setPlayer(null)
        player_view.setPlayer(null)
        player?.release()
        player = null

        super.onStop()
    }
}
