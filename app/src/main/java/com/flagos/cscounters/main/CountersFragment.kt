package com.flagos.cscounters.main

import android.content.Intent
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
import com.flagos.common.extensions.getViewModel
import com.flagos.common.extensions.hideKeyboard
import com.flagos.cscounters.R
import com.flagos.cscounters.databinding.FragmentCountersBinding
import com.flagos.cscounters.helpers.NetworkHelper
import com.flagos.cscounters.main.CountersFragmentDirections.Companion.actionCountersDestToCreateItemDest
import com.flagos.cscounters.main.adapter.CountersAdapter
import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.cscounters.main.CountersViewModel.CountersState
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnSuccess
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnGenericError
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnUpdateError
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnLoading
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnNoContent
import com.flagos.cscounters.main.CountersViewModel.CountersState.OnDeleteError
import com.flagos.data.api.ApiHelper
import com.flagos.data.api.RetrofitBuilder
import com.flagos.data.repository.CountersRepository

private const val BLANK = ""
private const val SHARE_IME_TYPE = "text/plain"

class CountersFragment : Fragment() {

    private val networkHelper by lazy { NetworkHelper(requireContext()) }
    private val apiHelper by lazy { ApiHelper(RetrofitBuilder.countersApi) }
    private val countersRepository by lazy { CountersRepository(apiHelper) }
    private val viewModel by lazy { getViewModel { CountersViewModel(countersRepository, networkHelper) } }

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
            onSelectedItemsAction = { list, actionType -> viewModel.performSelectionAction(list, actionType) }
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
            onShareCounters.observe(viewLifecycleOwner) { text -> shareCounters(text) }
        }
    }

    private fun setCountersInfoTexts(countersInfo: Pair<Int, Int>) {
        with(binding) {
            textCounterItems.text = getString(R.string.text_counters_count, countersInfo.first.toString())
            textCounterTimes.text = getString(R.string.text_counters_times, countersInfo.second.toString())
        }
    }

    private fun onUiStateChanged(state: CountersState) {
        when (state) {
            is OnLoading -> showLoader()
            is OnNoContent -> onNoContent()
            is OnUpdateError -> showCantUpdateCounterDialog(state.counterInfo)
            is OnGenericError -> onError(state.counterId, state.actionType, state.message)
            is OnSuccess -> onSuccess(state.countersList)
            is OnDeleteError -> showNoInternetDialog()
        }
    }

    private fun shareCounters(text: String) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = SHARE_IME_TYPE
        shareIntent.putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, getString(R.string.text_menu_share)))
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

    private fun showError(counterId: String?, actionType: CounterActionType, errorMessage: String? = null) {
        with(binding.layoutError) {
            textErrorTitle.text = getString(R.string.text_error_cant_load_title)
            textErrorDesc.text = if (!errorMessage.isNullOrEmpty()) errorMessage else getString(R.string.text_error_no_internet_desc)
            buttonErrorRetry.setOnClickListener { viewModel.performRetry(counterId, actionType) }
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

    private fun showCantUpdateCounterDialog(counterInfo: Pair<CounterUiItem, CounterActionType>) {
        val (counterItem, actionType) = counterInfo
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.text_error_cant_update_dialog_title, counterItem.title, counterItem.count.toString()))
            setMessage(getString(R.string.text_error_no_internet_dialog_desc))
            setPositiveButton(getString(R.string.text_error_dismiss)) { _, _ -> }
            setNegativeButton(getString(R.string.text_error_retry)) { _, _ -> viewModel.performRetry(counterItem.id, actionType) }
            show()
        }
    }

    private fun showNoInternetDialog() {
        val builder = AlertDialog.Builder(requireContext())
        with(builder) {
            setTitle(getString(R.string.text_error_cant_delete_counter_title))
            setMessage(getString(R.string.text_error_no_internet_dialog_desc))
            setPositiveButton(getString(R.string.text_error_dismiss)) { _, _ -> }
            show()
        }
    }

    private fun onSuccess(items: List<CounterUiItem>?) {
        actionMode?.finish()
        hideEmptyState()
        hideError()
        hideLoader()
        countersAdapter.setData(items)
    }

    private fun onError(counterId: String?, actionType: CounterActionType, errorMessage: String?) {
        actionMode?.finish()
        hideEmptyState()
        showError(counterId, actionType, errorMessage)
        hideLoader()
    }

    private fun onNoContent() {
        actionMode?.finish()
        hideError()
        showEmptyState()
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
