package be.hogent.faith.faith.treasureChest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import be.hogent.faith.R
import be.hogent.faith.databinding.DialogSaveTreasurechestBinding
import be.hogent.faith.domain.models.detail.Detail
import be.hogent.faith.faith.details.DetailsMetaDataViewModel
import be.hogent.faith.faith.details.TreasureChestDetailsMetaDataViewModel
import be.hogent.faith.faith.detailscontainer.SaveDetailsContainerDetailDialog
import org.koin.android.viewmodel.ext.android.getViewModel
import kotlin.reflect.KClass

class SaveTreasureChestDetailDialog(detailType: KClass<Detail>) :
    SaveDetailsContainerDetailDialog(detailType) {

    companion object {
        fun newInstance(detailType: KClass<Detail>): SaveTreasureChestDetailDialog {
            return SaveTreasureChestDetailDialog(detailType)
        }
    }

    override val detailsMetadataViewModel: DetailsMetaDataViewModel
        get() = getViewModel<TreasureChestDetailsMetaDataViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        saveDetailBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_save_treasurechest, container, false)
        (saveDetailBinding as DialogSaveTreasurechestBinding).detailsMetaDataViewModel =
            detailsMetadataViewModel
        saveDetailBinding.lifecycleOwner = this
        return saveDetailBinding.root
    }
}
