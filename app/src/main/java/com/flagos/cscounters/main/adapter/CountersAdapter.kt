package com.flagos.cscounters.main.adapter

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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
) : ListAdapter<CounterUiItem, RecyclerView.ViewHolder>(CounterItemsDiff()), Filterable {

    private var listForFilter = mutableListOf<CounterUiItem>()

    private val customFilter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchText = constraint.toString()
            val filteredList = mutableListOf<CounterUiItem>()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(listForFilter)
            } else {
                listForFilter.forEach { counter ->
                    if (counter.title.contains(searchText, true)) {
                        filteredList.add(counter)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            submitList(results?.values as MutableList<CounterUiItem>)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.inflater
        return CounterViewHolder(
            ItemCounterBinding.inflate(inflater, parent, false),
            onIncrementCallback,
            onDecrementCallback
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CounterViewHolder).bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    fun setData(countersList: List<CounterUiItem>?) {
        countersList?.let {
            listForFilter = it.toMutableList()
            submitList(countersList)
        }
    }

    override fun getFilter(): Filter = customFilter

    class CounterItemsDiff : DiffUtil.ItemCallback<CounterUiItem>() {
        override fun areItemsTheSame(oldItem: CounterUiItem, newItem: CounterUiItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CounterUiItem, newItem: CounterUiItem): Boolean = oldItem == newItem
    }
}
