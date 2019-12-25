package com.droidmonk.exodemo.tracks


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.droidmonk.exodemo.R
import com.droidmonk.exodemo.VideoPlayerActivity
import kotlinx.android.synthetic.main.fragment_tracks.*
import java.lang.ref.WeakReference

class TracksFragment : Fragment() {

    private val REQUEST_READ_EXTERNAL_STORAGE: Int = 1

    companion object{
        val TYPE_AUDIO_LOCAL=1
        val TYPE_VIDEO_LOCAL=2
        val TYPE_VIDEO_WEB=3
        val TYPE_AUDIO_WEB=4

        val KEY_TYPE="key"

        @JvmStatic
        fun newInstance(type:Int) =
            TracksFragment().apply {
                arguments = Bundle().apply {
                    putInt(KEY_TYPE, type)
                }
            }
    }

    val getTracksAsyncCallback= object : GetTracksAsyncTask.GetTracksCallback{
        override fun setUpTrackList(tracks: ArrayList<Track>) {
            val adapter=TracksAdapter(tracks)
            adapter.setOnClickListener(object : TracksAdapter.OnClickListener{
                override fun onClickAddToPlaylist(track: Track) {

                }

                override fun onClick(track: Track) {

                    activity?.startActivity(Intent(activity, VideoPlayerActivity::class.java).putExtra("track",track))
                }
            })
            list.layoutManager= LinearLayoutManager(activity)
            list.adapter=adapter
        }
    }

    var trackType:Int=TYPE_AUDIO_LOCAL   //default
    lateinit var getTrackAsyncTask:GetTracksAsyncTask

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        trackType= arguments?.getInt(KEY_TYPE)?: TYPE_AUDIO_LOCAL
        getTrackAsyncTask=GetTracksAsyncTask(activity!!,trackType,getTracksAsyncCallback)

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

    private fun checkPermission()
    {
        if (ContextCompat.checkSelfPermission(activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(activity!!,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL_STORAGE)
            }
        } else {
            getTrackAsyncTask.execute()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_EXTERNAL_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    getTrackAsyncTask.execute()

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }


    class GetTracksAsyncTask(context: Activity, private val trackType:Int, callback:GetTracksCallback):AsyncTask<Void,Void,ArrayList<Track>>(){

        interface GetTracksCallback{
           fun setUpTrackList(tracks:ArrayList<Track>)
        }

        private val context: WeakReference<Activity> = WeakReference(context)
        private val callback:WeakReference<GetTracksCallback> = WeakReference(callback)

        override fun doInBackground(vararg params: Void?): ArrayList<Track> {
            return getTrackList()
        }

        override fun onPostExecute(result: ArrayList<Track>?) {
            super.onPostExecute(result)

            result?.let { callback.get()?.setUpTrackList(it) }
        }

        private fun getTrackList(): ArrayList<Track>
                = when(trackType)
        {
            TYPE_AUDIO_LOCAL->getLocalAudio()
            TYPE_VIDEO_LOCAL->getLocalVideo()
            TYPE_VIDEO_WEB->getWebVideo()
            TYPE_AUDIO_WEB->getWebAudio()
            else->ArrayList<Track>()
        }

        private fun getWebVideo():ArrayList<Track> {

            val trackMP4=Track(1,"Simple MP4","Artist 1", context.get()?.resources!!.getString(R.string.media_url_mp4),null,null)
            val trackDASH=Track(1,"Simple DASH","Artist 1", context.get()?.resources!!.getString(R.string.media_url_dash),"mpd",null)

            return arrayListOf(trackMP4,trackDASH)
        }

        private fun getWebAudio():ArrayList<Track> {

            val trackMP4=Track(1,"Guitar solo","Unknown Artist", context.get()?.resources!!.getString(R.string.media_url_mp4),null,null)
            val trackDASH=Track(1,"Guitar fingerstyle","Unknown Artist", context.get()?.resources!!.getString(R.string.media_url_dash),null,null)
            val trackMP41=Track(1,"C progression","Unknown Artist", context.get()?.resources!!.getString(R.string.media_url_mp4),null,null)
            val trackDASH2=Track(1,"Do re mi","Unknown Artist", context.get()?.resources!!.getString(R.string.media_url_dash),null,null)

            return arrayListOf(trackMP4,trackDASH,trackMP41,trackDASH2)
        }

        private fun getLocalVideo():ArrayList<Track> {
            val trackList:ArrayList<Track> = ArrayList()

            val videoResolver = context.get()?.contentResolver
            val videoUri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI

            val videoCursor = videoResolver?.query(videoUri, null, null, null, null)

            if (videoCursor != null && videoCursor.moveToFirst()) {
                //get columns
                val titleColumn =
                    videoCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
                val idColumn = videoCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
                val artistColumn =
                    videoCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
                val dataColumn =
                    videoCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
                //add songs to list
                do {
                    val thisId = videoCursor.getLong(idColumn)
                    val thisTitle = videoCursor.getString(titleColumn)
                    val thisArtist = videoCursor.getString(artistColumn)
                    val thisPath = videoCursor.getString(dataColumn)
                    trackList.add(Track(thisId, thisTitle, thisArtist, thisPath, null, null))
                } while (videoCursor.moveToNext())
            }

            return trackList
        }

        private fun getLocalAudio():ArrayList<Track> {
            val trackList:ArrayList<Track> = ArrayList()

            val musicResolver = context.get()?.contentResolver
            val musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

            val musicCursor = musicResolver?.query(musicUri, null, null, null, null)

            if (musicCursor != null && musicCursor.moveToFirst()) {
                //get columns
                val titleColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE)
                val idColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID)
                val artistColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ARTIST)
                val dataColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.DATA)
                val albumColumn = musicCursor.getColumnIndex(android.provider.MediaStore.Audio.Media.ALBUM)
                //add songs to list
                do {
                    val thisId = musicCursor.getLong(idColumn)
                    val thisTitle = musicCursor.getString(titleColumn)
                    val thisArtist = musicCursor.getString(artistColumn)
                    val thisPath=musicCursor.getString(dataColumn)

                    var mediaDataRetriever= MediaMetadataRetriever()

                    mediaDataRetriever.setDataSource(context.get(),Uri.parse(thisPath))

                    var songImage:Bitmap?=null
                    mediaDataRetriever.embeddedPicture?.let {

                        val albumArt:ByteArray=mediaDataRetriever.embeddedPicture
                        songImage = BitmapFactory.decodeByteArray(albumArt, 0, albumArt.size);
                    }
                    trackList.add(Track(thisId, thisTitle, thisArtist,thisPath,null,null))
                } while (musicCursor.moveToNext())
            }

            return trackList
        }
    }




}
