package be.hogent.faith.faith.detailscontainer.detailFilters

import be.hogent.faith.faith.models.detail.Detail

interface DetailFilter : (Detail) -> Boolean
