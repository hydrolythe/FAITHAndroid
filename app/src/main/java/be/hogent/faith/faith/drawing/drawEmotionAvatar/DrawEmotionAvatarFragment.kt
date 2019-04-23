package be.hogent.faith.faith.drawing.drawEmotionAvatar

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawAvatarBinding
import be.hogent.faith.faith.drawing.DrawFragment
import be.hogent.faith.faith.enterEventDetails.EventDetailsViewModel
import be.hogent.faith.service.usecases.SaveEmotionAvatarUseCase
import be.hogent.faith.util.TAG
import com.divyanshu.draw.widget.DrawView
import io.reactivex.disposables.CompositeDisposable
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatar.
 */
private const val ARG_AVATAR_RES_ID = "avatarResId"

/**
 * Constant for avatarOutlineResId to indicate that no avatar was passed as an argument for this fragment.
 */
private const val NO_AVATAR = -1

/**
 * Fragment that allows the user to color in the outline of their avatar according to their emotional state.
 */
class DrawEmotionAvatarFragment : DrawFragment() {
    override val drawView: DrawView
        get() = drawAvatarBinding.drawView

    private val eventDetailsViewModel: EventDetailsViewModel by sharedViewModel()

    private lateinit var drawAvatarBinding: FragmentDrawAvatarBinding

    /**
     * The resource ID for the avatar's outline, which will be used as a background for the drawing.
     */
    private var avatarOutlineResId: Int = NO_AVATAR

    private val saveEmotionAvatarUseCase: SaveEmotionAvatarUseCase by inject()

    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            avatarOutlineResId = it.getInt(ARG_AVATAR_RES_ID)
        }

        drawAvatarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw_avatar, container, false)
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
        drawViewModel.drawnPaths.observe(this, Observer {
            // It's very important that the drawCanvas doesn't create its own paths but uses a paths object
            // that is saved in such a way that it survives configuration changes. See [DrawViewModel].
            drawView.setActions(it)
        })
    }

    private fun configureDrawingCanvas() {
        // Paint with semi-transparent paint so you can always see the background's outline
        drawView.setAlpha(70)

        if (avatarOutlineResId != NO_AVATAR) {
            drawView.setPaintedBackground(resources.getDrawable(R.drawable.outline) as BitmapDrawable)
        }

        drawView.addDrawViewListener(object : DrawView.DrawViewListener {
            override fun onDrawingChanged(bitmap: Bitmap) {
                val saveRequest = saveEmotionAvatarUseCase.execute(
                    SaveEmotionAvatarUseCase.Params(bitmap, eventDetailsViewModel.event.value!!)
                ).subscribe({
                    Log.i(TAG, "Drawing was saved")
                    eventDetailsViewModel.updateEvent()
                }, {
                    Toast.makeText(context!!, R.string.error_saving_drawing, Toast.LENGTH_LONG).show()
                })
                disposables.add(saveRequest)
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