package com.flagos.cscounters.examples

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.flagos.cscounters.examples.mapper.CategoryItemUiMapper
import com.flagos.cscounters.examples.model.CategoryUiItem

class ExamplesViewModel(private val categoryItemMapper: CategoryItemUiMapper) : ViewModel() {

    private val _onCategoriesRetrieved = MutableLiveData<List<CategoryUiItem>>()
    val onCategoriesRetrieved: LiveData<List<CategoryUiItem>>
        get() = _onCategoriesRetrieved

    fun fetchCategories() {
        _onCategoriesRetrieved.value = categoryItemMapper.toCategoryItemsList()
    }
}
