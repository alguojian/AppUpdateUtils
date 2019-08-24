package com.alguojian.appupdate

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import com.alguojian.appupdate.CallBack.ClickCallback
import com.alguojian.appupdate.CallBack.UpdateProgressCallBack
import com.alguojian.appupdate.dialog.ConfirmDialog

import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
import java.util.concurrent.TimeUnit


/**
 * @author ALguojian
 * @date on 2017/11/15.
 */
@SuppressLint("StaticFieldLeak")
object AppUpdate {

    private lateinit var activity: Activity


    /**
     * 通知栏
     */
    var showNotification = true

    /**
     * 完整的下载地址，地址中不能包含中文
     */
    private var apkPath = ""

    /**
     * 版本名字
     */
    private var serverVersionName = ""
    private var notificationName = ""

    /**
     * 下载时默认的通知栏图标
     */
    var notificationIcon = R.mipmap.ic_launcher

    /**
     * 点击更新时的取消以及确定回调
     */
    private var clickCallback: ClickCallback? = null

    /**
     * 下载进度的回调
     */
    private var updateProgressCallBack: UpdateProgressCallBack? = null

    /**
     * 是否强制更新
     */
    private var enforceUpdate = false


    /**
     * 是否使用断点续传下载
     */
    private var breakpointDown = false

    /**
     * 默认更新详情
     */
    private var updateInfo = "1、修复了一些已知bug"

    private var confirmDialog: ConfirmDialog? = null

    /**
     * 设置强制更新，默认为否
     */
    fun setEnforceUpdate(flag: Boolean): AppUpdate {
        enforceUpdate = flag
        return this
    }


    /**
     * 设置是否使用断点续传下载,默认关闭
     */
    fun setBreakpointDown(flag: Boolean): AppUpdate {
        breakpointDown = flag
        return this
    }

    /**
     * 初始化
     */
    fun initThis(context: Context) {
        val builder = OkDownload.Builder(context)
            .connectionFactory { url ->
                val factory = DownloadOkHttp3Connection.Factory()
                val builder1 = factory.builder()
                builder1.readTimeout(100, TimeUnit.SECONDS)
                factory.setBuilder(builder1)
                factory.create(url)
            }
        OkDownload.setSingletonInstance(builder.build())
    }

    /**
     * 初始化
     */
    fun with(activity: Activity): AppUpdate {
        AppUpdate.activity = activity
        return this
    }

    /**
     * 设置更新时的下载地址
     */
    fun setUpdatePath(apkPath: String): AppUpdate {
        AppUpdate.apkPath = apkPath
        return this
    }

    /**
     * 设置更新时点击取消或者确定的回调
     */
    fun setOnUpdateClick(clickCallback1: ClickCallback): AppUpdate {
        clickCallback = clickCallback1
        return this
    }

    /**
     * 下载进度的回调
     */
    fun setDownProgressListener(updateProgressCallBack: UpdateProgressCallBack): AppUpdate {
        AppUpdate.updateProgressCallBack = updateProgressCallBack
        return this
    }

    /**
     * 设置更新时的下载内容
     */
    fun setUpdateInfo(updateInfo: String): AppUpdate {
        AppUpdate.updateInfo = updateInfo
        return this
    }

    /**
     * 设置更新时的版本号
     */
    fun setVersionName(serverVersionName: String): AppUpdate {
        AppUpdate.serverVersionName = serverVersionName
        return this
    }

    /**
     * 设置通知栏更新时的图标，默认使用android机器人图标
     */
    fun setNotificationIconAndName(icon: Int, name: String): AppUpdate {
        notificationIcon = icon
        notificationName = name
        return this
    }

    /**
     * 开始更新
     */
    fun startUpdate() {
        if (DownloadAppUtils.isDownLoading) {
            Toast.makeText(activity, "新版本正在下载中...", Toast.LENGTH_LONG).show()
            return
        }

        var content = ""
        if (!TextUtils.isEmpty(updateInfo)) {
            content = updateInfo
        }
        confirmDialog = ConfirmDialog(
            activity, serverVersionName, content, enforceUpdate,
            ClickCallback { result ->
                if (null != clickCallback) {
                    clickCallback!!.result(result)
                }
                if (result) {
                    DownloadAppUtils.download(
                        activity,
                        apkPath,
                        "${notificationName}_${if (!breakpointDown) serverVersionName + "_" + System.currentTimeMillis().toString() else serverVersionName}",
                        updateProgressCallBack,
                        if (enforceUpdate) confirmDialog else null
                    )
                }
            })
        confirmDialog!!.show()
    }
}
