package be.hogent.faith.faith.skyscraper

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import org.koin.android.ext.android.getKoin

/**
 * An activity representing a list of Events of the user.
 */
class SkyscraperActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            val fragment = SkyscraperStartScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }
}