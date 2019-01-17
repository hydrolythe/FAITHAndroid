package be.hogent.faith.faith.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.createUser.CreateEventFragment

class MainActivity : AppCompatActivity() {
    private lateinit var emotion: Event
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, CreateEventFragment())
            .commit()
    }

}