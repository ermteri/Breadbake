package se.torsteneriksson.breadbake

import android.app.ActivityManager
import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import se.torsteneriksson.breadbake.service.RecepiHandler

class BreadBakeMainActivity : AppCompatActivity() {
    private val alarmManager = MyAlarmManager()
    var iRemoteService: IRecepiHandler? = null

    val mConnection = object : ServiceConnection {

        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service

            Toast.makeText(this@BreadBakeMainActivity,"Connected", Toast.LENGTH_LONG).show()
            iRemoteService = IRecepiHandler.Stub.asInterface(service)
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            iRemoteService = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breadbakemain)

        Intent(this, RecepiHandler::class.java).also { intent ->
            startService(intent)

        initService()
        }
    }

    fun startTimer(view: View) {
        //alarmManager.setAlarm(10, this, 4711, 42)
        // Id
        println("B: RecepiId:" + iRemoteService?.getId())
        iRemoteService?.setId(88)
        println("A: RecepiId:" + iRemoteService?.getId())
        // State
        println("B: RecepiState:" + iRemoteService?.getState())
        iRemoteService?.setState(12)
        println("A: RecepiIdState:" + iRemoteService?.getState())
    }


    /** Function to establish connections with the service, binding by interface names.  */
    private fun initService() {

        val i = Intent()
        i.setClassName(this.packageName, RecepiHandler::class.java.getName())
        val bindResult = bindService(i, mConnection, BIND_AUTO_CREATE)
        if (bindResult) {
            Toast.makeText(this@BreadBakeMainActivity,"Bounded!", Toast.LENGTH_LONG).show()
        }
    }
}