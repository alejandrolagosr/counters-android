package com.flagos.cscounters.examples.mapper

import com.flagos.cscounters.examples.model.CategoryUiItem
import java.util.UUID

class CategoryItemUiMapper {

    fun toCategoryItemsList(): List<CategoryUiItem> = mutableListOf(
        CategoryUiItem(generateId(), "Drinks", listOf("Cups of coffee", "Glasses of water", "Some more")),
        CategoryUiItem(generateId(), "Food", listOf("Hot-dogs", "Cupcakes eaten", "Chicken sandwich")),
        CategoryUiItem(generateId(), "Misc", listOf("Times sneezed", "Naps", "Day dreaming"))
    )

    private fun generateId() = UUID.randomUUID().toString().substring(0, 4)
}
