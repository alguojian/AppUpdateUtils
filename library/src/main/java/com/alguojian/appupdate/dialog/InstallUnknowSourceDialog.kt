package com.alguojian.appupdate.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.TextView
import com.alguojian.appupdate.R

/**
 * 允许安装未知来源
 *
 * @author alguojian
 * @data 2019/06/20
 */
class InstallUnknowSourceDialog(context: Context) : Dialog(context, R.style.CustomDialog) {
    init {
        setContentView(R.layout.update_dialog_install_unknow_source)
        setCanceledOnTouchOutside(false)
        findViewById<TextView>(R.id.cancel).setOnClickListener { dismiss() }
        findViewById<TextView>(R.id.confirm).setOnClickListener {
            val uri = Uri.fromParts("package", context.packageName, null)
            context.startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri))
            dismiss()
        }
    }
}
