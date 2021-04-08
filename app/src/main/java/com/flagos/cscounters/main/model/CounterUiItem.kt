package com.flagos.cscounters.main.model

data class CounterUiItem(
    val id: String,
    val title: String,
    val count: Int,
    var isSelected: Boolean = false
)
