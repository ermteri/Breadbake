package se.torsteneriksson.recepihandler.service

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.IBinder
import android.util.Log
import se.torsteneriksson.recepihandler.IRecepiHandlerService


class RecepiHandlerService() : Service() {
    var recepiState: Int = 0
    var recepiId: Int = 0

    override fun onBind(intent: Intent?): IBinder? {
        return object : IRecepiHandlerService.Stub() {
            override fun getState(): Int =
                recepiState

            override fun getId(): Int =
                recepiId


            override fun setState(state: Int) {
                recepiState = state
            }

            override fun setId(id: Int) {
                recepiId = id
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