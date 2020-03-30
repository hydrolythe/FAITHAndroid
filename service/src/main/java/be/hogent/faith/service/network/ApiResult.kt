/**
 * A generic class that holds a value with its loading status.
 */
sealed class ApiResult<out T : Any> {
    /**
     * property that indicates success + the data that is passed when successful
     * */
    data class Success<out T : Any>(val data: T) : ApiResult<T>()
    /**
     * property that indicates the error + the exception
     * */
    data class Error(val exception: Exception) : ApiResult<Nothing>()
}
