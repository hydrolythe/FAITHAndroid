package be.hogent.faith.database.mappers

/**
 * Interface for mappers that convert Entities of type E to Domain models of type D
 */
internal interface Mapper<E, D> {
    fun mapFromEntity(entity: E): D
    fun mapToEntity(model: D): E
}
