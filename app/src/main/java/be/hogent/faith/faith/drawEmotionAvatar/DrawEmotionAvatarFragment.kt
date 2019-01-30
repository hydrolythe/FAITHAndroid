package be.hogent.faith.faith.drawEmotionAvatar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawAvatarBinding
import kotlinx.android.synthetic.main.fragment_draw_avatar.*
import org.koin.android.viewmodel.ext.android.viewModel

class DrawEmotionAvatarFragment : Fragment() {

    private val drawEmotionViewModel: DrawEmotionViewModel by viewModel()
    private lateinit var drawAvatarBinding: FragmentDrawAvatarBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        drawAvatarBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_draw_avatar, container, false)
        drawAvatarBinding.drawEmotionViewModel = drawEmotionViewModel
        drawAvatarBinding.setLifecycleOwner(this)
        return drawAvatarBinding.root
    }

    override fun onStart() {
        super.onStart()
        // TODO: resize drawable so it sits in the middle of the view
        draw_canvas.background = resources.getDrawable(R.drawable.background_test)
        drawEmotionViewModel.selectedColor.observe(this, Observer { newColor ->
            draw_canvas.setColor(newColor)
        })

        drawEmotionViewModel.selectedLineWidth.observe(this, Observer { lineWidth ->
            draw_canvas.setStrokeWidth(lineWidth.width)
        })

        // Library version doesn't seem to have this made public yet, awaiting answer from dev
//        drawEmotionViewModel.eraserSelected.observe(this, Observer { state ->
//                        draw_canvas.toggleEraser()
//        })
    }

    companion object {
        const val TAG = "DrawEmotionFragment"
    }
}