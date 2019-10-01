package be.hogent.faith.faith.emotionCapture.editDetail

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawing.makeDrawing.MakeDrawingFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.enterText.EnterTextFragment
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.util.replaceChildFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_edit_detail.image_editDetail_avatar
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.UUID

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

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()

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

        userViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        })
    }

    private fun loadAvatarImage(event: Event) {
        val image =
            event.emotionAvatar ?: ContextCompat.getDrawable(
                this.context!!,
                avatarOutlineResId
            ) as BitmapDrawable

        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels

        Glide.with(this)
            .load(image)
            // to refresh the picture and not get it from the glide cache
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            // scaling, otherwise picture is blurry
            .override((width * 0.3).toInt(), height)
            .fitCenter()
            .into(image_editDetail_avatar)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment()
    }

    companion object {
        private const val DETAIL_UUID = "UUID of the Detail"
        fun newInstance(detail: Detail, @DrawableRes avatarOutLineId: Int): DetailFragmentWithEmotionAvatar {
            return when (detail) {
                is AudioDetail -> AudioFragmentWithEmotionAvatar()
                is TextDetail -> TextFragmentWithEmotionAvatar.newInstance(
                    avatarOutLineId,
                    detail.uuid
                )
                is DrawingDetail -> DrawingFragmentWithEmotionAvatar()
                is PhotoDetail -> PhotoFragmentWithEmotionAvatar()
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
        }

        override fun setChildFragment() {
            replaceChildFragment(
                TakePhotoFragment.newInstance(), R.id.fragment_container_editDetail
            )
        }
    }

    class DrawingFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int): DrawingFragmentWithEmotionAvatar {
                return DrawingFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId)
                }
            }
        }

        override fun setChildFragment() {
            replaceChildFragment(
                MakeDrawingFragment.newInstance(), R.id.fragment_container_editDetail
            )
        }
    }

    class TextFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int): TextFragmentWithEmotionAvatar {
                return TextFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId)
                }
            }

            fun newInstance(@DrawableRes avatarOutLineId: Int, detailUUID: UUID): TextFragmentWithEmotionAvatar {
                return TextFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId).apply {
                        putSerializable(DETAIL_UUID, detailUUID)
                    }
                }
            }
        }

        override fun setChildFragment() {
            val detailUuid = arguments?.getSerializable(DETAIL_UUID) as UUID?
            if (detailUuid == null) {
                replaceChildFragment(
                    EnterTextFragment.newInstance(), R.id.fragment_container_editDetail
                )
            } else {
                replaceChildFragment(
                    EnterTextFragment.newInstance(detailUuid),
                    R.id.fragment_container_editDetail
                )
            }
        }
    }

    class AudioFragmentWithEmotionAvatar : DetailFragmentWithEmotionAvatar() {
        companion object {
            fun newInstance(@DrawableRes avatarOutLineId: Int): AudioFragmentWithEmotionAvatar {
                return AudioFragmentWithEmotionAvatar().apply {
                    arguments = getBundleForAvatarOutline(avatarOutLineId)
                }
            }
        }

        override fun setChildFragment() {
            replaceChildFragment(
                RecordAudioFragment.newInstance(), R.id.fragment_container_editDetail
            )
        }
    }

    interface EditDetailNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }
}
