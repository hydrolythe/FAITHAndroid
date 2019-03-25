package be.hogent.faith.database.models.relations

import androidx.room.Embedded
import androidx.room.Relation
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity

/**
 * Relation class for the [EventEntity] and it's relation with [DetailEntity]s
 */
class EventWithDetails {
    @Embedded
    lateinit var eventEntity: EventEntity

    @Relation(parentColumn = "uuid", entityColumn = "eventUuid", entity = TextDetailEntity::class)
    var textDetailEntities: MutableList<TextDetailEntity> = mutableListOf()

    @Relation(parentColumn = "uuid", entityColumn = "eventUuid", entity = AudioDetailEntity::class)
    var audioDetailEntities: MutableList<AudioDetailEntity> = mutableListOf()

    @Relation(parentColumn = "uuid", entityColumn = "eventUuid", entity = PictureDetailEntity::class)
    var pictureDetailEntities: MutableList<PictureDetailEntity> = mutableListOf()

    // Keep as a var so it can be set in tests.
    val detailEntities: List<DetailEntity>
        get() {
            return mutableListOf<DetailEntity>().apply {
                addAll(audioDetailEntities)
                addAll(textDetailEntities)
                addAll(pictureDetailEntities)
            }
        }

    fun addDetailEntities(entities: List<DetailEntity>) {
        entities.forEach {
            when (it) {
                is TextDetailEntity -> textDetailEntities.add(it)
                is AudioDetailEntity -> audioDetailEntities.add(it)
                is PictureDetailEntity -> pictureDetailEntities.add(it)
            }
        }
    }
}