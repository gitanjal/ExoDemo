package com.droidmonk.exodemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*

class MainActivity : AppCompatActivity() {

    private var player: SimpleExoPlayer? = null
    private var adsLoader: ImaAdsLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsLoader = ImaAdsLoader(this, Uri.parse(resources.getString(R.string.ad_tag)))
    }

    override fun onStart() {
        super.onStart()

        player=ExoPlayerFactory.newSimpleInstance(this,DefaultTrackSelector())
        player_view.player=player
        adsLoader?.setPlayer(player)


        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            "ExoDemo")

        val mediaSource:MediaSource=ExtractorMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(resources.getString(R.string.media_url_mp4)))

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
                    R.id.one_x ->{
                        player?.playbackParameters= PlaybackParameters(1f)
                        true
                    }
                    R.id.two_x ->{
                        player?.playbackParameters= PlaybackParameters(2f)
                        true
                    }
                    R.id.three_x ->{
                        player?.playbackParameters= PlaybackParameters(3f)
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


    override fun onStop() {

        adsLoader?.setPlayer(null)
        player_view.setPlayer(null)
        player?.release()
        player = null

        super.onStop()
    }
}
