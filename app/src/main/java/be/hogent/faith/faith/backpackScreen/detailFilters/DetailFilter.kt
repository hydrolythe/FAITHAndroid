package be.hogent.faith.faith.backpackScreen.detailFilters

import be.hogent.faith.domain.models.detail.Detail


interface DetailFilter : (Detail) -> Boolean
