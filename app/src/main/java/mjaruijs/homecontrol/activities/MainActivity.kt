package mjaruijs.homecontrol.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import mjaruijs.homecontrol.R
import mjaruijs.homecontrol.activities.lampsetup.LampActivity
import mjaruijs.homecontrol.settings.PreferenceFragment
import mjaruijs.homecontrol.networking.server.EndConnection

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

    public override fun onStop() {
        super.onStop()
        EndConnection().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.synchronize -> preferenceFragment.synchronizeSettings()
            R.id.lamp_setup -> startActivity(Intent(applicationContext, LampActivity::class.java))
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onBackPressed() {
        if (backPressed) {
            EndConnection().execute()
            finish()
        } else {
            backPressedTime = System.currentTimeMillis()
            backPressed = true
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }

}
