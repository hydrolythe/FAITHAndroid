package be.hogent.faith.database.models.relations

import androidx.room.Embedded
import androidx.room.Relation
import be.hogent.faith.database.models.EventEntity
import be.hogent.faith.database.models.detail.AudioDetailEntity
import be.hogent.faith.database.models.detail.DetailEntity
import be.hogent.faith.database.models.detail.PictureDetailEntity
import be.hogent.faith.database.models.detail.TextDetailEntity

/**
 * Relation class for the [EventEntity] and it's relation with [DetailEntity]s.
 * Holds a separate list for each subtype of [DetailEntity]. The reasoning behind this can be found in the [DetailDao].
 * When requesting the detailEntities it combines these three lists into one.
 * When you need to set the detailEntities manually (when testing for example), use the [addDetailEntities] method.
 * This will classify the details in your provided list to the appropriate list.
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

    /**
     * The combined list of all [DetailEntity]s.
     * If you need to set this, use [addDetailEntities].
     */
    val detailEntities: List<DetailEntity>
        get() {
            return mutableListOf<DetailEntity>().apply {
                addAll(audioDetailEntities)
                addAll(textDetailEntities)
                addAll(pictureDetailEntities)
            }
        }

    /**
     * Adds a list of [DetailEntity]s.
     * Will place every given entity in the appropriate list.
     */
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