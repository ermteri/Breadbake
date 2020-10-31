package se.torsteneriksson.recepihandler

import android.app.Activity
import android.content.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

import se.torsteneriksson.recepihandler.service.RecepiHandlerService
import kotlin.math.roundToInt

class RecepiHandlerMainActivity : AppCompatActivity() {
    var mRecepiHandler: IRecepiHandlerService? = null

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val textView: TextView = findViewById<TextView>(R.id.id_timer)
            textView.setText(intent?.getStringExtra("Message"))
            updateGui()
        }
    }

    val mConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service

            Toast.makeText(this@RecepiHandlerMainActivity, "Connected", Toast.LENGTH_SHORT).show()
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
        val isRunning: Boolean = isMyServiceRunning(this, RecepiHandlerService::class.java.getName())
        if (!isRunning) {
            Intent(this, RecepiHandlerService::class.java).also { intent ->
                startService(intent)
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Check which request we're responding to
        if (requestCode == PICK_RECEPI_REQUEST) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                // The user picked a recepi.
                val recepi: String = data?.action as String
                storeRecepiInService(recepi)
                updateGui()
            }
        }
    }
    // ****************** GUI interface **************************
    fun prev(view: View) {
        val textView: TextView = findViewById<TextView>(R.id.id_stepinstruction)

        // Test of step
        mRecepiHandler?.prevStep()
        var recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        updateGui()
    }
    fun next(view: View) {
        val textView: TextView = findViewById<TextView>(R.id.id_stepinstruction)

        // Test of step
        mRecepiHandler?.nextStep()
        var recepi = mRecepiHandler?.getRecepi()
        val step = recepi?.getCurrentStep()
        updateGui()
    }

    fun showRecepiList(view: View) {
        val intent = Intent(this, RecepiSelectorActivity::class.java)
        val bundle: Bundle = Bundle()
        bundle.putStringArray("myData", arrayOf("kalle", "pelle", "olle"))
        startActivityForResult(intent, PICK_RECEPI_REQUEST, bundle)
        //startActivity(intent, bundle)
    }

    fun fetch(selected: String) {
        storeRecepiInService(selected)
        updateGui()
    }

    fun start(view: View) {
        val recepi = mRecepiHandler?.getRecepi()
        recepi?.nextStep()
        updateGui()
    }
    fun clear(view: View) {
        val alert = showAlertDialog(this, getString(R.string.delete), getString(R.string.delete_recepi))
        alert.show()
        val ok: Button = alert.getButton(AlertDialog.BUTTON_POSITIVE)
        ok.setOnClickListener { this.deleteRecepi(alert) }
    }

    fun deleteRecepi(alert: android.app.AlertDialog) {
        alert.dismiss()
        mRecepiHandler?.addRecepi(null)
        updateGui()
    }


    // **************************** Private functions *********************************


    // Function to establish connections with the service, binding by interface names.
    private fun bindToService() {

        val i = Intent()
        i.setClassName(this.packageName, RecepiHandlerService::class.java.getName())
        val bindResult = bindService(i, mConnection, BIND_AUTO_CREATE)
        if (bindResult) {
            Toast.makeText(this@RecepiHandlerMainActivity, "Bounded!", Toast.LENGTH_SHORT).show()
        }
    }

    // Upload a recepi to the service
    fun storeRecepiInService(recepi: String) {
        if (mRecepiHandler?.getRecepi()?.uid == "") {
            Toast.makeText(this@RecepiHandlerMainActivity, getString(R.string.no_active_recepi), Toast.LENGTH_SHORT).show()
        }
        val recepi: Recepi = getRecepi(recepi) as Recepi
        mRecepiHandler?.addRecepi(recepi)
    }

    // Update various fields
    fun updateGui() {
        val title = findViewById<TextView>(R.id.id_recepi_title)
        val overallDescription = findViewById<TextView>(R.id.id_overall_description)
        val stepinstruction = findViewById<TextView>(R.id.id_stepinstruction)
        val progressbar = findViewById<ProgressBar>(R.id.id_step_progress)
        val timer = findViewById<TextView>(R.id.id_timer)
        var recepi = mRecepiHandler?.getRecepi()
        if (recepi != null) {
            title.setText(recepi?.name)
            overallDescription.setText(recepi?.ingridients)
            stepinstruction.setText(recepi?.getCurrentStep()?.description)
            if (recepi?.getCurrentStep() is RecepiStepPrepare)
                timer.setText(getString(R.string.timer))
            val p = recepi.progress()
            progressbar.setProgress(recepi.progress().roundToInt())
        } else {
            title.setText(getString(R.string.recepiTitle))
            overallDescription.setText(getString(R.string.ingridients))
            stepinstruction.setText(getString(R.string.instruction))
            progressbar.setProgress(0)
            timer.setText(getString(R.string.timer))
        }
    }
}