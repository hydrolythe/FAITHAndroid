package be.hogent.faith.faith.cityScreen

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.util.replaceFragment
import android.R
import be.hogent.faith.faith.loginOrRegister.LoginManager
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem

class CityScreenActivity : AppCompatActivity(),
    CityScreenFragment.CityScreenNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(be.hogent.faith.R.layout.activity_main)

        // If a configuration state occurs we don't want to remove all fragments and start again from scratch.
        // savedInstanceState is null when the activity is first created, and not null when being recreated.
        // Using this we should only add a new fragment when savedInstanceState is null
        if (savedInstanceState == null) {
            val fragment = CityScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(be.hogent.faith.R.id.fragment_container, fragment)
                .commit()
        }

        drawer {
            footer {
                primaryItem("Logout") {
                    onClick { _ ->
                        val intent = Intent(applicationContext, LoginOrRegisterActivity::class.java)
                        intent.putExtra(LoginManager.KEY_CLEAR_CREDENTIALS, true)
                        startActivity(intent)
                        finish()
                        true
                    }
                }
            }
        }
    }

    override fun startEmotionCapture() {
        val intent = Intent(this, EmotionCaptureMainActivity::class.java)
        startActivity(intent)
    }

    override fun startOverviewEventsFragment() {
        replaceFragment(OverviewEventsFragment.newInstance(), be.hogent.faith.R.id.fragment_container)
    }

    override fun onBackPressed() {
        val alertDialog: AlertDialog = this.run {
            val builder = AlertDialog.Builder(this).apply {
                setTitle(getString(be.hogent.faith.R.string.cityScreen_logOut))
                setMessage(getString(be.hogent.faith.R.string.cityscreen_stad_verlaten))
                setPositiveButton(R.string.ok) { _, _ ->
                    // TODO nog uitloggen
                    navigateUpTo(Intent(applicationContext, LoginOrRegisterActivity::class.java))
                }
                setNegativeButton(be.hogent.faith.R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }
}
