package utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <R> suspendToResourceWrapper(
    suspendFun: suspend () -> R
): Flow<Resource<R>> = flow {
    try {
        emit(Resource.Loading())
        val data: R = suspendFun()
        emit(Resource.Success(data))
    } catch (e: Exception) {
        emit(Resource.Error(e.message ?: e.toString()))
    }
}

