package be.hogent.faith.faith.util.state

class Resource<out T> constructor(
    val status: ResourceState,
    val data: T?,
    val message: Int?
) {

    fun <T> success(data: T): Resource<T> {
        return Resource(ResourceState.SUCCESS, data, null)
    }

    fun <T> error(message: Int?): Resource<T> {
        return Resource(ResourceState.ERROR, null, message)
    }

    fun <T> loading(): Resource<T> {
        return Resource(ResourceState.LOADING, null, null)
    }
}