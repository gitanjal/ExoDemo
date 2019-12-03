package com.droidmonk.exodemo

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adsLoader: ImaAdsLoader
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        adsLoader = ImaAdsLoader(this, Uri.parse(resources.getString(R.string.ad_tag)))
    }

    override fun onStart() {
        super.onStart()

        player=ExoPlayerFactory.newSimpleInstance(this,DefaultTrackSelector())
        player_view.player=player
        adsLoader.setPlayer(player)


        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            "ExoDemo")

        val mediaSource:MediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(resources.getString(R.string.video_url)))
        val adsMediaSource = AdsMediaSource(mediaSource, dataSourceFactory, adsLoader, player_view)



        player?.prepare(adsMediaSource)
        player?.setPlayWhenReady(true)
    }
}
