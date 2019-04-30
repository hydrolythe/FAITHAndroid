package be.hogent.faith.faith.emotionCapture.editDetail

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.hogent.faith.R
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.util.replaceChildFragment
import be.hogent.faith.util.TAG
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_edit_detail.image_editdetail_avatar
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Key for this Fragment's [Bundle] to hold the DetailType pointing to type of detail
 */
const val ARG_DETAILTYPE = "detailType"
/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatar.
 */
private const val ARG_AVATAR_RES_ID = "avatarResId"
/**
 * Constant for avatarOutlineResId to indicate that no avatar was passed as an argument for this fragment.
 */
private const val NO_AVATAR = -1

enum class DetailType {
    TEXT,
    PICTURE,
    MUSIC,
    AUDIO,
    DRAWING,
    VIDEO
}

class EditDetailFragment : Fragment() {
    private var detailType: DetailType? = null
    private var navigation: EditDetailNavigationListener? = null
    private val eventViewModel: EventViewModel by sharedViewModel()
    private val editDetailViewModel: EditDetailViewModel by viewModel()
    private lateinit var editdetailBinding: be.hogent.faith.databinding.FragmentEditDetailBinding
    private var avatarOutlineResId: Int = NO_AVATAR

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EditDetailFragment.EditDetailNavigationListener) {
            navigation = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            detailType = it.getSerializable(ARG_DETAILTYPE) as DetailType
            avatarOutlineResId = it.getInt(ARG_AVATAR_RES_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editdetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_detail, container, false)
        editdetailBinding.editdetailViewModel = editDetailViewModel
        editdetailBinding.lifecycleOwner = this

        return editdetailBinding.root
    }

    override fun onStart() {
        super.onStart()
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        eventViewModel.event.observe(this, Observer {
            val image =
                it.emotionAvatar ?: ContextCompat.getDrawable(this.context!!, avatarOutlineResId) as BitmapDrawable
            Glide.with(this)
                .load(image)
                // to refresh the picture and not get it from the glide cache
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                // scaling, otherwise picture is blurry
                .override((width * 0.3).toInt(), height)
                .fitCenter()
                .into(image_editdetail_avatar)
        })
        editDetailViewModel.emotionAvatarButtonClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
        eventViewModel.updateEvent()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        when (detailType) {
            DetailType.PICTURE -> replaceChildFragment(
                TakePhotoFragment.newInstance(),
                be.hogent.faith.R.id.fragment_container_editdetail
            )
            DetailType.AUDIO -> replaceChildFragment(
                RecordAudioFragment.newInstance(),
                be.hogent.faith.R.id.fragment_container_editdetail
            )
            else -> Log.e(TAG, "type not defined")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(detailType: DetailType, @DrawableRes avatarOutLineId: Int) =
            EditDetailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_DETAILTYPE, detailType)
                    putInt(ARG_AVATAR_RES_ID, avatarOutLineId)
                }
            }
    }

    interface EditDetailNavigationListener {
        fun startDrawEmotionAvatarFragment()
    }
}
