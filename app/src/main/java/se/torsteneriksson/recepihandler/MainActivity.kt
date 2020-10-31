package se.torsteneriksson.recepihandler


import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.torsteneriksson.recepihandler.service.RecepiHandlerService
import java.security.KeyStore


class MainActivity : AppCompatActivity() {
    var bottomNavigationView: BottomNavigationView? = null
    var mRecepiHandler: IRecepiHandlerService? = null
    var mCurrentRecepi: Recepi? = null

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val textView: TextView = findViewById<TextView>(R.id.id_timer)
            textView.setText(intent?.getStringExtra("Message"))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createBottomNavigation()
        val isRunning: Boolean = isMyServiceRunning(this, RecepiHandlerService::class.java.getName())
        if (!isRunning) {
            Intent(this, RecepiHandlerService::class.java).also { intent ->
                startService(intent)
            }
        }
        bindToService()
        registerReceiver(mBroadcastReceiver, IntentFilter("se.torsteneriksson.recepihandler.countdown_br"));
    }


    // Private methods
    val mConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service

            Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
            mRecepiHandler = IRecepiHandlerService.Stub.asInterface(service)
            if (mRecepiHandler?.recepi == null)
                bottomNavigationView?.menu?.findItem(R.id.action_selected)?.isEnabled = false

        }
        // Called when the connection with the service disconnects unexpectedly
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(ContentValues.TAG, "Service has unexpectedly disconnected")
            mRecepiHandler = null
        }
    }


    private val navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener =
        object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.getItemId()) {
                    R.id.action_home ->     {
                        startHomeFragment()
                    }
                    R.id.action_selected -> {
                        startCurrentRecepiFragment()
                    }
                    R.id.action_search -> {
                        startSearchFragment()
                    }
                }
                return true
            }
        }

    // Public methods for fragments
    fun setCurrentRecepi(recepiName: String) {
        mRecepiHandler?.addRecepi(getRecepi(recepiName))
        startCurrentRecepiFragment()
        bottomNavigationView?.setSelectedItemId(R.id.action_selected)
        bottomNavigationView?.menu?.findItem(R.id.action_selected)?.isEnabled = true
    }

    fun getRecepiHanlderService(): IRecepiHandlerService? {
        return mRecepiHandler
    }

    // Private functions
    fun startHomeFragment() {
        val selectorFragment = RecepiSelectorFragment.newInstance("","")
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_top_frame, selectorFragment).commit()
        val bottomFragment = supportFragmentManager.findFragmentById(R.id.main_bottom_frame)
        if (bottomFragment != null)
            supportFragmentManager.beginTransaction()
                .remove(bottomFragment).commit()
    }

    fun startCurrentRecepiFragment() {
        val recepiName = mRecepiHandler?.recepi?.name
        val recepiDescrition = mRecepiHandler?.recepi?.ingridients
        val image = mRecepiHandler?.recepi?.image
        val descriptionFragment = RecepiDescriptionFragment.newInstance(
            recepiName as String,
            recepiDescrition as String, image as Int)
        val stepsFragment = RecepiStepsFragment.newInstance("","")
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_top_frame, descriptionFragment).commit()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_bottom_frame, stepsFragment).commit()
    }

    fun startSearchFragment() {
        val selectorFragment = RecepiSelectorFragment.newInstance("","")
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_top_frame, selectorFragment).commit()
        val bottomFragment = supportFragmentManager.findFragmentById(R.id.main_bottom_frame)
        if (bottomFragment != null)
            supportFragmentManager.beginTransaction()
                .remove(bottomFragment).commit()
    }
    private fun bindToService() {
        val i = Intent()
        i.setClassName(this.packageName, RecepiHandlerService::class.java.getName())
        val bindResult = bindService(i, mConnection, BIND_AUTO_CREATE)
        if (bindResult) {
            Toast.makeText(this@MainActivity, "Bounded!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottom_navigation) as BottomNavigationView
        val menu: Menu = bottomNavigationView!!.menu
        menu.clear()
        bottomNavigationView?.inflateMenu(R.menu.bottom_navigation_menu)
        bottomNavigationView?.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
    }
}