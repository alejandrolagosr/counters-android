package com.flagos.cscounters.main.mapper

import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.data.model.CounterItem

private const val ZERO = 0

class CounterItemUiMapper {

    fun toCounterUiItemList(items: List<CounterItem>): List<CounterUiItem> = mutableListOf<CounterUiItem>().apply {
        items.forEach {
            add(CounterUiItem(it.id.orEmpty(), it.title.orEmpty(), it.count ?: ZERO))
        }
    }
}
