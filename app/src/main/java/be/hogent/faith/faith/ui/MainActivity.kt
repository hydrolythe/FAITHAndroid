package be.hogent.faith.faith.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.EmotionType

class MainActivity : AppCompatActivity() {
    private lateinit var emotion: Event
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emotion = Event(EmotionType.AFRAID)
    }

    override fun onStart() {
        super.onStart()
        val tv = findViewById<TextView>(R.id.textView_main_title)
        tv.text = "Hullo there, ik ben ${emotion.emotionType}"
    }
}