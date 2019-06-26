package com.alguojian.appupdate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.content.FileProvider
import java.io.File

/**
 * @author ALguojain
 * @date on 2017/11/3.
 */

class UpdateAppReceiver(context: Context) : BroadcastReceiver() {

    val intentFilter: IntentFilter
        get() {
            val intentFilter = IntentFilter()
            intentFilter.addAction("com.alguojian.appupdate")
            return intentFilter
        }

    init {
        setNotification(context)
    }

    override fun onReceive(context: Context, intent: Intent) {

        val notifyId = 1
        val progress = intent.getIntExtra("progress", 0)
        val title = intent.getStringExtra("title")

        if (AppUpdate.showNotification) {
            val builder = NotificationCompat.Builder(context, "appupdate")

            builder.setContentTitle("正在下载：$title")
            builder.setSmallIcon(AppUpdate.notificationIcon)
            //只提醒一次
            builder.setOnlyAlertOnce(true)
            builder.setProgress(ONE_HUNDRED, progress, false)
            nm!!.notify(notifyId, builder.build())
        }

        if (ONE_HUNDRED == progress) {
            if (nm != null) {
                nm!!.cancel(notifyId)
            }
            startInstall(context)
        }
    }

    private fun setNotification(context: Context) {
        nm = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "应用升级"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("appupdate", channelName, importance)
            channel.canBypassDnd()
            channel.enableLights(true)
            if (nm != null) {
                nm!!.createNotificationChannel(channel)
            }
        }
    }

    private fun startInstall(context: Context) {
        if (null != DownloadAppUtils.filePath) {
            val i = Intent(Intent.ACTION_VIEW)
            val apkFile = File(DownloadAppUtils.filePath)
            if (!apkFile.exists()) {
                return
            }
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                i.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                val contentUri = FileProvider.getUriForFile(
                        context, context.packageName + ".fileprovider", apkFile)
                i.setDataAndType(contentUri, "application/vnd.android.package-archive")
            } else {
                i.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            context.startActivity(i)
        }
    }

    companion object {
        private const val ONE_HUNDRED = 100
        private var nm: NotificationManager? = null
    }
}
