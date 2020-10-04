package se.torsteneriksson.recepihandler

import android.content.ComponentName
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            storeRecepiInService()
        }

        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            mRecepiHandler = null
        }
    }

    fun storeRecepiInService() {
        var  recepiSteps = ArrayList<RecepiStep>()
        recepiSteps.add(RecepiStepWait("Låt jäsa", 30))
        recepiSteps.add(RecepiStepPrepare("Blanda degen"))
        var recepi: Recepi = Recepi("id1","bröd1",recepiSteps)
        mRecepiHandler?.addRecepi(recepi)
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

        val button: Button = view as Button
        button.setBackgroundColor(Color.BLUE)
        button.setTextColor(Color.WHITE)

        // Test of step
        var recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        when (step?.type) {
            STEPTYPE.TIMER -> {
                val step = step as RecepiStepWait
                button.setText("Now we have to wait for " + step.description + " in " + step.time + "s")
            }
            STEPTYPE.PREPARE -> {
                val step = step as RecepiStepPrepare
                button.setText("Now you should " + step.description)
            }
        }
        mRecepiHandler?.nextStep()
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