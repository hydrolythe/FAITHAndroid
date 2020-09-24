package be.hogent.faith.faith.detailscontainer.detailFilters

import be.hogent.faith.domain.models.detail.Detail

interface DetailFilter : (Detail) -> Boolean
