package com.rokoblak.gittrendingcompose.data.repo.model


sealed interface LoadableResult<out T> {
    data class Success<T>(val value: T): LoadableResult<T>
    data class Error(val type: LoadErrorType): LoadableResult<Nothing>
    object Loading: LoadableResult<Nothing>
}

fun <T>CallResult<T>.toLoadable(): LoadableResult<T> = when (this) {
    is CallResult.Error -> LoadableResult.Error(type)
    is CallResult.Success -> LoadableResult.Success(value)
}
