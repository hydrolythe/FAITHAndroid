package be.hogent.faith.faith.treasureChest

import be.hogent.faith.domain.models.TreasureChest
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel
import be.hogent.faith.service.usecases.detailscontainer.DeleteDetailsContainerDetailUseCase
import be.hogent.faith.service.usecases.detailscontainer.GetDetailsContainerDataUseCase
import be.hogent.faith.service.usecases.detailscontainer.LoadDetailFileUseCase
import be.hogent.faith.service.usecases.detailscontainer.SaveDetailsContainerDetailUseCase

class TreasureChestViewModel(
    saveDetailUseCase: SaveDetailsContainerDetailUseCase<TreasureChest>,
    deleteDetailUseCase: DeleteDetailsContainerDetailUseCase<TreasureChest>,
    treasureChest: TreasureChest,
    loadDetailFileUseCase: LoadDetailFileUseCase<TreasureChest>,
    getDataUseCase: GetDetailsContainerDataUseCase<TreasureChest>
) : DetailsContainerViewModel<TreasureChest>(
    saveDetailUseCase,
    deleteDetailUseCase,
    loadDetailFileUseCase,
    getDataUseCase,
    treasureChest
)
