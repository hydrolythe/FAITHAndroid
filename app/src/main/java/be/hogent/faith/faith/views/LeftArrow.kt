package be.hogent.faith.faith.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import be.hogent.faith.R

class LeftArrow(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    init {
        inflate(context, R.layout.arrow_to_left, this)

        val imageView: ImageView = findViewById(R.id.img_arrow_left)
        val textView: TextView = findViewById(R.id.txt_arrow_left)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.arrow_to_right)
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.arrow_to_right_image))
        textView.text = attributes.getString(R.styleable.arrow_to_right_text)
        attributes.recycle()
    }
}