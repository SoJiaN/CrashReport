package com.example.crashreport

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Copyright © 2013-2018 Worktile. All Rights Reserved.
 * Author: SongJian
 * Email: songjian@worktile.com
 * Date: 2019/3/29
 * Time: 14:25
 * Desc:
 */
class CustomActivityLifeCycleCallback : Application.ActivityLifecycleCallbacks {
    val uploadList = ArrayList<String>()

    private val paused = "onPaused"
    private val resumed = "onResumed"
    private val started = "onStarted"
    private val destroyed = "onDestroyed"
    private val stopped = "onStopped"
    private val created = "onCreated"

    override fun onActivityPaused(activity: Activity?) {
        uploadList.add("${activity?.toString()} $paused")
    }

    override fun onActivityResumed(activity: Activity?) {
        uploadList.add("${activity?.toString()} $resumed")
    }

    override fun onActivityStarted(activity: Activity?) {
        uploadList.add("${activity?.toString()} $started")
    }

    override fun onActivityDestroyed(activity: Activity?) {
        uploadList.add("${activity?.toString()} $destroyed")
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
        uploadList.add("${activity?.toString()} $stopped")
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        uploadList.add("${activity?.toString()} $created")
    }

}