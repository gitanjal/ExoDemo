package com.droidmonk.exodemo.tracks

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.droidmonk.exodemo.R
import kotlinx.android.synthetic.main.activity_tracks.*
import kotlinx.android.synthetic.main.app_bar.*

class TracksActivity : AppCompatActivity() {

    private lateinit var actionBarToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracks)

        actionBarToggle =object: ActionBarDrawerToggle(this, drawer_layout, toolbar,
            R.string.drawer_open, R.string.drawer_close){

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)


            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
            actionBarToggle.syncState()
        }


        drawer_layout.addDrawerListener(actionBarToggle)
        drawer_layout.post(Runnable { actionBarToggle.syncState() })


        var type=TracksFragment.TYPE_AUDIO_LOCAL
        supportFragmentManager.beginTransaction().replace(R.id.fr_holder,TracksFragment.newInstance(type)).commit()

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
