package com.example.core.resProvider

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getString(@StringRes resourceIdentifier: Int, vararg arguments: Any): String {
        return if (arguments.isNotEmpty()) {
            context.getString(resourceIdentifier, *arguments)
        } else {
            context.getString(resourceIdentifier)
        }
    }
}
