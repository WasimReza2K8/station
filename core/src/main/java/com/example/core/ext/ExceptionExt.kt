package com.example.core.ext

import retrofit2.HttpException
import java.io.IOException

fun Throwable.isNetworkException() =
    this is IOException || this is HttpException
