package be.hogent.faith.faith.drawEmotionAvatar

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
import org.koin.android.viewmodel.ext.android.viewModel

class DrawEmotionAvatarFragment : Fragment() {

    private val drawEmotionViewModel: DrawEmotionViewModel by viewModel()
    private lateinit var drawAvatarBinding: FragmentDrawAvatarBinding
    private var avatarOutlineResId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        arguments?.let {
            avatarOutlineResId = it.getInt(ARG_AVATAR_RES_ID)
        }

        drawAvatarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw_avatar, container, false)
        drawAvatarBinding.drawEmotionViewModel = drawEmotionViewModel
        drawAvatarBinding.setLifecycleOwner(this)
        return drawAvatarBinding.root
    }

    override fun onStart() {
        super.onStart()
        updateUI()
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
        setDrawingOpacity()
    }

    private fun setDrawingOpacity() {
        drawAvatarBinding.drawCanvas.setAlpha(70)
    }

    private fun setDrawViewBackground() {
        if (avatarOutlineResId != -1) {
            drawAvatarBinding.drawCanvas.setPaintedBackground(resources.getDrawable(R.drawable.outline))
        }
    }


    companion object {
        const val TAG = "DrawEmotionFragment"
        const val ARG_AVATAR_RES_ID = "avatarResId"

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