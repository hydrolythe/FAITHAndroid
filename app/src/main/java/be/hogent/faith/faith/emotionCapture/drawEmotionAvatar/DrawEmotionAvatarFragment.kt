package be.hogent.faith.faith.emotionCapture.drawEmotionAvatar

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawAvatarBinding
import be.hogent.faith.faith.details.drawing.create.DrawFragment
import be.hogent.faith.faith.details.drawing.create.DrawViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import com.divyanshu.draw.widget.DrawView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.panel_drawing_colors.hr_customColor
import kotlinx.android.synthetic.main.panel_drawing_colors.paintpot_customColor
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatarName.
 */
private const val ARG_AVATAR_RES_ID = "avatarResId"

/**
 * Constant for avatarOutlineResId to indicate that no avatarName was passed as an argument for this fragment.
 */
private const val NO_AVATAR = -1

/**
 * Alpha value used for the colors
 */
private const val COLOR_ALPHA = 230 // 90%

/**
 * Fragment that allows the user to color in the outline of their avatarName according to their emotional state.
 */
class DrawEmotionAvatarFragment : DrawFragment() {

    override val drawViewModel: DrawViewModel by viewModel()

    override val drawView: DrawView
        get() = drawAvatarBinding.drawAvatarDrawView

    override val colorAlpha: Int
        get() = COLOR_ALPHA

    /**
     * saves the bitmap when the save button is clicked and goes back to the event
     */
    override fun saveBitmap() {
        eventViewModel.saveEmotionAvatarImage(drawView.getBitmap())
        navigation?.backToEvent()
    }

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
        startListeners()
        hr_customColor.visibility = View.GONE
        paintpot_customColor.visibility = View.GONE
    }

    override fun showExitAlert() {
        val alertDialog: AlertDialog = this.run {
            val builder = AlertDialog.Builder(this.requireContext()).apply {
                setTitle(getString(R.string.dialog_wil_je_teruggaan))
                setMessage(R.string.dialog_to_the_event_message)
                setPositiveButton(R.string.ok) { _, _ ->
                    navigation!!.backToEvent()
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun configureDrawingCanvas() {
        // Paint with semi-transparent paint so you can always see the background's outline
        drawView.setAlpha(COLOR_ALPHA)
        // Leave some whitespace around the avatar
        if (eventViewModel.event.value?.emotionAvatar == null) {
            drawAvatar()
        } else {
            loadExistingDrawing()
        }
    }

    /**
     * draw avatar on screen. the background is set to the width of the avatar
     */
    private fun drawAvatar() {
        drawView.fullScreenBackground = false
        if (avatarOutlineResId != NO_AVATAR) {
            drawView.setPaintedBackground(
                ContextCompat.getDrawable(
                    requireContext(),
                    avatarOutlineResId
                ) as BitmapDrawable
            )
        }
    }

    /**
     * load existing drawing. The drawing takes the width and height of the full screen
     */
    private fun loadExistingDrawing() {
        drawView.fullScreenBackground = true
        drawView.setImageBackground(eventViewModel.event.value!!.emotionAvatar!!)
    }

    private fun startListeners() {
        drawViewModel.restartClicked.observe(this, Observer {
            val alertDialog: AlertDialog = this.run {
                val builder = AlertDialog.Builder(this.requireContext()).apply {
                    setTitle(R.string.dialog_to_restart_drawavatar_title)
                    setMessage(R.string.dialog_to_restart_drawavatar_message)
                    setPositiveButton(R.string.ok) { _, _ ->
                        drawView.clearCanvas()
                        drawAvatar()
                    }
                    setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.cancel()
                    }
                }
                builder.create()
            }
            alertDialog.show()
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