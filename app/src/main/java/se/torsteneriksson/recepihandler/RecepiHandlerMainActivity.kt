package se.torsteneriksson.recepihandler

import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import se.torsteneriksson.recepihandler.service.RecepiHandlerService

class RecepiHandlerMainActivity : AppCompatActivity() {
    private val alarmManager = MyAlarmManager()
    var mRecepiHandler: IRecepiHandlerService? = null
    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val textView: TextView = findViewById<TextView>(R.id.timer)
            textView.setText(intent?.getStringExtra("Message"))
            updateGui()
        }
    }

    val mConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service

            Toast.makeText(this@RecepiHandlerMainActivity, "Connected", Toast.LENGTH_LONG).show()
            mRecepiHandler = IRecepiHandlerService.Stub.asInterface(service)
            updateGui()
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
        registerReceiver(mBroadcastReceiver, IntentFilter("se.torsteneriksson.recepihandler.countdown_br"));
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver);
        //Intent(this, RecepiHandlerService::class.java).also { intent ->
        //    stopService(intent)
        //}
    }
    // ****************** GUI interface **************************
    fun prev(view: View) {
        val textView: TextView = findViewById<TextView>(R.id.instruction)

        // Test of step
        mRecepiHandler?.prevStep()
        var recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        textView.setText(step?.description)
    }
    fun next(view: View) {
        val textView: TextView = findViewById<TextView>(R.id.instruction)

        // Test of step
        mRecepiHandler?.nextStep()
        var recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        textView.setText(step?.description)
    }

    fun fetch(view: View) {
        val textView: TextView = findViewById<TextView>(R.id.instruction)
        storeRecepiInService()
        val recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        textView.setText(step?.description)
    }

    fun clear(view: View) {
        mRecepiHandler?.addRecepi(null)
        var instructionView: TextView = findViewById<TextView>(R.id.instruction)
        instructionView.setText("instruction")
        val timerView: TextView = findViewById<TextView>(R.id.timer)
        timerView.setText("timer")

    }



    // **************************** Private functions *********************************
    // Function to establish connections with the service, binding by interface names.
    private fun bindToService() {

        val i = Intent()
        i.setClassName(this.packageName, RecepiHandlerService::class.java.getName())
        val bindResult = bindService(i, mConnection, BIND_AUTO_CREATE)
        if (bindResult) {
            Toast.makeText(this@RecepiHandlerMainActivity, "Bounded!", Toast.LENGTH_LONG).show()
        }
    }

    // Upload a recepi to the service
    fun storeRecepiInService() {
        if (mRecepiHandler?.getRecepi()?.uid == "") {
            Toast.makeText(this@RecepiHandlerMainActivity, "No active recepi!", Toast.LENGTH_LONG).show()
        }
        var  recepiSteps = ArrayList<RecepiStep>()
        recepiSteps.add(RecepiStepPrepare("Blanda degen"))
        recepiSteps.add(RecepiStepWait("Låt jäsa i 10", 10))
        recepiSteps.add(RecepiStepPrepare("Dags att vika 1:a gången"))
        recepiSteps.add(RecepiStepWait("Låt jäsa i 20", 10))
        recepiSteps.add(RecepiStepPrepare("Dags att vika 2:a gången"))
        recepiSteps.add(RecepiStepWait("Låt jäsa i 20", 10))
        recepiSteps.add(RecepiStepPrepare("Dags att vika, 3:e och sista gången"))
        recepiSteps.add(RecepiStepWait("Låt jäsa i 20", 20))
        recepiSteps.add(RecepiStepPrepare("Färdigt!!"))

        var recepi: Recepi = Recepi("id1", "bröd1", recepiSteps)
        mRecepiHandler?.addRecepi(recepi)
    }

    // Update various fields
    fun updateGui() {
        val instruction = findViewById<TextView>(R.id.instruction)
        var recepi = mRecepiHandler?.getRecepi()
        if (recepi != null)
            instruction.setText(recepi.getCurrentStep().description)
    }
}