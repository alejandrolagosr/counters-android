package com.flagos.cscounters.examples.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flagos.common.extensions.inflater
import com.flagos.cscounters.databinding.ItemExamplesBinding
import com.flagos.cscounters.examples.adapter.viewholder.ExamplesViewHolder
import com.flagos.cscounters.examples.model.ExampleItems

class ExamplesAdapter(
    val items: List<ExampleItems.CategoryItem>,
    val onExampleCallback: ((String) -> Unit)
) : ListAdapter<ExampleItems, RecyclerView.ViewHolder>(ExampleItemsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.inflater
        return ExamplesViewHolder(ItemExamplesBinding.inflate(inflater, parent, false), onExampleCallback)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) = (holder as ExamplesViewHolder).bind(items[position])

    override fun getItemCount(): Int = items.size

    class ExampleItemsDiff : DiffUtil.ItemCallback<ExampleItems>() {
        override fun areItemsTheSame(oldItem: ExampleItems, newItem: ExampleItems): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ExampleItems, newItem: ExampleItems): Boolean = oldItem == newItem
    }
}
