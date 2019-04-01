package com.example.crashreport

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_empty.*
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException

class EmptyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)

        button.setOnClickListener {
            val intent = Intent(this, ScrollingActivity::class.java)
            startActivity(intent)
        }

        nested_exception.setOnClickListener {
            causeExceptionOne()
        }
    }

    private fun causeExceptionOne(): Int {
        var ioE: Exception? = null
        try {
            ioE = IOException("ioException in causeExceptionOne")
            throw ioE
        } catch (e: Exception) {
            var mathE: ArithmeticException? = null
            try {
                return 3 / 0
            } catch (e: ArithmeticException) {
                mathE = e
                mathE.initCause(ioE)
                Log.e("catch exception", e.message)
            } finally {
                val finallyE = RuntimeException("in causeException method finally")
                finallyE.initCause(mathE)
                throw finallyE
            }
        } finally {
            Log.e("in ", "outter causeException method finally")
        }

    }
}
