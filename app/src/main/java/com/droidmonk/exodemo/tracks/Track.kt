package com.droidmonk.exodemo.tracks

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Track(
    val trackTitle: String,
    val trackArtist: String?,
    val path: String,
    val extension: String?,
    val albumArt: Bitmap?,
    val iconUri: Uri?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackTitle)
        parcel.writeString(trackArtist)
        parcel.writeString(path)
        parcel.writeString(extension)
        parcel.writeParcelable(albumArt, flags)
        parcel.writeParcelable(iconUri, flags)
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