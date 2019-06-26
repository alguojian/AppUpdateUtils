package com.alguojian.appupdate

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.alguojian.appupdate.CallBack.UpdateProgressCallBack
import com.alguojian.appupdate.dialog.ConfirmDialog
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import java.io.File

/**
 * @author ALguojian
 * @date on 2017/12/13.
 */
object DownloadAppUtils {

    var filePath: String? = null

    fun download(
        context: Context,
        url: String,
        name: String,
        updateProgressCallBack: UpdateProgressCallBack?,
        enforceUpdateProgressDialog: ConfirmDialog?
    ) {
        filePath = context.externalCacheDir.absolutePath + File.separator + name + ".apk"
        Downloader.downloadImage(context, url, "$name.apk", object : DownloadListener1() {
            override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
                Toast.makeText(context, "正在下载，请稍后...", Toast.LENGTH_LONG).show()
            }

            override fun taskEnd(
                task: DownloadTask,
                cause: EndCause,
                realCause: Exception?,
                model: Listener1Assist.Listener1Model
            ) {
                if (null == realCause) {
                    send(context, 100, name)
                } else {
                    Toast.makeText(context, "网络出错了，请稍后再试", Toast.LENGTH_LONG).show()
                }
                enforceUpdateProgressDialog?.setEnd(realCause)
            }

            override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
                enforceUpdateProgressDialog?.setProgress((currentOffset * 100L / totalLength).toInt())
                updateProgressCallBack?.downloadProgress((currentOffset / totalLength * 100L).toInt())
                send(context, (currentOffset * 100L / totalLength).toInt(), name)
            }

            override fun connected(task: DownloadTask, blockCount: Int, currentOffset: Long, totalLength: Long) {

            }

            override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
            }
        })
    }

    private fun send(context: Context, progress: Int, name: String) {
        val intent = Intent("com.alguojian.appupdate")
        intent.putExtra("progress", progress)
        intent.putExtra("title", name)
        context.sendBroadcast(intent)
    }
}
