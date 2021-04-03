package com.flagos.cscounters.examples

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flagos.cscounters.examples.mapper.CategoryItemMapper
import com.flagos.cscounters.examples.model.ExampleItems

class ExamplesViewModel(private val categoryItemMapper: CategoryItemMapper) : ViewModel() {

    private val _onCategoriesRetrieved = MutableLiveData<List<ExampleItems.CategoryItem>>()
    val onCategoriesRetrieved: LiveData<List<ExampleItems.CategoryItem>>
        get() = _onCategoriesRetrieved

    fun fetchCategories() {
        _onCategoriesRetrieved.value = categoryItemMapper.toCategoryItemsList()
    }
}
