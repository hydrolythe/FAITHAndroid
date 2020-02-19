package be.hogent.faith.faith.backpackScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.databinding.FragmentBackpackBinding


class BackpackScreenFragment : Fragment() {

    companion object {
        fun newInstance() = BackpackScreenFragment()
    }
    private lateinit var backpackBinding: FragmentBackpackBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        backpackBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_backpack, container, false)
        backpackBinding.lifecycleOwner = this

        return backpackBinding.root
    }
}
