package be.hogent.faith.domain.models

import java.util.UUID

class Backpack(
    val uuid: UUID = UUID.randomUUID()
) : DetailsContainer()