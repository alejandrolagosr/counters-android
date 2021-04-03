package com.flagos.common.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController

fun <T> NavController.saveBackStackEntryState(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.set(key, value)
}

fun <T> NavController.observeBackStackEntry(viewLifecycleOwner: LifecycleOwner, key: String, action: (T) -> Unit) {
    currentBackStackEntry?.savedStateHandle?.getLiveData<T>(key)?.observe(viewLifecycleOwner) {
        action(it)
        currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }
}
