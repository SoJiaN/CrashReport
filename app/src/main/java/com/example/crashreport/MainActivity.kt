package com.example.crashreport

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.Snackbar
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main_test.*
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            throw RuntimeException("Click Exception==========")
        }

        handler.setOnClickListener {
            Handler(Looper.getMainLooper()).post {
                throw RuntimeException("Handler Exception ==========")
            }
        }


        thread.setOnClickListener {
            val t = Thread(Runnable {
                throw RuntimeException("Thread Exception==========")
            })
            t.start()
        }

        togo.setOnClickListener {
            val intent = Intent(this, EmptyActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        CrashHandler.verifyStoragePermissions(this)

        Observable.just(1)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                for (i in 2000 downTo 0) {
                    print(i)
                }
            }
            .subscribe()

        Observable.just(1)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                for (i in 2000 downTo 0) {
                    print(i)
                }
            }
            .subscribe()

        Observable.just(1)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                for (i in 2000 downTo 0) {
                    print(i)
                }
            }
            .subscribe()

        Observable.just(1)
            .subscribeOn(Schedulers.io())
            .doOnNext {
                for (i in 2000 downTo 0) {
                    print(i)
                }
            }
            .subscribe()

    }
}
