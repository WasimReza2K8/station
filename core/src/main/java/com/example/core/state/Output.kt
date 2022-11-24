package com.example.core.state

sealed interface Output<out T> {
    data class Success<T>(val result: T) : Output<T>
    object UnknownError : Output<Nothing>
    object NetworkError : Output<Nothing>
}
