package com.flagos.cscounters.main.adapter.viewholder

import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.flagos.cscounters.R
import com.flagos.cscounters.databinding.ItemCounterBinding
import com.flagos.cscounters.main.model.CounterUiItem

private const val NO_TIMES = 0
private const val NO_RESOURCE = 0

class CounterViewHolder(
    private val binding: ItemCounterBinding,
    private val onIncrementCallback: ((String) -> Unit),
    private val onDecrementCallback: ((String) -> Unit),
    private val onCounterSelected: ((CounterUiItem) -> Unit),
    private val onCounterRemoved: ((CounterUiItem) -> Unit)
) : RecyclerView.ViewHolder(binding.root) {

    private lateinit var counterItem: CounterUiItem

    init {
        with(binding) {
            imageCounterLess.setOnClickListener { if (counterItem.count > NO_TIMES) onDecrementCallback.invoke(counterItem.id) }
            imageCounterMore.setOnClickListener { onIncrementCallback.invoke(counterItem.id) }
            root.setOnLongClickListener {
                if (!counterItem.isSelected) {
                    counterItem.isSelected = true
                    onCounterSelected.invoke(counterItem)

                    root.setBackgroundResource(R.drawable.background_item_selected)
                    imageCounterSelected.visibility = VISIBLE
                    groupCounterOptions.visibility = GONE
                } else {
                    counterItem.isSelected = false
                    onCounterRemoved.invoke(counterItem)

                    root.setBackgroundResource(NO_RESOURCE)
                    imageCounterSelected.visibility = GONE
                    groupCounterOptions.visibility = VISIBLE
                }
                true
            }
        }
    }

    fun bind(item: CounterUiItem) {
        counterItem = item
        with(binding) {
            textCounterTitle.text = item.title
            textCounter.text = item.count.toString()
            if (item.count > NO_TIMES) {
                imageCounterLess.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_less))
                textCounter.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
            } else {
                imageCounterLess.setImageDrawable(ContextCompat.getDrawable(binding.root.context, R.drawable.ic_less_inactive))
                textCounter.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray))
            }
        }
    }
}
