package be.hogent.faith.faith.drawEmotionAvatar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentDrawAvatarBinding
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

    companion object {
        const val TAG = "DrawEmotionFragment"
    }
}