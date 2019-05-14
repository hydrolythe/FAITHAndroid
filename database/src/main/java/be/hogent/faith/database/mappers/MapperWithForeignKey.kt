package be.hogent.faith.database.mappers

import java.util.UUID

/**
 * Interface for mappers that convert between [Entity]s with a FK and [DomainObject]s
 */
interface MapperWithForeignKey<Entity, DomainObject> {
    fun mapFromEntity(entity: Entity): DomainObject
    fun mapToEntity(model: DomainObject, foreignKey: UUID): Entity
    fun mapFromEntities(entities: List<Entity>): List<DomainObject>
    fun mapToEntities(models: List<DomainObject>, foreignKey: UUID): List<Entity>
}