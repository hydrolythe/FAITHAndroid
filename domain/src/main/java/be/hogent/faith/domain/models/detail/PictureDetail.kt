package be.hogent.faith.domain.models.detail

import java.io.File
import java.util.UUID

class PictureDetail(
    file: File,
    name: String? = null,
    uuid: UUID = UUID.randomUUID()
) : Detail(file, name, uuid)