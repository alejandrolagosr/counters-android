package com.flagos.cscounters.examples

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.flagos.common.extensions.ToolbarType
import com.flagos.common.extensions.getViewModel
import com.flagos.common.extensions.saveBackStackEntryState
import com.flagos.common.extensions.setUpFragmentToolBar
import com.flagos.cscounters.R
import com.flagos.cscounters.databinding.FragmentExamplesBinding
import com.flagos.cscounters.examples.adapter.ExamplesAdapter
import com.flagos.cscounters.examples.mapper.CategoryItemMapper
import com.flagos.cscounters.examples.model.ExampleItems

class ExamplesFragment : Fragment() {

    private lateinit var navController: NavController
    private val viewModel by lazy { getViewModel { ExamplesViewModel(CategoryItemMapper()) } }

    private var _binding: FragmentExamplesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExamplesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        initToolbar()
        initObservers()
    }

    private fun initToolbar() {
        setUpFragmentToolBar(binding.toolbar.toolbarSingleTitle, getString(R.string.text_examples_title), ToolbarType.WITH_BACK_BUTTON)
        binding.toolbar.toolbarSingleTitle.setNavigationOnClickListener { goBack() }
    }

    private fun initObservers() {
        with(viewModel) {
            fetchCategories()
            onCategoriesRetrieved.observe(viewLifecycleOwner) { items -> initRecycler(items) }
        }
    }

    private fun initRecycler(items: List<ExampleItems.CategoryItem>) {
        binding.recycler.adapter = ExamplesAdapter(items) { example ->
            navController.saveBackStackEntryState(SELECTED_EXAMPLE_KEY, example)
            goBack()
        }
    }

    private fun goBack() = navController.navigateUp()

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    companion object {
        const val SELECTED_EXAMPLE_KEY = "SELECTED_EXAMPLE_KEY"
    }
}
