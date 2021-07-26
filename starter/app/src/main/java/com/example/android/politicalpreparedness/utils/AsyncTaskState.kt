package com.example.android.politicalpreparedness.utils

sealed class AsyncTaskState<out S : Any, out E : Any> {
    object InitialState : AsyncTaskState<Nothing, Nothing>()

    object LoadingState : AsyncTaskState<Nothing, Nothing>()

    data class ErrorState<out E : Any>(val error: E) : AsyncTaskState<Nothing, E>()

    data class SuccessState<out S : Any>(val data: S) : AsyncTaskState<S, Nothing>()
}