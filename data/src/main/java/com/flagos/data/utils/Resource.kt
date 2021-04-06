package com.flagos.data.utils

import com.flagos.data.utils.Status.SUCCESS
import com.flagos.data.utils.Status.ERROR
import com.flagos.data.utils.Status.LOADING

data class Resource<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T): Resource<T> = Resource(SUCCESS, data, null)
        fun <T> error(data: T?, message: String): Resource<T> = Resource(ERROR, data, message)
        fun <T> loading(data: T?): Resource<T> = Resource(LOADING, data, null)
    }
}
