package com.example.crashreport

import android.app.Application
import android.content.Context

/**
 * Copyright © 2013-2018 Worktile. All Rights Reserved.
 * Author: SongJian
 * Email: songjian@worktile.com
 * Date: 2019/3/26
 * Time: 23:11
 * Desc:
 */
class MyApplication : Application() {

    val defaultCaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        CrashHandler.instance.init(baseContext)
        CrashHandler.instance.registerCallback(this)
    }
}