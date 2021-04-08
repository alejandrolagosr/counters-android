package com.flagos.cscounters.create

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.flagos.common.extensions.*
import com.flagos.cscounters.R
import com.flagos.cscounters.create.CreateCounterFragmentDirections.Companion.actionCreateCounterDestToExamplesDest
import com.flagos.cscounters.databinding.FragmentCreateItemBinding
import com.flagos.cscounters.examples.ExamplesFragment.Companion.SELECTED_EXAMPLE_KEY
import com.flagos.cscounters.helpers.NetworkHelper
import com.flagos.data.api.ApiHelper
import com.flagos.data.api.RetrofitBuilder
import com.flagos.data.repository.CountersRepository

class CreateCounterFragment : Fragment() {

    private lateinit var navController: NavController

    private val networkHelper by lazy { NetworkHelper(requireContext()) }
    private val apiHelper by lazy { ApiHelper(RetrofitBuilder.countersApi) }
    private val countersRepository by lazy { CountersRepository(apiHelper) }
    private val viewModel by lazy { getViewModel { CreateCounterViewModel(countersRepository, networkHelper) } }

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
        navController.observeBackStackEntry<String>(viewLifecycleOwner, SELECTED_EXAMPLE_KEY, { binding.editTextCreateItem.setText(it) })

        initToolbar()
        initViews()
        initObservers()
    }

    private fun initToolbar() {
        setUpFragmentToolBar(binding.toolbar.toolbarSingleTitle, getString(R.string.text_create_title), ToolbarType.WITH_CLOSE_BUTTON)
        binding.toolbar.toolbarSingleTitle.setNavigationOnClickListener {
            hideKeyboard()
            goBack()
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

    private fun initObservers() {
        with(viewModel) {
            onCounterSaved.observe(viewLifecycleOwner) { goBack() }
            onShowNoInternetDialog.observe(viewLifecycleOwner) { showNoInternetDialog() }
            onShowError.observe(viewLifecycleOwner) { message -> showErrorToast(message) }
        }
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.text_error_no_internet_dialog_generic_title))
            setMessage(getString(R.string.text_error_no_internet_dialog_generic_desc))
            setPositiveButton(getString(R.string.text_ok)) { _, _ -> }
            show()
        }
    }

    private fun showErrorToast(message: String) = Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

    private fun goBack() = navController.navigateUp()

    private fun goToExamplesScreen() {
        val action = actionCreateCounterDestToExamplesDest()
        navController.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_create_item, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_item -> {
                saveCounter()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveCounter() {
        val counterName = binding.editTextCreateItem.text.toString()
        viewModel.saveCounter(counterName)
    }

    override fun onDestroy() {
        hideKeyboard()
        _binding = null
        super.onDestroy()
    }
}
