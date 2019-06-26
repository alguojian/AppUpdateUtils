package com.alguojian.appupdate

import android.content.Context
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
import com.liulishuo.okdownload.core.listener.DownloadListener1
import java.util.concurrent.TimeUnit

/**
 * 下载类
 */
object Downloader {
    fun downloadImage(context: Context, url: String, fileName: String, listener: DownloadListener1): DownloadTask {
        val task = DownloadTask.Builder(url, context.externalCacheDir.absolutePath, fileName)
                .setMinIntervalMillisCallbackProcess(30)
                .build()
        task.enqueue(listener)
        return task
    }
}
