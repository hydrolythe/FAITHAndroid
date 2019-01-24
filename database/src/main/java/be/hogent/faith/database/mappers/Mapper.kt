package be.hogent.faith.database.mappers

/**
 * Interface for mappers that convert between [Entity]s and [DomainObject]s
 */
interface Mapper<Entity, DomainObject> {
    fun mapFromEntity(entity: Entity): DomainObject
    fun mapToEntity(model: DomainObject): Entity
    fun mapFromEntities(entities: List<Entity>): List<DomainObject>
    fun mapToEntities(models: List<DomainObject>): List<Entity>
}
