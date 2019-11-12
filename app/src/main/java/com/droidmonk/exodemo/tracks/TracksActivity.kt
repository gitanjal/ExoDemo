package com.droidmonk.exodemo.tracks

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.droidmonk.exodemo.R
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_tracks.*;

class TracksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracks)

        var type=TracksFragment.TYPE_AUDIO_LOCAL
        supportFragmentManager.beginTransaction().replace(R.id.fr_holder,TracksFragment.newInstance(type))

        navigationView.setNavigationItemSelectedListener { item ->
            when(item.itemId)
            {
                R.id.menu_local_audio->{
                    type=TracksFragment.TYPE_AUDIO_LOCAL
                    true
                }
                R.id.menu_local_video->{
                    type=TracksFragment.TYPE_VIDEO_LOCAL
                    true
                }
                R.id.menu_web_audio->{
                    type=TracksFragment.TYPE_AUDIO_LOCAL
                    true
                }
                R.id.menu_web_video->{
                    type=TracksFragment.TYPE_VIDEO_WEB
                    true
                }
                else->{
                    type=TracksFragment.TYPE_AUDIO_LOCAL
                    true
                }
            }

            supportFragmentManager.beginTransaction().replace(R.id.fr_holder,TracksFragment.newInstance(type)).commit()
            true
        }

    }
}
