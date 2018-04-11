package mjaruijs.servertest.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import mjaruijs.servertest.R
import mjaruijs.servertest.fragments.PreferenceFragment
import mjaruijs.servertest.server.ConnectionResponse
import mjaruijs.servertest.server.EndConnection
import mjaruijs.servertest.server.authentication.ConnectionState
import mjaruijs.servertest.server.authentication.ConnectionState.CONNECTION_CLOSED

class MainActivity : AppCompatActivity() {

    private val preferenceFragment = PreferenceFragment()

    private var backPressedTime: Long = -1
    private var backPressed: Boolean = false

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        fragmentManager.beginTransaction()
                .replace(R.id.preferences_holder, preferenceFragment)
                .commitAllowingStateLoss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.synchronize -> preferenceFragment.synchronizeSettings()
            else -> super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onBackPressed() {
        if (backPressed) {
            EndConnection(object : ConnectionResponse() {
                override fun resultState(result: ConnectionState) {
                    super.resultState(result)
                    when (result) {
                        CONNECTION_CLOSED -> {
                            Toast.makeText(applicationContext, "You are now logged out", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        else -> {
                            Toast.makeText(applicationContext, "Connection could not be closed..", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }).execute()
        } else {
            backPressedTime = System.currentTimeMillis()
            backPressed = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

}
