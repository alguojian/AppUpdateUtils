package com.alguojian.appupdate

import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.alguojian.appupdate.CallBack.ClickCallback
import com.alguojian.appupdate.CallBack.UpdateProgressCallBack


import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var updateAppReceiver: UpdateAppReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        updateAppReceiver = UpdateAppReceiver(this)
        registerReceiver(updateAppReceiver, updateAppReceiver.intentFilter)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
            ), 200
        )

        findViewById<View>(R.id.button).setOnClickListener {
            update(false)
        }
        findViewById<View>(R.id.button3).setOnClickListener {
            update(true)
        }

        findViewById<View>(R.id.button2).setOnClickListener {
            val path = this@MainActivity.externalCacheDir!!.absolutePath + File.separator + "约杯咖啡_1.4.0" + ".apk"
            val file = File(path)
            if (file.exists()) {
                val delete = file.delete()
                Toast.makeText(this@MainActivity, "删除结果：$delete", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun update(boolean: Boolean) {
        AppUpdate.with(this@MainActivity)
            //设置下载地址，不支持中文
            .setUpdatePath("https://ossqiyu.oss-cn-hangzhou.aliyuncs.com/Versions/yuebeikafei137.apk")
            //设置通知栏通知的图标
            .setNotificationIconAndName(R.mipmap.ic_launcher, "约杯咖啡")
            //设置版本名字
            .setVersionName("1.4.0")
            //设置版本更新内容
            .setUpdateInfo(getString(R.string.app_update_message))
            //设置是否强制更新
            .setEnforceUpdate(boolean)
            //设置更新弹框回调
            .setOnUpdateClick(ClickCallback {

            })
            .setDownProgressListener(UpdateProgressCallBack {

            })
            .startUpdate()
    }

    override fun onDestroy() {
        unregisterReceiver(updateAppReceiver)
        super.onDestroy()
    }
}
