package se.torsteneriksson.recepihandler

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import se.torsteneriksson.recepihandler.R
import se.torsteneriksson.recepihandler.service.RecepiHandlerService

class RecepiHandlerMainActivity : AppCompatActivity() {
    private val alarmManager = MyAlarmManager()
    var mRecepiHandler: IRecepiHandlerService? = null

    val mConnection = object : ServiceConnection {

        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service

            Toast.makeText(this@RecepiHandlerMainActivity,"Connected", Toast.LENGTH_LONG).show()
            mRecepiHandler = IRecepiHandlerService.Stub.asInterface(service)
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            mRecepiHandler = null
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recepihandlermain)
        Intent(this, RecepiHandlerService::class.java).also { intent ->
            startService(intent)
        }
        bindToService()
    }

    fun startTimer(view: View) {
        //alarmManager.setAlarm(10, this, 4711, 42)
        // Id
        println("B: RecepiId:" + mRecepiHandler?.getId())
        mRecepiHandler?.setId(88)
        println("A: RecepiId:" + mRecepiHandler?.getId())
        // State
        println("B: RecepiState:" + mRecepiHandler?.getState())
        mRecepiHandler?.setState(12)
        println("A: RecepiIdState:" + mRecepiHandler?.getState())
    }


    /** Function to establish connections with the service, binding by interface names.  */
    private fun bindToService() {

        val i = Intent()
        i.setClassName(this.packageName, RecepiHandlerService::class.java.getName())
        val bindResult = bindService(i, mConnection, BIND_AUTO_CREATE)
        if (bindResult) {
            Toast.makeText(this@RecepiHandlerMainActivity,"Bounded!", Toast.LENGTH_LONG).show()
        }
    }
}