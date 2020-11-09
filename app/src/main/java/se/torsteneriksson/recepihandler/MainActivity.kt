package se.torsteneriksson.recepihandler


import android.app.Activity
import android.content.ComponentName
import android.content.ContentValues
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.torsteneriksson.recepihandler.database.RecepiList
import se.torsteneriksson.recepihandler.service.RecepiHandlerService

private const val TOP_FRAGEMENT = "top_fragment"
private const val BOTTOM_FRAGEMENT = "bottom_fragment"

interface IMainActivity {
    fun setCurrentRecepi(recepiName: String)
    fun getRecepiHandlerService(): IRecepiHandlerService?
}

class MainActivity : AppCompatActivity(), IMainActivity {
    var bottomNavigationView: BottomNavigationView? = null
    var mRecepiHandler: IRecepiHandlerService? = null
    var mActivity = Activity()
    lateinit var mRecepiList: RecepiList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createBottomNavigation()
        val isRunning: Boolean = isMyServiceRunning(
            this,
            RecepiHandlerService::class.java.getName()
        )
        if (!isRunning) {
            Intent(this, RecepiHandlerService::class.java).also { intent ->
                startService(intent)
            }
        }
        mRecepiList = RecepiList(this)
        bindToService()
        mRecepiList.loadRecepies(false)
        mActivity = this
    }


    // Private methods
    val mConnection = object : ServiceConnection {
        // Called when the connection with the service is established
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service

            Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()
            mRecepiHandler = IRecepiHandlerService.Stub.asInterface(service)
            if (mRecepiHandler?.recepi == null) {
                bottomNavigationView?.menu?.findItem(R.id.action_selected)?.isEnabled = false
                bottomNavigationView?.setSelectedItemId(R.id.action_search)
            }
            else {
                bottomNavigationView?.setSelectedItemId(R.id.action_selected)
            }
            mRecepiList = RecepiList(this@MainActivity)
            mRecepiList?.loadRecepies()
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
                    R.id.action_selected -> {
                        startCurrentRecepiFragment()
                    }
                    R.id.action_search -> {
                        startSearchFragment()
                    }
                    R.id.action_clear -> {
                        if (mRecepiHandler?.recepi != null) {
                            val alert = showAlertDialog(
                                this@MainActivity,
                                getString(R.string.delete), getString(R.string.delete_recepi)
                            )
                            alert.show()
                            val ok: Button = alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            ok.setOnClickListener { this@MainActivity.deleteRecepi(alert) }
                        }
                    }
                }
                return true
            }
        }

    // Public methods for fragments
    override fun setCurrentRecepi(recepiName: String) {
        mRecepiHandler?.addRecepi(mRecepiList.getRecepi(recepiName))
        // Next line will implicitly invoke startCurrentRecepiFragment
        bottomNavigationView?.menu?.findItem(R.id.action_selected)?.isEnabled = true
        bottomNavigationView?.setSelectedItemId(R.id.action_selected)
    }

    override fun getRecepiHandlerService(): IRecepiHandlerService? {
        return mRecepiHandler
    }

    // Private functions
    fun deleteRecepi(alert: android.app.AlertDialog) {
        alert.dismiss()
        removeFragments()
        mRecepiHandler?.addRecepi(null)
        bottomNavigationView?.menu?.findItem(R.id.action_selected)?.isEnabled = false
        bottomNavigationView?.setSelectedItemId(R.id.action_search)
    }

    fun startHomeFragment() {
        removeFragments()
        val homeFragment = RecepiHomeFragment.newInstance("", "")
        supportFragmentManager.beginTransaction()
            .add(R.id.main_top_frame, homeFragment, TOP_FRAGEMENT).commit()
        // Remove current recepi
        mRecepiHandler?.addRecepi(null)
        bottomNavigationView?.menu?.findItem(R.id.action_selected)?.isEnabled = false
    }

    fun startCurrentRecepiFragment() {
        removeFragments()
        val recepiName = mRecepiHandler?.recepi?.name
        val recepiDescrition = mRecepiHandler?.recepi?.description
        val image = mRecepiHandler?.recepi?.image
        val descriptionFragment = RecepiDescriptionFragment.newInstance(
            recepiName as String,
            recepiDescrition as String,
            image as Int
        )
        val stepsFragment = RecepiStepsFragment.newInstance("", "")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.main_top_frame, descriptionFragment, TOP_FRAGEMENT)
        transaction.add(R.id.main_bottom_frame, stepsFragment, BOTTOM_FRAGEMENT)
        transaction.commit()
    }

    fun startSearchFragment() {
        removeFragments()
        val selectorFragment = RecepiSelectorFragment.newInstance("", "")
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.main_top_frame, selectorFragment, TOP_FRAGEMENT)
        transaction.commit()
    }

    fun removeFragments() {
        val topFragment = supportFragmentManager.findFragmentByTag(TOP_FRAGEMENT)
        val bottomFragment = supportFragmentManager.findFragmentByTag(BOTTOM_FRAGEMENT)
        if (topFragment != null)
            supportFragmentManager.beginTransaction().remove(topFragment).commit()
        if (bottomFragment != null)
            supportFragmentManager.beginTransaction().remove(bottomFragment).commit()
    }

    fun loadRecepies(force: Boolean): RecepiList{
        mRecepiList.loadRecepies(force)
        return mRecepiList
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
