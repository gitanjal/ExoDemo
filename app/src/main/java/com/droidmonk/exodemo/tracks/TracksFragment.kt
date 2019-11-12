package com.droidmonk.exodemo.tracks


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidmonk.exodemo.R
import kotlinx.android.synthetic.main.fragment_tracks.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TracksFragment : Fragment() {
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS: Int = 1
    companion object{
        val TYPE_AUDIO_LOCAL=1
        val TYPE_VIDEO_LOCAL=2
        val TYPE_VIDEO_WEB=3

        val KEY_TYPE="key"

        @JvmStatic
        fun newInstance(type:Int) =
            TracksFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_TYPE, type)
                }
            }
    }

    var trackType:Int=TYPE_AUDIO_LOCAL   //default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trackType= arguments?.getInt(KEY_TYPE)?: TYPE_AUDIO_LOCAL
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracks, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        checkPermission()




    }

    private fun setUpTrackList() {

        var adapter=TracksAdapter(getTrackList())
        list.layoutManager= LinearLayoutManager(activity)
        list.adapter=adapter
    }

    private fun getTrackList(): ArrayList<Track>
                = when(trackType)
                {
                    TYPE_AUDIO_LOCAL->getLocalAudio()
                    TYPE_VIDEO_LOCAL->getLocalVideo()
                    TYPE_VIDEO_WEB->getWebVideo()
                    else->ArrayList<Track>()
                }



    private fun getWebVideo():ArrayList<Track> {

        var trackMP4=Track(1,"Simple MP4","Artist 1", resources.getString(R.string.media_url_mp4),null)
        var trackDASH=Track(1,"Simple DASH","Artist 1", resources.getString(R.string.media_url_dash),"mpd")

        return arrayListOf(trackMP4,trackDASH)
    }

    private fun getLocalVideo():ArrayList<Track> {
        var trackList:ArrayList<Track> = ArrayList()

        val musicResolver = activity?.contentResolver
        val musicUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI

        val musicCursor = musicResolver?.query(musicUri, null, null, null, null)

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val dataColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisPath=musicCursor.getString(dataColumn)
                trackList.add(Track(thisId, thisTitle, thisArtist,thisPath,null))
            } while (musicCursor.moveToNext())
        }

        return trackList
    }

    private fun getLocalAudio():ArrayList<Track> {
        var trackList:ArrayList<Track> = ArrayList()

        val musicResolver = activity?.contentResolver
        val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val musicCursor = musicResolver?.query(musicUri, null, null, null, null)

        if (musicCursor != null && musicCursor.moveToFirst()) {
            //get columns
            val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
            val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
            val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
            val dataColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
            //add songs to list
            do {
                val thisId = musicCursor.getLong(idColumn)
                val thisTitle = musicCursor.getString(titleColumn)
                val thisArtist = musicCursor.getString(artistColumn)
                val thisPath=musicCursor.getString(dataColumn)
                trackList.add(Track(thisId, thisTitle, thisArtist,thisPath,null))
            } while (musicCursor.moveToNext())
        }

        return trackList
    }

    fun checkPermission()
    {
        if (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS)

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            setUpTrackList()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    setUpTrackList()

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
