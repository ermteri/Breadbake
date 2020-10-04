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
    var mRecepi = Recepi("","",ArrayList())
    private val mAlarmManager = MyAlarmManager()
    val COUNTDOWN_BR = "se.torsteneriksson.recepihandler.countdown_br"
    var mBroadcastIntent = Intent(COUNTDOWN_BR)


    override fun onBind(intent: Intent?): IBinder? {
        return object : IRecepiHandlerService.Stub() {
            override fun getRecepi(): Recepi {
                return mRecepi
            }

            override fun addRecepi(recepi: Recepi?) {
                mRecepi = recepi!!
            }

            override fun nextStep() {
                mRecepi.nextStep()
                if (mRecepi.getCurrentStep().type == STEPTYPE.TIMER) {
                    val step = mRecepi.getCurrentStep() as RecepiStepWait
                    startTimer(step.time.toLong())
                }
            }
            override fun prevStep() {
                mRecepi.prevStep()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
    }
    // ************************* Private methods **************************
    fun startTimer(sec: Long) {

        val timer = object : CountDownTimer( sec * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mBroadcastIntent.putExtra("Message", "Remaining: " + millisUntilFinished/1000);
                sendBroadcast(mBroadcastIntent)
            }

            override fun onFinish() {
                mBroadcastIntent.putExtra("Message", "Finished");
                sendBroadcast(mBroadcastIntent)
            }
        }
        timer.start()
    }
}