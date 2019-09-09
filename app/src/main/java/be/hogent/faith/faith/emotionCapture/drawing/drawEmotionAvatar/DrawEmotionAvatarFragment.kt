package be.hogent.faith.faith.emotionCapture.drawing.drawEmotionAvatar

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawAvatarBinding
import be.hogent.faith.faith.emotionCapture.drawing.DrawFragment
import be.hogent.faith.faith.emotionCapture.drawing.DrawViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import com.divyanshu.draw.widget.DrawView
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatarName.
 */
private const val ARG_AVATAR_RES_ID = "avatarResId"

/**
 * Constant for avatarOutlineResId to indicate that no avatarName was passed as an argument for this fragment.
 */
private const val NO_AVATAR = -1

/**
 * Fragment that allows the user to color in the outline of their avatarName according to their emotional state.
 */
class DrawEmotionAvatarFragment : DrawFragment() {

    override val drawViewModel: DrawViewModel by sharedViewModel()

    override val drawView: DrawView
        get() = drawAvatarBinding.drawView

    private val eventViewModel: EventViewModel by sharedViewModel()

    private lateinit var drawAvatarBinding: FragmentDrawAvatarBinding

    /**
     * The resource ID for the avatarName's outline, which will be used as a background for the drawing.
     */
    private var avatarOutlineResId: Int = NO_AVATAR

    private val disposables = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            avatarOutlineResId = it.getInt(ARG_AVATAR_RES_ID)
        }

        drawAvatarBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_draw_avatar, container, false)
        drawAvatarBinding.drawViewModel = drawViewModel
        drawAvatarBinding.lifecycleOwner = this
        return drawAvatarBinding.root
    }

    override fun onStart() {
        super.onStart()

        configureDrawingCanvas()
        listenToViewModelEvents()
    }

    private fun listenToViewModelEvents() {
        eventViewModel.errorMessage.observe(this, Observer {
            Toast.makeText(context, getString(R.string.error_saving_drawing), Toast.LENGTH_SHORT)
                .show()
        })
    }

    private fun configureDrawingCanvas() {
        // Paint with semi-transparent paint so you can always see the background's outline
        drawView.setAlpha(70)

        if (avatarOutlineResId != NO_AVATAR) {
            drawView.setPaintedBackground(
                ContextCompat.getDrawable(
                    context!!,
                    avatarOutlineResId
                ) as BitmapDrawable
            )
        }

        drawView.addDrawViewListener(object : DrawView.DrawViewListener {
            override fun onDrawingChanged(bitmap: Bitmap) {
                eventViewModel.saveEmotionAvatarImage(bitmap)
            }
        })
    }

    override fun onStop() {
        super.onStop()
        disposables.clear()
    }

    companion object {
        /**
         * Creates a new instances of this fragment, with the background of the drawView set to the given drawable.
         * @param avatarOutLineId The resource ID of the drawable that will be placed on the background.
         */
        fun newInstance(@DrawableRes avatarOutLineId: Int): DrawEmotionAvatarFragment {
            return DrawEmotionAvatarFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_AVATAR_RES_ID, avatarOutLineId)
                }
            }
        }
    }
}