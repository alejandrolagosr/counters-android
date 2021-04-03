package com.flagos.cscounters.examples.mapper

import com.flagos.cscounters.examples.model.ExampleItems

class CategoryItemMapper {

    fun toCategoryItemsList(): List<ExampleItems.CategoryItem> = mutableListOf(
        ExampleItems.CategoryItem("Drinks", listOf("Cups of coffee", "Glasses of water", "Some more")),
        ExampleItems.CategoryItem("Food", listOf("Hot-dogs", "Cupcakes eaten", "Chicken sandwich")),
        ExampleItems.CategoryItem("Misc", listOf("Times sneezed", "Naps", "Day dreaming"))
    )
}
