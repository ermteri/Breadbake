package se.torsteneriksson.recepihandler.service

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import se.torsteneriksson.recepihandler.*


class RecepiHandlerService() : Service() {
    //var mRecepi = Recepi("","",ArrayList())
    var mRecepi: Recepi? = null
    private val mAlarmManager = MyAlarmManager()
    val COUNTDOWN_BR = "se.torsteneriksson.recepihandler.countdown_br"
    var broadcastIntent = Intent(COUNTDOWN_BR)
    var mTimer: CountDownTimer? = null


    override fun onBind(intent: Intent?): IBinder? {
        return object : IRecepiHandlerService.Stub() {
            override fun getRecepi(): Recepi? {
                return mRecepi
            }

            override fun addRecepi(recepi: Recepi?) {
                mRecepi = recepi
            }

            override fun nextStep() {
                mRecepi?.nextStep()
                if (mRecepi?.getCurrentStep()?.type == STEPTYPE.TIMER) {
                    val step = mRecepi?.getCurrentStep() as RecepiStepWait
                    startTimer(step.time)
                }
            }
            override fun prevStep() {
                mRecepi?.prevStep()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate()")
        createNotificationChannel(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mTimer?.cancel()
        Log.d(TAG, "onDestroy()")
    }
    // ************************* Private methods **************************
    fun startTimer(sec: Long) {

        mTimer = object : CountDownTimer( sec * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                broadcastIntent.putExtra("Message", getString(R.string.remaining) +
                        getHumanFriendlyTime(millisUntilFinished/1000))
                sendBroadcast(broadcastIntent)
            }

            override fun onFinish() {
                mRecepi?.nextStep()
                broadcastIntent.putExtra("Message", getString(R.string.finished));
                sendBroadcast(broadcastIntent)
                notify(applicationContext, getString(R.string.alarmTitle), getString(R.string.alarmMessage))
            }
        }
        mTimer?.start()
    }
}