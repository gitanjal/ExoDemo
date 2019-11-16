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


        holder.itemView.setOnClickListener {
        holder.itemView.context.startActivity(Intent(holder.itemView.context,VideoPlayerActivity::class.java).putExtra("track",tracks[position]))
        }
    }

    class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}