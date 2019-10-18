package be.hogent.faith.faith.library.eventDetailFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import be.hogent.faith.R
import kotlinx.android.synthetic.main.fragment_text_detail.lbl_text_detail

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val DESCRIPTION = "description"

/**
 * A simple [Fragment] subclass.
 * Use the [TextDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TextDetailFragment : Fragment() {

    private var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            description = it.getString(DESCRIPTION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_text_detail, container, false)
        lbl_text_detail.text = description
        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TextDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(description: String) =
            TextDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(DESCRIPTION, description)
                }
            }
    }
}
