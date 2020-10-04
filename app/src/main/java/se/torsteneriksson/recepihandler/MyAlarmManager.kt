package se.torsteneriksson.recepihandler


import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.PendingIntent
import android.content.Context
import android.content.Intent



class MyAlarmManager {
    private val REQUEST_CODE = 100
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    fun setAlarm(time: Long, context: Context, recepi_id: Int, state: Int) {

        // Creating the pending intent to send to the BroadcastReceiver
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyAlarmReceiver::class.java)
        intent.putExtra("recepi_id", recepi_id)
        intent.putExtra("state", state)
        pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent,0)
        // Starts the alarm manager
        val msec: Long = time * 1000
        println("About to set the alarm")
        alarmManager.set(
            RTC,
            msec,
            pendingIntent
        )

    }
}