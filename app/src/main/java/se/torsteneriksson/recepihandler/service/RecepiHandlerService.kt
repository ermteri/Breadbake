package se.torsteneriksson.recepihandler.service

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import se.torsteneriksson.recepihandler.IRecepiHandlerService
import se.torsteneriksson.recepihandler.Recepi


class RecepiHandlerService() : Service() {
    var mRecepi = Recepi("","",ArrayList())

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
}