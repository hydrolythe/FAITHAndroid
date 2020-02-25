package be.hogent.faith.faith.backpackScreen

import androidx.lifecycle.ViewModel
import be.hogent.faith.service.usecases.backpack.GetBackPackFilesDummyUseCase
import org.koin.core.KoinComponent

class BackpackScreenViewModel(
    private val getBackPackFilesDummyUseCase : GetBackPackFilesDummyUseCase
) : ViewModel(), KoinComponent {



}