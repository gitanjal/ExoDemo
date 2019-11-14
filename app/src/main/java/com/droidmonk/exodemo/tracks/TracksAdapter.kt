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



        holder.itemView.setOnClickListener {
            listener.onClick(tracks[position])
        }

        holder.itemView.more.setOnClickListener {
            listener.onClickAddToPlaylist(tracks[position])
        }

    }

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}