package com.droidmonk.exodemo

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


class AppContainer(val context: Context) {

    private var dataSourceFactory: DefaultHttpDataSourceFactory =
        DefaultHttpDataSourceFactory("ExoDemo")
    private var databaseProvider: DatabaseProvider
    private var downloadContentDirectory: File
    var downloadCache: Cache
    var downloadManager: DownloadManager

    init {
        databaseProvider = ExoDatabaseProvider(context)
        downloadContentDirectory = File(context.getExternalFilesDir(null), "downloads")
        downloadCache = SimpleCache(downloadContentDirectory, NoOpCacheEvictor(), databaseProvider)
        downloadManager = DownloadManager(
            context,
            databaseProvider,
            downloadCache,
            dataSourceFactory
        )
    }


}