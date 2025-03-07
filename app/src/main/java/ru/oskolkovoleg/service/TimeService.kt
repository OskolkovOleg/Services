package ru.oskolkovoleg.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeService: Service() {
    private val localBinder = MyLocalBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return localBinder
    }

    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.UK)
        return dateFormat.format(Date())
    }

    inner class MyLocalBinder: Binder() {
        fun getService(): TimeService {
            return this@TimeService
        }
        fun getConst() {

        }
    }
}