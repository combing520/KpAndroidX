package cn.cc1w.app.ui.manager

import android.content.Context
import android.os.Looper
import cn.cc1w.app.ui.ui.splash.SplashActivity
import cn.cc1w.app.ui.utils.IntentUtil
import cn.cc1w.app.ui.utils.ToastUtil
import kotlin.system.exitProcess

class CrashCollectHandler : Thread.UncaughtExceptionHandler {
    var mContext: Context? = null
    var mDefaultHandler: Thread.UncaughtExceptionHandler? = null

    companion object {
        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { CrashCollectHandler() }
    }

    fun init(pContext: Context) {
        this.mContext = pContext
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        if (!handleException(e) && mDefaultHandler != null) {
            mDefaultHandler?.uncaughtException(t, e)
        } else {
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            AppManager.finishAllActivity()
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(0)
        }
    }

    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        Thread {
            Looper.prepare()
            ToastUtil.showShortToast("很抱歉,程序出现异常,即将退出")
            mContext?.apply {
                IntentUtil.startActivity(mContext,SplashActivity::class.java)
            }
            Looper.loop()
        }.start()
        return true
    }
}