package se.torsteneriksson.recepihandler.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import se.torsteneriksson.recepihandler.*
import se.torsteneriksson.recepihandler.database.Recepi
import se.torsteneriksson.recepihandler.database.RecepiStepWait
import se.torsteneriksson.recepihandler.database.STEPTYPE


class RecepiHandlerService : Service() {
    //var mRecepi = Recepi("","",ArrayList())
    var mRecepi: Recepi? = null
    private val COUNTDOWN_BR = "se.torsteneriksson.recepihandler.countdown_br"
    var mBroadcastIntent = Intent(COUNTDOWN_BR)
    var mTimer: CountDownTimer? = null
    val ONGOING_NOTIFICATION_ID = 1

    companion object;

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags.or(START_STICKY), startId)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMyActivityNotification(title: String, message: String, alarm: Boolean): Notification {
        val pendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        if (alarm) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            return NotificationCompat.Builder(this, getString(R.string.channeld_id))
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_baseline_fastfood_24)
                .setContentIntent(pendingIntent)
                .setSound(soundUri)
                .setOnlyAlertOnce(false)
                .build()
        } else {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            return NotificationCompat.Builder(this, getString(R.string.channeld_id))
                .setContentTitle(title)
                .setContentText(message)
                .setOnlyAlertOnce(true)
                .setSound(soundUri)
                .setSmallIcon(R.drawable.ic_baseline_fastfood_24)
                .setContentIntent(pendingIntent)
                .build()
        }
    }

    /**
     * This is the method that can be called to update the Notification
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateNotification(title: String, message: String, alarm: Boolean) {
        val notification = getMyActivityNotification(title, message, alarm)
        if (alarm)
            notification.flags = Notification.FLAG_INSISTENT or Notification.FLAG_AUTO_CANCEL
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(ONGOING_NOTIFICATION_ID, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return object : IRecepiHandlerService.Stub() {
            override fun getRecepi(): Recepi? {
                return mRecepi
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun addRecepi(recepi: Recepi?) {
                mRecepi = recepi
                stopForeground(ONGOING_NOTIFICATION_ID)
                mTimer?.cancel()
                if (recepi != null) {
                    startForeground(
                        ONGOING_NOTIFICATION_ID,
                        getMyActivityNotification(recepi.name as String, "", false)
                    )
                    // Reset the step counter
                    mRecepi?.mCurrentStep = -1
                }
            }

            override fun nextStep() {
                mRecepi?.nextStep()
                mTimer?.cancel()
                if (mRecepi?.getCurrentStep()?.steptype == STEPTYPE.TIMER) {
                    mTimer?.cancel()
                    val step = mRecepi?.getCurrentStep() as RecepiStepWait
                    startTimer(mRecepi?.name.toString(), step.time)
                }
            }

            override fun prevStep() {
                mTimer?.cancel()
                mRecepi?.prevStep()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "SVC onCreate()")
        createNotificationChannel(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancel()
        Log.d(TAG, "SVC onDestroy()")
    }
    // ************************* Private methods **************************
    fun startTimer(title: String, sec: Long) {

        mTimer = object : CountDownTimer(sec * 1000, 1000) {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onTick(millisUntilFinished: Long) {
                val time: String = getHumanFriendlyTime(millisUntilFinished / 1000)
                val extra: String? = mRecepi?.getCurrentStep()?.description
                mBroadcastIntent.putExtra("Message", time)
                sendBroadcast(mBroadcastIntent)
                updateNotification(title, "$extra:$time", false, )
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                val message: String = mRecepi?.getCurrentStep()?.description + ": "+  getString(R.string.finished)
                mBroadcastIntent.putExtra("Message", message)
                sendBroadcast(mBroadcastIntent)
                //notify(applicationContext, getString(R.string.alarmTitle), getString(R.string.alarmMessage))
                updateNotification(title, message,true)
                mRecepi?.nextStep()
            }
        }
        mTimer?.start()
    }
}