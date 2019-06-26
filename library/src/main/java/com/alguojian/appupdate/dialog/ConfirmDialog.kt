package com.alguojian.appupdate.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.alguojian.appupdate.CallBack.ClickCallback
import com.alguojian.appupdate.R


/**
 * 更新弹框
 *
 * @author ALguojain
 * @date on 2017/10/13.
 */
class ConfirmDialog(
    var mContext: Context,
    var serverVersionName: String,
    var string: String,
    var enforceUpdate: Boolean,
    var clickCallback: ClickCallback
) : Dialog(mContext, R.style.CustomDialog) {
    private var content: TextView? = null
    private var downLoadProgressLayout: RelativeLayout? = null
    private var textProgress: TextView? = null
    private var progressBar: ProgressBar? = null
    private var sureBtn: TextView? = null
    private var cancelBtn: TextView? = null
    private var versionName: TextView? = null
    private var view: View? = null
    var isDownLoading = false

    /**
     * 检测是否有网络
     */
    private val isNewWorkContact: Boolean
        get() {
            val cm = mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            return networkInfo != null
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_confirm)
        if (enforceUpdate) {
            setCancelable(false)
        }
        setCanceledOnTouchOutside(false)
        setCustomDialog()
        if (enforceUpdate) {
            cancelBtn!!.visibility = View.GONE
            view!!.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    fun setProgress(progress: Int) {
        if (progress > 0) {
            isDownLoading = true
            downLoadProgressLayout!!.visibility = View.VISIBLE
            textProgress!!.text = "$progress/100"
            progressBar!!.progress = progress
        }
    }

    fun setEnd(exception: Exception?) {
        isDownLoading = false
        if (null == exception) {
            dismiss()
        }
    }

    private fun setCustomDialog() {
        sureBtn = findViewById(R.id.dialog_confirm_sure)
        view = findViewById(R.id.view)
        versionName = findViewById(R.id.versionName)
        downLoadProgressLayout = findViewById(R.id.downLoadProgressLayout)
        textProgress = findViewById(R.id.textProgress)
        progressBar = findViewById(R.id.progressBar)
        cancelBtn = findViewById(R.id.dialog_confirm_cancle)
        content = findViewById(R.id.content)
        content!!.text = string
        versionName!!.text = "版本号：$serverVersionName"
        sureBtn!!.setOnClickListener {
            if (!isNewWorkContact) {
                Toast.makeText(mContext, "网络连接失败，请检查后再试", Toast.LENGTH_LONG).show()
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !mContext.packageManager.canRequestPackageInstalls()) {
                InstallUnknowSourceDialog(mContext).show()
            } else {
                if (!enforceUpdate) {
                    dismiss()
                } else if (isDownLoading) {
                    return@setOnClickListener
                }
                clickCallback.result(true)
            }
        }
        cancelBtn!!.setOnClickListener {
            dismiss()
            clickCallback.result(false)
        }
    }
}
