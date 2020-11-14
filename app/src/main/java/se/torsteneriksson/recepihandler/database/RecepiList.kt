package se.torsteneriksson.recepihandler.database

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL

class RecepiList(context: Context) : ViewModel() {
    val RECEPIES_FILE = "recepies.json"
    val RECEPIES_URL = "https://torsteneriksson.se/public/recepies.json"
    val mRecepiFile: File
    val mContext: Context
    lateinit var mRecepies: ArrayList<Recepi>

    init {
        mContext = context
        this.mRecepiFile = File(mContext.filesDir, RECEPIES_FILE)
        if (isRecepieListLoaded())
            mRecepies = Json.decodeFromString(mRecepiFile.readText())
    }

    fun isRecepieListLoaded(): Boolean {
        return mRecepiFile.exists()
    }

    fun getRecepies(): ArrayList<Recepi>? {
        if (isRecepieListLoaded())
            return mRecepies
        else
            return null
    }

    fun getRecepi(name: String): Recepi? {
        for (recepi in mRecepies)
            if (name == recepi.name)
                return recepi
        return null
    }

    suspend fun refresh() =
        // Dispatchers.Main
        withContext(Dispatchers.IO) {
            mRecepiFile.delete()
            val json_str = URL(RECEPIES_URL).readText()
            mRecepiFile.writeText(json_str)
            mRecepies = Json.decodeFromString(mRecepiFile.readText())
        }
}


/*
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) setContentView(R.layout.activity_main)
        title = "KotlinApp"
        DownloadImageFromInternet(findViewById(R.id.imageView)).execute("https://images.unsplash.com/photo-1535332371349-a5d229f49cb5?ixlib=rb-1.2.1&w=1000&q=80")
    }
    @SuppressLint("StaticFieldLeak") @Suppress("DEPRECATION")
    private inner class DownloadImageFromInternet(var imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
        init {
            Toast.makeText(applicationContext, "Please wait, it may take a few minute...",
                Toast.LENGTH_SHORT).show()
        }
        override fun doInBackground(vararg urls: String): Bitmap? {
            val imageURL = urls[0]
            var image: Bitmap? = null try {
                val `in` = java.net.URL(imageURL).openStream() image = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error Message", e.message.toString())
                e.printStackTrace()
            }
            return image
        }
        override fun onPostExecute(result: Bitmap?) {
            imageView.setImageBitmap(result)
        }
    }
}*/