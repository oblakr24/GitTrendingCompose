package com.rokoblak.gittrendingcompose.data.repo.model


sealed interface LoadResult<out T> {
    data class Success<T>(val value: T): LoadResult<T>
    data class Error(val type: LoadErrorType2): LoadResult<Nothing>

    companion object {

        fun <T, K, R>compose(first: LoadResult<T>, second: LoadResult<K>, onSuccess: (T, K) -> R): LoadResult<R> {
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

sealed interface LoadErrorType2 {
    object NoNetwork: LoadErrorType2
    data class ApiError(val message: String): LoadErrorType2
}