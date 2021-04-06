package com.flagos.cscounters.main.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.flagos.cscounters.databinding.ItemCounterBinding
import com.flagos.cscounters.main.model.CounterUiItem

class CounterViewHolder(
    private val binding: ItemCounterBinding,
    private val onIncrementCallback: ((String) -> Unit),
    private val onDecrementCallback: ((String) -> Unit)
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var counterItem: CounterUiItem

    init {
        with(binding) {
            imageCounterLess.setOnClickListener { onDecrementCallback.invoke(counterItem.id.orEmpty()) }
            imageCounterMore.setOnClickListener { onIncrementCallback.invoke(counterItem.id.orEmpty()) }
        }
    }

    fun bind(item: CounterUiItem) {
        counterItem = item
        with(binding) {
            textCounterTitle.text = item.title
            textCounter.text = item.count.toString()
        }
    }
}
