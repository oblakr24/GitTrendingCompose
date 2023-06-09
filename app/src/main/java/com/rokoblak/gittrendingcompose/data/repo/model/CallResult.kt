package com.rokoblak.gittrendingcompose.data.repo.model


sealed interface CallResult<out T> {
    data class Success<T>(val value: T): CallResult<T>
    data class Error(val type: LoadErrorType): CallResult<Nothing>

    suspend fun <R>map(mapper: suspend (T) -> R): CallResult<R> = when (this) {
        is Error -> this
        is Success -> Success(mapper(value))
    }

    companion object {

        fun <T, K, R>compose(first: CallResult<T>, second: CallResult<K>, onSuccess: (T, K) -> R): CallResult<R> {
            val firstValue = when (first) {
                is Error -> return first
                is Success -> first.value
            }
            val secondValue = when (second) {
                is Error -> return second
                is Success -> second.value
            }
            return Success(onSuccess(firstValue, secondValue))
        }
    }
}

sealed interface LoadErrorType {
    object NoNetwork: LoadErrorType
    data class ApiError(val message: String): LoadErrorType
}
