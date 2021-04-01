package be.hogent.faith.faith.treasureChest

import android.app.Application
import android.content.Context
import be.hogent.faith.faith.models.TreasureChest
import be.hogent.faith.faith.detailscontainer.DetailsContainerViewModel

class TreasureChestViewModel(
    treasureChest: TreasureChest,
    treasureChestRepository: TreasureChestRepository,
    context:Context
) : DetailsContainerViewModel<TreasureChest>(
    treasureChest,
    treasureChestRepository,
    context
)
