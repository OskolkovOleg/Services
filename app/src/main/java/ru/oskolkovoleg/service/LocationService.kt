package ru.oskolkovoleg.service

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext



class LocationService: Service(), LocationListener {
        lateinit var locationManager : LocationManager

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        GlobalScope.async {
            while (true) {
                delay(5000)
                locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

                if (ActivityCompat.checkSelfPermission(
                        this@LocationService,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this@LocationService,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {

                }
                withContext(Dispatchers.Main) {
                    locationManager.requestLocationUpdates(
                        LocationManager.FUSED_PROVIDER,
                        5000,
                        5f,
                        this@LocationService
                    )
                }
            }
        }
        return START_STICKY
    }

    override fun onLocationChanged(location: Location) {
        val intent = Intent(INTENT_SERVICE)
        intent.putExtra("lat", location.latitude)
        intent.putExtra("long", location.longitude)
        sendBroadcast(intent)
    }

    companion object {
        val INTENT_SERVICE = "myCat"
    }
}