package com.flagos.cscounters.main.adapter

import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flagos.common.extensions.inflater
import com.flagos.cscounters.R
import com.flagos.cscounters.databinding.ItemCounterBinding
import com.flagos.cscounters.main.CounterSelectionActionType
import com.flagos.cscounters.main.CounterSelectionActionType.SHARE
import com.flagos.cscounters.main.CounterSelectionActionType.DELETE
import com.flagos.cscounters.main.adapter.viewholder.CounterViewHolder
import com.flagos.cscounters.main.model.CounterUiItem

private const val NO_COUNTERS = 0

class CountersAdapter(
    private val onIncrementCallback: ((String) -> Unit),
    private val onDecrementCallback: ((String) -> Unit),
    private val onStartContextMenu: (() -> Unit),
    private val onFinishContextMenu: (() -> Unit),
    private val onSelectedItemsCountChanged: ((Int) -> Unit),
    private val onSelectedItemsAction: ((List<CounterUiItem>, CounterSelectionActionType) -> Unit)
) : ListAdapter<CounterUiItem, RecyclerView.ViewHolder>(CounterItemsDiff()), Filterable {

    private var filterCountersList = mutableListOf<CounterUiItem>()
    private var selectedCountersList = mutableListOf<CounterUiItem>()
    private var isContextMenuStarted = false

    private val customFilter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchText = constraint.toString()
            val filteredList = mutableListOf<CounterUiItem>()
            if (constraint.isNullOrEmpty()) {
                filteredList.addAll(filterCountersList)
            } else {
                filterCountersList.forEach { counter ->
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

    val actionModeCallback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            val inflater: MenuInflater = mode.menuInflater
            inflater.inflate(R.menu.menu_main_counters, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = true

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.share_item -> {
                    onSelectedItemsAction.invoke(selectedCountersList, SHARE)
                }
                R.id.delete_item -> {
                    onSelectedItemsAction.invoke(selectedCountersList, DELETE)
                }
            }
            return true
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            selectedCountersList.clear()
            onFinishContextMenu.invoke()
            isContextMenuStarted = false
        }
    }

    override fun getFilter(): Filter = customFilter

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = parent.inflater
        return CounterViewHolder(
            ItemCounterBinding.inflate(inflater, parent, false),
            onIncrementCallback,
            onDecrementCallback,
            onCounterSelected = { counterAddedToSelection(it) },
            onCounterRemoved = { counterRemovedFromSelection(it) }
        )
    }

    private fun counterAddedToSelection(counterItem: CounterUiItem) {
        selectedCountersList.add(counterItem)
        if (!isContextMenuStarted) {
            onStartContextMenu.invoke()
            isContextMenuStarted = true
        }
        onSelectedItemsCountChanged.invoke(getSelectedItemCount())
    }

    private fun counterRemovedFromSelection(counterItem: CounterUiItem) {
        selectedCountersList.remove(counterItem)
        val itemsCount = getSelectedItemCount()
        if (itemsCount == NO_COUNTERS) {
            onFinishContextMenu.invoke()
            isContextMenuStarted = false
        } else {
            onSelectedItemsCountChanged.invoke(itemsCount)
        }
    }

    private fun getSelectedItemCount(): Int = selectedCountersList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as CounterViewHolder).bind(currentList[position])
    }

    override fun getItemCount(): Int = currentList.size

    fun setData(countersList: List<CounterUiItem>?) {
        countersList?.let {
            filterCountersList = it.toMutableList()
            submitList(countersList)
        }
    }

    class CounterItemsDiff : DiffUtil.ItemCallback<CounterUiItem>() {
        override fun areItemsTheSame(oldItem: CounterUiItem, newItem: CounterUiItem): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CounterUiItem, newItem: CounterUiItem): Boolean = oldItem == newItem
    }
}
