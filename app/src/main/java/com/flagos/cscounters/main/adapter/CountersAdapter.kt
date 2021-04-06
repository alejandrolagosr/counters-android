package com.flagos.cscounters.main.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flagos.common.extensions.inflater
import com.flagos.cscounters.databinding.ItemCounterBinding
import com.flagos.cscounters.main.adapter.viewholder.CounterViewHolder
import com.flagos.cscounters.main.model.CounterUiItem

class CountersAdapter(
    private val onIncrementCallback: ((String) -> Unit),
    private val onDecrementCallback: ((String) -> Unit)
) : ListAdapter<CounterUiItem, RecyclerView.ViewHolder>(CounterItemsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.inflater
        return CounterViewHolder(ItemCounterBinding.inflate(inflater, parent, false), onIncrementCallback, onDecrementCallback)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)  {
        (holder as CounterViewHolder).bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    class CounterItemsDiff : DiffUtil.ItemCallback<CounterUiItem>() {
        override fun areItemsTheSame(oldItem: CounterUiItem, newItem: CounterUiItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CounterUiItem, newItem: CounterUiItem): Boolean = oldItem == newItem
    }
}
