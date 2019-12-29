package com.droidmonk.exodemo.tracks

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class Track (val trackId:Long,
                  val trackTitle:String,
                  val trackArtist:String?,
                  val path:String,
                  val extension:String?,
                  val albumArt:Bitmap?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(trackId)
        parcel.writeString(trackTitle)
        parcel.writeString(trackArtist)
        parcel.writeString(path)
        parcel.writeString(extension)
        parcel.writeParcelable(albumArt, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Track> {
        override fun createFromParcel(parcel: Parcel): Track {
            return Track(parcel)
        }

        override fun newArray(size: Int): Array<Track?> {
            return arrayOfNulls(size)
        }
    }

}