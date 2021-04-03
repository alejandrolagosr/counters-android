package com.flagos.cscounters.examples.adapter.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.flagos.cscounters.databinding.ItemExamplesBinding
import com.flagos.cscounters.examples.model.ExampleItems
import com.google.android.material.chip.Chip

class ExamplesViewHolder(
    private val binding: ItemExamplesBinding,
    private val onExampleCallback: ((String) -> Unit)
): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ExampleItems.CategoryItem) {
        with(binding) {
            textExampleCategory.text = item.categoryName
            item.examples.forEach {
                val chip = Chip(binding.root.context).apply {
                    text = it
                    setOnClickListener { onExampleCallback.invoke(text.toString()) }
                }
                chipGroupExamples.addView(chip)
            }
        }
    }
}
