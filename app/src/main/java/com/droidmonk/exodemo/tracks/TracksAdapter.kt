package com.droidmonk.exodemo.tracks

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.droidmonk.exodemo.VideoPlayerActivity
import com.droidmonk.exodemo.R
import kotlinx.android.synthetic.main.item_track.view.*


class TracksAdapter(val tracks:ArrayList<Track>) : RecyclerView.Adapter<TracksAdapter.TracksViewHolder>() {

    interface OnClickListener{
        fun onClick(track:Track)
        fun onClickAddToPlaylist(track:Track)
    }

    private lateinit var listener:OnClickListener

    public fun setOnClickListener(listener:OnClickListener)
    {
        this.listener=listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        var view=LayoutInflater.from(parent.context).inflate(R.layout.item_track,parent,false)
        return TracksViewHolder(view)
    }

    override fun getItemCount(): Int = tracks.size

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.itemView.track_title.text=tracks[position].trackTitle
        tracks[position].albumArt?.let {
            holder.itemView.img_album.setImageBitmap(tracks[position].albumArt)
        }?:run{
            holder.itemView.img_album.setImageDrawable(holder.itemView.context.resources.getDrawable(R.drawable.ic_music_note_black_24dp))
        }

        if(tracks[position].path.contains("http://") || tracks[position].path.contains("https://"))
            holder.itemView.download.visibility=View.VISIBLE
        else
            holder.itemView.download.visibility=View.GONE

        holder.itemView.setOnClickListener {
            listener.onClick(tracks[position])
        }

        holder.itemView.more.setOnClickListener {
            listener.onClickAddToPlaylist(tracks[position])
        }

    }

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}