package com.example.crashreport

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.os.Environment
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.widget.Toast

import java.io.*
import java.lang.Thread.UncaughtExceptionHandler
import java.text.SimpleDateFormat
import java.util.*

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 *
 * @author user
 */
class CrashHandler
/** 保证只有一个CrashHandler实例  */
private constructor() : UncaughtExceptionHandler {

    //系统默认的UncaughtException处理类
    private var mDefaultHandler: Thread.UncaughtExceptionHandler? = null
    //程序的Context对象
    private var mContext: Context? = null
    //用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()

    //用于格式化日期,作为日志文件名的一部分
    private val formatter = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")

    private val callback = CustomActivityLifeCycleCallback()

    private val lineSeparator: String? = System.getProperty("line.separator")

    /**
     * 初始化
     *
     * @param context
     */
    fun init(context: Context) {
        mContext = context
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    fun registerCallback(myApplication: MyApplication) {
        myApplication.registerActivityLifecycleCallbacks(callback)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(thread, ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler!!.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                Log.e(TAG, "error : ", e)
            }

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(1)
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(thread: Thread, ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        //使用Toast来显示异常信息
        object : Thread() {
            override fun run() {
                Looper.prepare()
                Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }.start()
        //收集设备参数信息
        collectDeviceInfo(mContext)
        //保存日志文件
        saveCrashInfo2File(thread, ex)
        return false
    }

    /**
     * 收集设备参数信息
     * @param ctx
     */
    private fun collectDeviceInfo(ctx: Context?) {
        try {
            val pm = ctx!!.packageManager
            val pi = pm.getPackageInfo(ctx.packageName, PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null) "null" else pi.versionName
                val versionCode = pi.versionCode.toString() + ""
                infos["versionName"] = versionName
                infos["versionCode"] = versionCode
            }
        } catch (e: NameNotFoundException) {
            Log.e(TAG, "an error occured when collect package info", e)
        }

        val fields = Build::class.java.declaredFields
        for (field in fields) {
            try {
                field.isAccessible = true
                infos[field.name] = field.get(null).toString()
                Log.d(TAG, field.name + " : " + field.get(null))
            } catch (e: Exception) {
                Log.e(TAG, "an error occured when collect crash info", e)
            }

        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param initialThrowable
     * @return  返回文件名称,便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(thread: Thread, initialThrowable: Throwable): String? {
        val sb = StringBuffer()
        for ((key, value) in infos) {
            sb.append("$key=$value$lineSeparator")
        }

        sb.append(lineSeparator)

        sb.append("${thread.name}$lineSeparator")
        var keyStackTraceElement = ""
        findRootCause(initialThrowable)?.stackTrace?.get(0)?.toString()?.let {
            keyStackTraceElement = it
            sb.append("the key stackTraceElement is$lineSeparator $keyStackTraceElement$lineSeparator")
        }

        sb.append(lineSeparator)

        sb.append("just like bugly !!!!!$lineSeparator")

        sb.append(lineSeparator)

        sb.append("$initialThrowable $lineSeparator")
        sb.append("$keyStackTraceElement $lineSeparator")
        sb.append("......$lineSeparator")
        sb.append("Cause by")

        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        findRootCause(initialThrowable)?.printStackTrace(printWriter)

        sb.append("$lineSeparator$writer$lineSeparator")
        printWriter.write("${lineSeparator}this printWriter is closed !!!!!!$lineSeparator")
        printWriter.close()



        sb.append("*************************************************************************")
        sb.append(lineSeparator)
        sb.append(lineSeparator)
        findRootCause(initialThrowable)?.stackTrace?.forEach {traceElement->
            this.javaClass.`package`?.name?.let {
                if (traceElement.className.startsWith(it)) sb.append(traceElement.toString() + lineSeparator)
            }
        }
        sb.append(lineSeparator)
        sb.append("*************************************************************************")


        // 其他线程信息
        val hashMap = Thread.getAllStackTraces()
        hashMap.keys.forEach { thread ->
            val stack = hashMap[thread]
            sb.append("$lineSeparator${thread.name}$lineSeparator")
            stack?.forEachIndexed { _, stackTraceElement ->
                sb.append("$stackTraceElement$lineSeparator")
            }
        }

        callback.uploadList.forEachIndexed { index, s ->
            if (index == 0) sb.append(lineSeparator)
            sb.append("$s$lineSeparator")
        }

        try {
            val timestamp = System.currentTimeMillis()
            val time = formatter.format(Date())
            val fileName = "crash-$time-$timestamp.log"
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path = Environment.getExternalStorageDirectory().absolutePath + "/CustomCrashReport/crash/"
                val dir = File(path)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val fos = FileOutputStream(path + fileName)
                fos.write(sb.toString().toByteArray())
                fos.close()
            }
            return fileName
        } catch (e: Exception) {
            Log.e(TAG, "an error occured while writing file...", e)
        }

        return null
    }

    /**
     * 得到最开始的 cause
     */
    private var rootCause: Throwable? = null

    private fun findRootCause(e: Throwable?): Throwable? {
        return if (e?.cause != null) {
            rootCause = e.cause
            findRootCause(rootCause)
        } else {
            rootCause
        }
    }

    companion object {

        const val TAG = "CrashHandler"
        //CrashHandler实例
        /** 获取CrashHandler实例 ,单例模式  */
        @SuppressLint("StaticFieldLeak")
        val instance = CrashHandler()

        // Storage Permissions
        private const val REQUEST_EXTERNAL_STORAGE = 1
        private val PERMISSIONS_STORAGE =
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        /**
         * Checks if the app has permission to write to device storage
         *
         * If the app does not has permission then the user will be prompted to grant permissions
         *
         * @param activity
         */
        fun verifyStoragePermissions(activity: Activity) {
            // Check if we have write permission
            val permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }
}
