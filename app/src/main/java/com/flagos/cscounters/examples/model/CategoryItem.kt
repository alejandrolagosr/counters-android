package com.flagos.cscounters.examples.model

sealed class ExampleItems(val id: Int) {

    data class CategoryItem(val categoryName: String, val examples: List<String>)
}
