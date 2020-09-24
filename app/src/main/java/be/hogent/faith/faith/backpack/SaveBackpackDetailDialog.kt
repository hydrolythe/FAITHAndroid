package be.hogent.faith.faith.backpack

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSaveBackpackdetailBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.cinema.SaveCinemaDetailDialog
import be.hogent.faith.faith.details.BackpackDetailsMetaDataViewModel
import be.hogent.faith.faith.details.DetailsMetaDataViewModel
import be.hogent.faith.faith.detailscontainer.SaveDetailsContainerDetailDialog
import org.koin.android.viewmodel.ext.android.getViewModel

import kotlin.reflect.KClass

class SaveBackpackDetailDialog(detailType: KClass<Detail>) : SaveDetailsContainerDetailDialog(detailType) {

    companion object {
        fun newInstance(detailType: KClass<Detail>): SaveCinemaDetailDialog {
            return SaveCinemaDetailDialog(detailType)
        }
    }

    override val detailsMetadataViewModel: DetailsMetaDataViewModel
        get() = getViewModel<BackpackDetailsMetaDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_backpackdetail, container, false)
        (saveDetailBinding as DialogSaveBackpackdetailBinding).detailsMetaDataViewModel = detailsMetadataViewModel
        saveDetailBinding.lifecycleOwner = this
        return saveDetailBinding.root
    }
}
