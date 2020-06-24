package be.hogent.faith.faith.skyscraper.goal

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatSeekBar

class VerticalSeekBar : AppCompatSeekBar {
    /**
     * A change listener registrating start and stop of tracking. Need an own listener because the listener in SeekBar
     * is private.
     */
    private var mOnSeekBarChangeListener: OnSeekBarChangeListener? = null

    /**
     * Standard constructor to be implemented for all views.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     */
    constructor(context: Context?) : super(context) {}

    /**
     * Standard constructor to be implemented for all views.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     */
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
    }

    /**
     * Standard constructor to be implemented for all views.
     *
     * @param context The Context the view is running in, through which it can access the current theme, resources, etc.
     * @param attrs The attributes of the XML tag that is inflating the view.
     * @param defStyle An attribute in the current theme that contains a reference to a style resource that supplies default
     * values for the view. Can be 0 to not look for defaults.
     */
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    override fun onSizeChanged(
        width: Int,
        height: Int,
        oldWidth: Int,
        oldHeight: Int
    ) {
        super.onSizeChanged(height, width, oldHeight, oldWidth)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(@NonNull c: Canvas) {
        c.rotate(ROTATION_ANGLE.toFloat())
        c.translate(-height.toFloat(), 0f)
        super.onDraw(c)
    }

    override fun setOnSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        // Do not use super for the listener, as this would not set the fromUser flag properly
        mOnSeekBarChangeListener = listener
    }

    override fun onTouchEvent(@NonNull event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                setProgressInternally(
                    max - (max * event.y / height).toInt(),
                    true
                )
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener!!.onStartTrackingTouch(this)
                }
            }
            MotionEvent.ACTION_MOVE -> setProgressInternally(
                max - (max * event.y / height).toInt(),
                true
            )
            MotionEvent.ACTION_UP -> {
                setProgressInternally(
                    max - (max * event.y / height).toInt(),
                    true
                )
                if (mOnSeekBarChangeListener != null) {
                    mOnSeekBarChangeListener!!.onStopTrackingTouch(this)
                }
            }
            MotionEvent.ACTION_CANCEL -> if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener!!.onStopTrackingTouch(this)
            }
            else -> {
            }
        }
        return true
    }

    /**
     * Set the progress by the user. (Unfortunately, Seekbar.setProgressInternally(int, boolean) is not accessible.)
     *
     * @param progress the progress.
     * @param fromUser flag indicating if the change was done by the user.
     */
    fun setProgressInternally(progress: Int, fromUser: Boolean) {
        if (progress != getProgress()) {
            super.setProgress(progress)
            if (mOnSeekBarChangeListener != null) {
                mOnSeekBarChangeListener!!.onProgressChanged(this, progress, fromUser)
            }
        }
        onSizeChanged(width, height, 0, 0)
    }

    override fun setProgress(progress: Int) {
        setProgressInternally(progress, false)
    }

    companion object {
        /**
         * The angle by which the SeekBar view should be rotated.
         */
        private const val ROTATION_ANGLE = -90
    }
}