package com.droidmonk.exodemo

import android.app.Notification
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.Util

class MediaDownloadService() : DownloadService(FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    CHANNEL_ID,
    R.string.exo_download_notification_channel_name,
    0) {

    companion object {
        private const val CHANNEL_ID = "download_channel"
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1
        private var nextNotificationId =
            FOREGROUND_NOTIFICATION_ID + 1
    }

    lateinit var notificationHelper: DownloadNotificationHelper

    override fun onCreate() {
        super.onCreate()

        notificationHelper= DownloadNotificationHelper(this, CHANNEL_ID)
    }

    override fun getDownloadManager(): DownloadManager {
        return (application as App).appContainer.downloadManager
    }

    override fun getForegroundNotification(downloads: MutableList<Download>?): Notification {
        return notificationHelper.buildProgressNotification(R.drawable.ic_file_download_black_24dp,null,"Downloading media..",downloads)
    }

    override fun getScheduler(): Scheduler? {
        return null
    }


}
