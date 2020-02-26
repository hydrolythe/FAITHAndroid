package be.hogent.faith.faith.backpackScreen


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.domain.models.detail.AudioDetail
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.domain.models.detail.TextDetail
import be.hogent.faith.faith.details.text.create.TextDetailFragment
import be.hogent.faith.faith.emotionCapture.editDetail.EditDetailViewModel
import be.hogent.faith.faith.util.replaceChildFragment
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

abstract class BackpackDetailFragment : Fragment() {

    private val backpackViewModel: BackpackViewModel by sharedViewModel()
    private lateinit var editDetailBinding: be.hogent.faith.databinding.FragmentEditDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_detail, container, false)
        editDetailBinding.lifecycleOwner = this

        return editDetailBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setChildFragment()
    }

    abstract fun setChildFragment(detail : Detail? = null)

    companion object {
        private const val DETAIL = "The detail to show"
        fun newInstance(detail: Detail): BackpackDetailFragment {
            return when (detail) {
                is TextDetail -> TextFragmentNoEmotionAvatar.newInstance(
                    detail
                )
                else -> TextFragmentNoEmotionAvatar.newInstance(
                    detail as TextDetail
                )
            }

        }
    }



        class TextFragmentNoEmotionAvatar : BackpackDetailFragment() {

            companion object {
                fun newInstance(): TextFragmentNoEmotionAvatar{
                    return TextFragmentNoEmotionAvatar()
                }

                fun newInstance(textDetail: TextDetail): TextFragmentNoEmotionAvatar {
                    return TextFragmentNoEmotionAvatar()
                    }
                }

            override fun setChildFragment(detail : Detail?) {
     //           val detail = arguments?.getSerializable(DETAIL) as TextDetail?
                val childFragment = if (detail == null) {
                    TextDetailFragment.newInstance()
                } else {
                    TextDetailFragment.newInstance(detail as TextDetail)
                }
                replaceChildFragment(childFragment, R.id.fragment_container_editDetail)
            }
            }

        }

