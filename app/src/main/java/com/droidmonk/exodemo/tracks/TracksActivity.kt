package com.droidmonk.exodemo.tracks

import android.os.Bundle
import android.view.View
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.droidmonk.exodemo.R
import kotlinx.android.synthetic.main.activity_tracks.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.app_bar.view.*


class TracksActivity : AppCompatActivity() {

    private lateinit var actionBarToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracks)

        val tbar:androidx.appcompat.widget.Toolbar=findViewById(R.id.app_bar)

        setSupportActionBar(tbar)
        actionBarToggle = ActionBarDrawerToggle(this, drawer_layout,tbar, R.string.drawer_open, R.string.drawer_close)
        drawer_layout.addDrawerListener(actionBarToggle)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)


        actionBarToggle.syncState()


        var type=TracksFragment.TYPE_AUDIO_LOCAL
        supportFragmentManager.beginTransaction().replace(R.id.fr_holder,TracksFragment.newInstance(type)).commit()

        navigationView.setNavigationItemSelectedListener { item ->
            when(item.itemId)
            {
                R.id.menu_local_audio->{
                    type=TracksFragment.TYPE_AUDIO_LOCAL
                }
                R.id.menu_local_video->{
                    type=TracksFragment.TYPE_VIDEO_LOCAL
                }
                R.id.menu_web_audio->{
                    type=TracksFragment.TYPE_AUDIO_WEB
                }
                R.id.menu_web_video->{
                    type=TracksFragment.TYPE_VIDEO_WEB
                }
                else->{
                    type=TracksFragment.TYPE_AUDIO_LOCAL
                }
            }

            supportFragmentManager.beginTransaction().replace(R.id.fr_holder,TracksFragment.newInstance(type)).commit()
            true
        }

    }
    public override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarToggle.syncState()
    }

}
