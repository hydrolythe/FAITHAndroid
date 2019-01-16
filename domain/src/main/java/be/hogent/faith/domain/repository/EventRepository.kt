package be.hogent.faith.domain.repository

import java.util.*


interface EventRepository<T> {
    fun clearAll()

    fun add(item: T)

    fun addAll(items: List<T>)

    fun find(uuid: UUID) : T?
}
