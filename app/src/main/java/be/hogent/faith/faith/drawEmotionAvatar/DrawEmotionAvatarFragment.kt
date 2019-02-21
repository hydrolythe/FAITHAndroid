package be.hogent.faith.faith.drawEmotionAvatar

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawAvatarBinding
import be.hogent.faith.domain.models.Event
import be.hogent.faith.faith.util.TAG
import be.hogent.faith.faith.util.toast
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import com.divyanshu.draw.widget.DrawView
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDateTime

/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatar.
 */
private const val ARG_AVATAR_RES_ID = "avatarResId"

/**
 * Constant for [avatarOutlineResId] to indicate that no avatar was passed as an argument for this fragment.
 */
private const val NO_AVATAR = -1

/**
 * Fragment that allows the user to color in the outline of their avatar according to their emotional state.
 */
class DrawEmotionAvatarFragment : Fragment() {

    private val drawEmotionViewModel: DrawEmotionViewModel by sharedViewModel()
    private lateinit var drawAvatarBinding: FragmentDrawAvatarBinding

    /**
     * The resource ID for the avatar's outline, which will be used as a background for the drawing.
     */
    private var avatarOutlineResId: Int = NO_AVATAR

    private val saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase by inject()

    // TODO: replace with the actual Event once all fragments are tied together
    private val event = Event(LocalDateTime.now(), "TestDescription")

    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            avatarOutlineResId = it.getInt(ARG_AVATAR_RES_ID)
        }

        drawAvatarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw_avatar, container, false)
        drawAvatarBinding.drawEmotionViewModel = drawEmotionViewModel
        drawAvatarBinding.lifecycleOwner = this
        return drawAvatarBinding.root
    }

    override fun onStart() {
        super.onStart()

        updateUI()
        listenToViewModelEvents()

        drawAvatarBinding.drawCanvas.addDrawViewListener(object : DrawView.DrawViewListener {
            override fun onDrawingChanged(bitmap: Bitmap) {
                val saveRequest = saveEmotionAvatarUseCase.execute(
                    SaveEmotionAvatarUseCase.SaveBitmapParams(bitmap, event)
                ).subscribe({
                    Log.i(TAG, "Drawing was saved")
                }, {
                    context?.toast(getString(R.string.error_saving_drawing))
                })
                disposables.add(saveRequest)
            }
        })
    }

    private fun listenToViewModelEvents() {
        drawEmotionViewModel.drawnPaths.observe(this, Observer {
            drawAvatarBinding.drawCanvas.setPaths(it)
        })
        drawEmotionViewModel.selectedColor.observe(this, Observer { newColor ->
            Log.i(TAG, "Color set to $newColor")
            drawAvatarBinding.drawCanvas.setColor(newColor)
        })

        drawEmotionViewModel.selectedLineWidth.observe(this, Observer { lineWidth ->
            Log.i(TAG, "Line width set to $lineWidth")
            drawAvatarBinding.drawCanvas.setStrokeWidth(lineWidth.width)
        })
        drawEmotionViewModel.undoClicked.observe(this, Observer {
            Log.i(TAG, "Last action undone")
            drawAvatarBinding.drawCanvas.undo()
        })
    }

    private fun updateUI() {
        setDrawViewBackground()
        configureDrawCanvas()
    }

    private fun configureDrawCanvas() {
        drawAvatarBinding.drawCanvas.setAlpha(70)
    }

    private fun setDrawViewBackground() {
        if (avatarOutlineResId != NO_AVATAR) {
            drawAvatarBinding.drawCanvas.setPaintedBackground(resources.getDrawable(R.drawable.outline) as BitmapDrawable)
        }
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