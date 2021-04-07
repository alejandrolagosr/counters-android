package com.flagos.cscounters.examples.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flagos.common.extensions.inflater
import com.flagos.cscounters.databinding.ItemExamplesBinding
import com.flagos.cscounters.examples.adapter.viewholder.ExamplesViewHolder
import com.flagos.cscounters.examples.model.CategoryUiItem

class ExamplesAdapter(private val onExampleCallback: ((String) -> Unit)) :
    ListAdapter<CategoryUiItem, RecyclerView.ViewHolder>(ExampleItemsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.inflater
        return ExamplesViewHolder(ItemExamplesBinding.inflate(inflater, parent, false), onExampleCallback)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = (holder as ExamplesViewHolder).bind(currentList[position])

    override fun getItemCount(): Int = currentList.size

    class ExampleItemsDiff : DiffUtil.ItemCallback<CategoryUiItem>() {
        override fun areItemsTheSame(oldItem: CategoryUiItem, newItem: CategoryUiItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CategoryUiItem, newItem: CategoryUiItem): Boolean = oldItem == newItem
    }
}
