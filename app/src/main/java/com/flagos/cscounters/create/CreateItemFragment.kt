package com.flagos.cscounters.create

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.flagos.common.extensions.ToolbarType
import com.flagos.common.extensions.hideKeyboard
import com.flagos.common.extensions.setUpFragmentToolBar
import com.flagos.common.extensions.showKeyboard
import com.flagos.cscounters.R
import com.flagos.cscounters.create.CreateItemFragmentDirections.Companion.actionCreateItemFragmentDestToExamplesFragmentDest
import com.flagos.cscounters.databinding.FragmentCreateItemBinding

class CreateItemFragment : Fragment() {

    private lateinit var navController: NavController

    private var _binding: FragmentCreateItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreateItemBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        initToolbar()
        initViews()
    }

    private fun initToolbar() {
        setUpFragmentToolBar(binding.toolbar.toolbarSingleTitle, getString(R.string.text_create_title), ToolbarType.WITH_CLOSE_BUTTON)
        binding.toolbar.toolbarSingleTitle.setNavigationOnClickListener {
            hideKeyboard()
            navController.navigateUp()
        }
    }

    private fun initViews() {
        binding.textCreateItemSeeExamples.setOnClickListener {
            hideKeyboard()
            goToExamplesScreen()
        }

        binding.editTextCreateItem.requestFocus()
        showKeyboard()
    }

    private fun goToExamplesScreen() {
        val action = actionCreateItemFragmentDestToExamplesFragmentDest()
        navController.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_item -> {
                //viewModel.onSaveItem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        hideKeyboard()
        _binding = null
        super.onDestroy()
    }
}
