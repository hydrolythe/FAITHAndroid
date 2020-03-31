package be.hogent.faith.faith.cinema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R

class CinemaActivity : AppCompatActivity(),CinemaStartScreenFragment.CinemaNavigationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cinema)

        if (savedInstanceState == null) {
            val fragment = CinemaStartScreenFragment.newInstance()
            supportFragmentManager.beginTransaction()
                    .add(R.id.cinema_fragment_container, fragment)
                    .commit()
        }
    }

    override fun closeCinema() {
        finish()
    }
}
