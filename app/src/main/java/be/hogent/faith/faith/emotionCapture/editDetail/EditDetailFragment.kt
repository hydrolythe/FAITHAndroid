package be.hogent.faith.faith.emotionCapture.editDetail

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
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
import be.hogent.faith.faith.UserViewModel
import be.hogent.faith.faith.di.KoinModules
import be.hogent.faith.faith.emotionCapture.drawing.makeDrawing.MakeDrawingFragment
import be.hogent.faith.faith.emotionCapture.enterEventDetails.EventViewModel
import be.hogent.faith.faith.emotionCapture.enterEventDetails.SaveEventDialog
import be.hogent.faith.faith.emotionCapture.enterText.EnterTextFragment
import be.hogent.faith.faith.emotionCapture.recordAudio.RecordAudioFragment
import be.hogent.faith.faith.emotionCapture.takePhoto.TakePhotoFragment
import be.hogent.faith.faith.util.replaceChildFragment
import be.hogent.faith.util.TAG
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_edit_detail.image_editDetail_avatar
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Key for this Fragment's [Bundle] to hold the DetailType pointing to type of detail
 */
const val ARG_DETAILTYPE = "detailType"
/**
 * Key for this Fragment's [Bundle] to hold the resource ID pointing to the outline drawing of the avatarName.
 */
private const val ARG_AVATAR_RES_ID = "avatarResId"
/**
 * Constant for avatarOutlineResId to indicate that no avatarName was passed as an argument for this fragment.
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
    private lateinit var editDetailBinding: be.hogent.faith.databinding.FragmentEditDetailBinding
    private var avatarOutlineResId: Int = NO_AVATAR

    private val userViewModel: UserViewModel = getKoin().getScope(KoinModules.USER_SCOPE_ID).get()
    private lateinit var saveDialog: SaveEventDialog

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is EditDetailNavigationListener) {
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
        editDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_detail, container, false)
        editDetailBinding.editdetailViewModel = editDetailViewModel
        editDetailBinding.lifecycleOwner = this

        return editDetailBinding.root
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
                .into(image_editDetail_avatar)
        })
        editDetailViewModel.emotionAvatarButtonClicked.observe(this, Observer {
            navigation?.startDrawEmotionAvatarFragment()
        })
        editDetailViewModel.sendButtonClicked.observe(this, Observer {
            saveDialog = SaveEventDialog.newInstance()
            saveDialog.show(fragmentManager!!, null)
        })
        eventViewModel.updateEvent()

        userViewModel.eventSavedSuccessFully.observe(this, Observer {
            Toast.makeText(context, R.string.save_event_success, Toast.LENGTH_LONG).show()
            saveDialog.dismiss()
            navigation?.eventSaved()
        })
        userViewModel.errorMessage.observe(this, Observer { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        when (detailType) {
            DetailType.PICTURE -> replaceChildFragment(
                TakePhotoFragment.newInstance(),
                R.id.fragment_container_editDetail
            )
            DetailType.AUDIO -> replaceChildFragment(
                RecordAudioFragment.newInstance(),
                R.id.fragment_container_editDetail
            )
            DetailType.TEXT -> replaceChildFragment(
                EnterTextFragment.newInstance(),
                R.id.fragment_container_editDetail
            )
            DetailType.DRAWING -> replaceChildFragment(
                MakeDrawingFragment.newInstance(), R.id.fragment_container_editDetail
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
        fun eventSaved()
    }
}
