package com.alguojian.appupdate.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.alguojian.appupdate.CallBack.ClickCallback
import com.alguojian.appupdate.R


/**
 * 更新弹框
 *
 * @author alguojian
 * @date on 2017/10/13.
 */
class ConfirmDialog(
    var mContext: Context,
    var serverVersionName: String,
    var upDateTitle: String,
    var upDateContent: String,
    var enforceUpdate: Boolean,
    var clickCallback: ClickCallback
) : Dialog(mContext, R.style.CustomDialog) {
    private var content: TextView? = null
    private var downLoadProgressLayout: RelativeLayout? = null
    private var textProgress: TextView? = null
    private var progressBar: ProgressBar? = null
    private var sureBtn: TextView? = null
    private var cancelBtn: TextView? = null
    private var title: TextView? = null
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
        setWidth(320/360f,0f)
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

    fun setEnd() {
        isDownLoading = false
    }

    private fun setCustomDialog() {
        sureBtn = findViewById(R.id.dialog_confirm_sure)
        view = findViewById(R.id.view)
        title = findViewById(R.id.title)
        downLoadProgressLayout = findViewById(R.id.downLoadProgressLayout)
        textProgress = findViewById(R.id.textProgress)
        progressBar = findViewById(R.id.progressBar)
        cancelBtn = findViewById(R.id.dialog_confirm_cancle)
        content = findViewById(R.id.content)
        content!!.text = upDateContent
        title!!.text = upDateTitle
        sureBtn!!.setOnClickListener {
            if (!isNewWorkContact) {
                Toast.makeText(mContext, "网络连接失败，请检查后重试", Toast.LENGTH_LONG).show()
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


    private fun setWidth(a: Float, b: Float) {

        val dm = DisplayMetrics()
        val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        wm.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (a == 1f && b == 1f) {
            window.setGravity(Gravity.TOP)
        } else {
            window.setGravity(Gravity.CENTER)
        }
        val lp = window.attributes

        if (a != 0f)
            lp.width = (width * a).toInt()
        if (b != 0f) {
            if (a == 1f && b == 1f) {
                lp.height = height
            } else {
                lp.height = (width.toFloat() * a * b).toInt()
            }
        }
        window.setLayout(lp.width, lp.height)
    }
}
