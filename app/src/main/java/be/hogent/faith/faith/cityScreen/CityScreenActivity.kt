package be.hogent.faith.faith.cityScreen

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.emotionCapture.EmotionCaptureMainActivity
import be.hogent.faith.faith.loginOrRegister.LoginOrRegisterActivity
import be.hogent.faith.faith.overviewEvents.OverviewEventsFragment
import be.hogent.faith.faith.util.replaceFragment
import co.zsmb.materialdrawerkt.builders.drawer
import co.zsmb.materialdrawerkt.builders.footer
import co.zsmb.materialdrawerkt.draweritems.badgeable.primaryItem


class CityScreenActivity : AppCompatActivity(),
    CityScreenFragment.CityScreenNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                        Log.d("DRAWER", "Clicked logout")
                        val intent = Intent(applicationContext, LoginOrRegisterActivity::class.java)
                        intent.putExtra(LoginOrRegisterActivity.KEY_CLEAR_CREDENTIALS, true)
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
        replaceFragment(OverviewEventsFragment.newInstance(), R.id.fragment_container)
    }
}
