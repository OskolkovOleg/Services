package ru.oskolkovoleg.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import android.Manifest
import ru.oskolkovoleg.service.ui.theme.ServiceTheme

class MainActivity : ComponentActivity() {
    lateinit var timeService: TimeService
    var isBound = false
    val locationState = mutableStateOf("location")


    //private val myConnection = object : ServiceConnection {
    //    override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
    //        val binder = service as TimeService.MyLocalBinder
    //        timeService = binder.getService()
    //        isBound = true
    //    }

    //    override fun onServiceDisconnected(p0: ComponentName?) {
    //        isBound = false
    //    }
    //}

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            locationState.value = "${intent!!.getDoubleExtra("lat", 0.0)}" +
                    " / ${intent!!.getDoubleExtra("long", 0.0)}"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        launcher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION))

        val intent = Intent(this, LocationService::class.java)
        startService(intent)

        val timeState = mutableStateOf("0 / 0")

        GlobalScope.async {
            while (true) {
                Thread.sleep(1000)
                if (isBound) {
                    timeState.value = timeService.getCurrentTime()
                }
            }
        }

        setContent {
            ServiceTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        time = timeState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(receiver, IntentFilter(LocationService.INTENT_SERVICE), RECEIVER_EXPORTED)
    }

    @Composable
    fun Greeting(time: MutableState<String>, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = locationState.value,
                modifier = Modifier
            )

            Text(
                text = time.value,
                modifier = Modifier
            )

            Button(onClick = {
                //val intent = Intent(this@MainActivity, TimeService::class.java)
                //bindService(intent, myConnection, Context.BIND_AUTO_CREATE)
            }) {
                Text(text = "Bind Service")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            //unbindService(myConnection)
        }
        unregisterReceiver(receiver)
    }

    val launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback {
            val intent = Intent(this, LocationService::class.java)
            startService(intent)
        })

}