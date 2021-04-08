package com.flagos.cscounters.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ActionMode
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.flagos.common.extensions.hideKeyboard
import com.flagos.cscounters.R
import com.flagos.cscounters.databinding.FragmentCountersBinding
import com.flagos.cscounters.helpers.NetworkHelper
import com.flagos.cscounters.main.CountersFragmentDirections.Companion.actionCountersDestToCreateItemDest
import com.flagos.cscounters.main.adapter.CountersAdapter
import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.data.api.ApiHelper
import com.flagos.data.api.RetrofitBuilder
import com.flagos.data.repository.CountersRepository

private const val BLANK = ""

class CountersFragment : Fragment() {

    private val networkHelper by lazy { NetworkHelper(requireContext()) }
    private val apiHelper by lazy { ApiHelper(RetrofitBuilder.countersApi) }
    private val countersRepository by lazy { CountersRepository(apiHelper) }
    private val viewModel by lazy { CountersViewModel(countersRepository, networkHelper) }

    private lateinit var countersAdapter: CountersAdapter
    private lateinit var navController: NavController

    private var actionMode: ActionMode? = null

    private var _binding: FragmentCountersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCountersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        initViews()
        initObservers()
    }

    private fun initViews() {
        countersAdapter = CountersAdapter(
            onIncrementCallback = { id ->
                resetSearchBox()
                viewModel.incrementCounter(id)
            },
            onDecrementCallback = { id ->
                resetSearchBox()
                viewModel.decrementCounter(id)
            },
            onStartContextMenu = {
                actionMode = requireActivity().startActionMode(countersAdapter.actionModeCallback)
                hideSearchBox()
            },
            onFinishContextMenu = {
                actionMode?.finish()
                showSearchBox()
            },
            onSelectedItemsCountChanged = { actionMode?.title = getString(R.string.text_counters_items_selected, it.toString()) },
            onSelectedItemsAction = { _, _ -> /*call view model*/ }
        )

        binding.buttonCounterAdd.setOnClickListener { goToCreateItemScreen() }
        binding.recycler.adapter = countersAdapter

        with(binding.layoutSearch.editTextCountersSearch) {
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun afterTextChanged(s: Editable?) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    countersAdapter.filter.filter(s.toString())
                }
            })
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    resetSearchBox()
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }
        }
    }

    private fun resetSearchBox() {
        binding.layoutSearch.editTextCountersSearch.setText(BLANK)
        hideKeyboard()
    }

    private fun hideSearchBox() {
        resetSearchBox()
        binding.layoutSearch.editTextCountersSearch.visibility = GONE
    }

    private fun showSearchBox() {
        binding.layoutSearch.editTextCountersSearch.visibility = VISIBLE
    }

    private fun initObservers() {
        with(viewModel) {
            fetchCounters()
            onCounterInfoRetrieved.observe(viewLifecycleOwner) { countersInfo -> setCountersInfoTexts(countersInfo) }
            onCountersStateChanged.observe(viewLifecycleOwner) { state -> onUiStateChanged(state) }
        }
    }

    private fun setCountersInfoTexts(countersInfo: Pair<Int, Int>) {
        with(binding) {
            textCounterItems.text = getString(R.string.text_counters_count, countersInfo.first.toString())
            textCounterTimes.text = getString(R.string.text_counters_times, countersInfo.second.toString())
        }
    }


    private fun onUiStateChanged(state: CountersViewModel.CountersState) {
        when (state) {
            is CountersViewModel.CountersState.OnLoading -> showLoader()
            is CountersViewModel.CountersState.OnNoContent -> onNoContent()
            is CountersViewModel.CountersState.OnNoInternet -> showNoInternetDialog(state.counterInfo)
            is CountersViewModel.CountersState.OnError -> onError(state.message)
            is CountersViewModel.CountersState.OnSuccess -> onSuccess(state.countersList)
        }
    }

    private fun showLoader() {
        binding.layoutLoader.root.visibility = VISIBLE
    }

    private fun hideLoader() {
        binding.layoutLoader.root.visibility = GONE
    }

    private fun showEmptyState() {
        with(binding.layoutError) {
            textErrorTitle.text = getString(R.string.text_error_no_counters_title)
            textErrorDesc.text = getString(R.string.text_error_no_counters_desc)
            root.visibility = VISIBLE
        }
    }

    private fun hideEmptyState() {
        with(binding.layoutError) {
            textErrorTitle.text = BLANK
            textErrorDesc.text = BLANK
            root.visibility = GONE
        }
    }

    private fun showError(errorMessage: String? = null) {
        with(binding.layoutError) {
            textErrorTitle.text = getString(R.string.text_error_no_internet_title)
            textErrorDesc.text = if (!errorMessage.isNullOrEmpty()) errorMessage else getString(R.string.text_error_no_internet_desc)
            buttonErrorRetry.setOnClickListener { viewModel.fetchCounters() }
            buttonErrorRetry.visibility = VISIBLE
            root.visibility = VISIBLE
        }
    }

    private fun hideError() {
        with(binding.layoutError) {
            textErrorTitle.text = BLANK
            textErrorDesc.text = BLANK
            buttonErrorRetry.setOnClickListener(null)
            buttonErrorRetry.visibility = GONE
            root.visibility = GONE
        }
    }

    private fun showNoInternetDialog(counterInfo: Pair<CounterUiItem, CounterActionType>) {
        val (counterItem, actionType) = counterInfo
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.text_error_no_internet_dialog_title, counterItem.title, counterItem.count.toString()))
            setMessage(getString(R.string.text_error_no_internet_dialog_desc))
            setPositiveButton(getString(R.string.text_error_dismiss)) { _, _ -> }
            setNegativeButton(getString(R.string.text_error_retry)) { _, _ -> viewModel.retryAction(counterItem.id, actionType) }
            show()
        }
    }

    private fun onSuccess(items: List<CounterUiItem>?) {
        hideEmptyState()
        hideError()
        hideLoader()
        countersAdapter.setData(items)
    }

    private fun onError(errorMessage: String?) {
        hideEmptyState()
        showError(errorMessage)
        hideLoader()
    }

    private fun onNoContent() {
        showEmptyState()
        hideError()
        hideLoader()
    }

    private fun goToCreateItemScreen() {
        val action = actionCountersDestToCreateItemDest()
        navController.navigate(action)
    }

    override fun onDestroy() {
        viewModel.clearInfo()
        _binding = null
        super.onDestroy()
    }
}
