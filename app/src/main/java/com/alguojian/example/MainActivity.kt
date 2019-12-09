package com.alguojian.example


import android.Manifest
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.alguojian.appupdate.AppUpdate
import com.alguojian.appupdate.CallBack.ClickCallback
import com.alguojian.appupdate.CallBack.UpdateProgressCallBack
import com.alguojian.appupdate.UpdateAppReceiver
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
            val files = File(this@MainActivity.externalCacheDir!!.absolutePath)
            val listFiles = files.listFiles()
            for (ff in listFiles) {
                if (ff.path.endsWith(".apk")) {
                    val delete = ff.delete()
                    Toast.makeText(this@MainActivity, "删除结果：$delete", Toast.LENGTH_SHORT).show()
                }
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

            //设置是否强制更新，默认不强制
            .setEnforceUpdate(boolean)

            //设置是否支持断点续传下载，默认不支持
            .setBreakpointDown(false)

            //设置更新弹框回调
            .setOnUpdateClick(ClickCallback {
                Toast.makeText(this,"哈哈哈哈哈",Toast.LENGTH_LONG).show()
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
