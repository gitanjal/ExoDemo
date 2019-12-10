package com.droidmonk.exodemo

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val TAG:String="MainActivity"

    private var currentPosition: Long = 0
    private var player: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        player=ExoPlayerFactory.newSimpleInstance(this,DefaultTrackSelector())
        player_view.player=player


        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            "ExoDemo")

        val mediaSource:MediaSource=ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(resources.getString(R.string.video_url)))

        player?.prepare(mediaSource)
        player?.seekTo(currentPosition)
        player?.setPlayWhenReady(true)

    }

    override fun onStop() {
        super.onStop()

        Log.d(TAG,"Onstop")

        currentPosition = player!!.contentPosition
        player_view.setPlayer(null)
        player!!.release()
        player = null

        super.onStop()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG,"Onpause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"Onresume")
    }
}
