package se.torsteneriksson.breadbake

import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context

class MyAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("Alarm recieved")
        val recepi = intent.getIntExtra("recepi_id", 0)
        val state = intent.getIntExtra("state", 0)
        Toast.makeText(context, "Recepi:" + recepi + ", State: " + state, Toast.LENGTH_LONG).show()
    }
}