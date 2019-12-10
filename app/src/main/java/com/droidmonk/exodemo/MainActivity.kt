package com.droidmonk.exodemo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*

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

        val mediaSource:MediaSource=DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(resources.getString(R.string.media_url_dash)))

        val adsMediaSource = AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, player_view)


        player?.prepare(adsMediaSource)
        player?.setPlayWhenReady(true)
    }


    override fun onStop() {


        player_view.setPlayer(null)
        player?.release()
        player = null

        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()

        adsLoader?.setPlayer(null)
    }
}
