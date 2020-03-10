package be.hogent.faith.faith.emotionCapture.editDetail

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.domain.models.Event
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.DrawingDetail
import be.hogent.faith.domain.models.detail.PhotoDetail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.audio.RecordAudioFragment
import be.hogent.faith.faith.details.drawing.create.DrawingDetailFragment
import be.hogent.faith.faith.details.photo.create.TakePhotoFragment
import be.hogent.faith.faith.details.photo.view.ReviewPhotoFragment
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.util.replaceChildFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_edit_detail.image_editDetail_avatar
import kotlinx.android.synthetic.main.fragment_edit_detail.textView_editDetail_avatar
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

abstract class DetailFragmentWithEmotionAvatar : Fragment() {
    private var navigation: EditDetailNavigationListener? = null
    private val eventViewModel: EventViewModel by sharedViewModel()
    private val editDetailViewModel: EditDetailViewModel by viewModel()
    private lateinit var editDetailBinding: be.hogent.faith.databinding.FragmentEditDetailBinding
    private var avatarOutlineResId: Int = NO_AVATAR

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EditDetailNavigationListener) {
            navigation = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            avatarOutlineResId = it.getInt(ARG_AVATAR_RES_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_detail, container, false)
        editDetailBinding.editdetailViewModel = editDetailViewModel
        editDetailBinding.lifecycleOwner = this

        return editDetailBinding.root
    }

    override fun onStart() {
        super.onStart()
        startListeners()
        eventViewModel.updateEvent()
    }

    private fun startListeners() {
        eventViewModel.event.observe(this, Observer { event ->
            loadAvatarImage(event)
        })
        editDetailViewModel.emotionAvatarButtonClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
    }

    private fun loadAvatarImage(event: Event) {
        if (event.emotionAvatar != null) {
            textView_editDetail_avatar.text = ""

            val width = Resources.getSystem().displayMetrics.widthPixels
            val height = Resources.getSystem().displayMetrics.heightPixels

            Glide.with(this)
                .load(event.emotionAvatar)
                // to refresh the picture and not get it from the glide cache
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                // scaling, otherwise picture is blurry
                .override((width * 0.3).toInt(), height)
                .fitCenter()
                .into(image_editDetail_avatar)
        } else
            textView_editDetail_avatar.visibility = View.VISIBLE
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment()
    }

    companion object {
        private const val DETAIL = "The detail to show"
        fun newInstance(detail: Detail, @DrawableRes avatarOutLineId: Int): DetailFragmentWithEmotionAvatar {
            return when (detail) {
                is AudioDetail -> AudioFragmentWithEmotionAvatar.newInstance(
                    avatarOutLineId, detail
                )
                is TextDetail -> TextFragmentWithEmotionAvatar.newInstance(
                    avatarOutLineId, detail
                )
                is DrawingDetail -> DrawingFragmentWithEmotionAvatar.newInstance(
                    avatarOutLineId, detail
                )
                is PhotoDetail -> PhotoFragmentWithEmotionAvatar.newInstance(
                    avatarOutLineId, detail
                )
            }
        }

        private fun getBundleForAvatarOutline(@DrawableRes avatarOutLineId: Int): Bundle {
            return Bundle().apply {
                putInt(ARG_AVATAR_RES_ID, avatarOutLineId)
            }
        }
    }

    abstract fun setChildFragment()

    class PhotoFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int): PhotoFragmentWithEmotionAvatar {
                return PhotoFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId)
                }
            }

            fun newInstance(@DrawableRes avatarOutLineId: Int, existingDetail: PhotoDetail): PhotoFragmentWithEmotionAvatar {
                return PhotoFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId).apply {
                        putSerializable(DETAIL, existingDetail)
                    }
                }
            }
        }

        override fun setChildFragment() {
            val detail = arguments?.getSerializable(DETAIL) as PhotoDetail?
            val childFragment = if (detail == null) {
                TakePhotoFragment.newInstance()
            } else {
                ReviewPhotoFragment.newInstance(detail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editDetail)
        }
    }

    class DrawingFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int): DrawingFragmentWithEmotionAvatar {
                return DrawingFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId)
                }
            }

            fun newInstance(@DrawableRes avatarOutLineId: Int, drawingDetail: DrawingDetail): DrawingFragmentWithEmotionAvatar {
                return DrawingFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId).apply {
                        putSerializable(DETAIL, drawingDetail)
                    }
                }
            }
        }

        override fun setChildFragment() {
            val detail = arguments?.getSerializable(DETAIL) as DrawingDetail?
            val childFragment = if (detail == null) {
                DrawingDetailFragment.newInstance()
            } else {
                DrawingDetailFragment.newInstance(detail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editDetail)
        }
    }

    class TextFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int): TextFragmentWithEmotionAvatar {
                return TextFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId)
                }
            }

            fun newInstance(@DrawableRes avatarOutLineId: Int, textDetail: TextDetail): TextFragmentWithEmotionAvatar {
                return TextFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId).apply {
                        putSerializable(DETAIL, textDetail)
                    }
                }
            }
        }

        override fun setChildFragment() {
            val detail = arguments?.getSerializable(DETAIL) as TextDetail?
            val childFragment = if (detail == null) {
                TextDetailFragment.newInstance()
            } else {
                TextDetailFragment.newInstance(detail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editDetail)
        }
    }

    class AudioFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int, existingDetail: AudioDetail? = null): AudioFragmentWithEmotionAvatar {
                return AudioFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId).apply {
                        putSerializable(DETAIL, existingDetail)
                    }
                }
            }
        }

        override fun setChildFragment() {
            val existingDetail = arguments?.getSerializable(DETAIL) as AudioDetail?
            val childFragment = if (existingDetail == null) {
                RecordAudioFragment.newInstance()
            } else {
                RecordAudioFragment.newInstance(existingDetail)
            }
            replaceChildFragment(childFragment, R.id.fragment_container_editDetail)
        }
    }

    interface EditDetailNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }
}
