package be.hogent.faith.faith.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import be.hogent.faith.R
import be.hogent.faith.faith.drawEmotionAvatar.DrawEmotionAvatarFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = DrawEmotionAvatarFragment.newInstance(R.drawable.outline)
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container,fragment)
            .commit()
    }
}