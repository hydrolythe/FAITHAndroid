package be.hogent.faith.faith.cinema

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogCinemaSaveDetailBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.details.CinemaDetailsMetaDataViewModel
import be.hogent.faith.faith.details.DetailsMetaDataViewModel
import be.hogent.faith.faith.detailscontainer.SaveDetailsContainerDetailDialog
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass

class SaveCinemaDetailDialog(detailType: KClass<Detail>) : SaveDetailsContainerDetailDialog(detailType) {

    companion object {
        fun newInstance(detailType: KClass<Detail>): SaveCinemaDetailDialog {
            return SaveCinemaDetailDialog(detailType)
        }
    }

    override val detailsMetadataViewModel: DetailsMetaDataViewModel
        get() = getViewModel<CinemaDetailsMetaDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_cinema_save_detail, container, false)
        (saveDetailBinding as DialogCinemaSaveDetailBinding).detailsMetaDataViewModel = detailsMetadataViewModel
        saveDetailBinding.lifecycleOwner = this
        return saveDetailBinding.root
    }
}