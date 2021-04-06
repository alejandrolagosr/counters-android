package com.flagos.cscounters.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.flagos.cscounters.R
import com.flagos.cscounters.databinding.FragmentCountersBinding
import com.flagos.cscounters.main.CountersFragmentDirections.Companion.actionCountersDestToCreateItemDest
import com.flagos.cscounters.main.adapter.CountersAdapter
import com.flagos.cscounters.main.model.CounterUiItem
import com.flagos.data.api.ApiHelper
import com.flagos.data.api.RetrofitBuilder
import com.flagos.data.repository.CountersRepository
import com.flagos.data.utils.Status

class CountersFragment : Fragment() {

    private val apiHelper by lazy { ApiHelper(RetrofitBuilder.countersApi) }
    private val countersRepository by lazy { CountersRepository(apiHelper) }
    private val viewModel by lazy { CountersViewModel(countersRepository) }

    private lateinit var countersAdapter: CountersAdapter

    private var _binding: FragmentCountersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCountersBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        countersAdapter = CountersAdapter(
            onIncrementCallback = {},
            onDecrementCallback = {}
        )

        with(binding) {
            buttonCounterAdd.setOnClickListener { goToCreateItemScreen() }
            recycler.adapter = countersAdapter
        }
    }

    private fun initObservers() {
        with(viewModel) {
            onCounterInfoRetrieved.observe(viewLifecycleOwner) { countersInfo -> setCountersInfoTexts(countersInfo) }
            onNoCountersAdded.observe(viewLifecycleOwner) { showEmptyState() }
            fetchCounters().observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> onSuccess(resource.data)
                    Status.ERROR -> onError(resource.message)
                    Status.LOADING -> showLoader()
                }
            }
        }
    }

    private fun setCountersInfoTexts(countersInfo: Pair<Int, Int>) {
        with(binding) {
            textCounterItems.text = getString(R.string.text_counters_count, countersInfo.first.toString())
            textCounterTimes.text = getString(R.string.text_counters_times, countersInfo.second.toString())
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

    private fun showError(errorMessage: String? = null) {
        with(binding.layoutError) {
            textErrorTitle.text = getString(R.string.text_error_no_internet_title)
            textErrorDesc.text = if (!errorMessage.isNullOrEmpty()) errorMessage else getString(R.string.text_error_no_internet_desc)
            buttonErrorRetry.setOnClickListener { viewModel.fetchCounters() }
            buttonErrorRetry.visibility = VISIBLE
            root.visibility = VISIBLE
        }
    }

    private fun hideEmptyState() {
        binding.layoutError.root.visibility = GONE
    }

    private fun onSuccess(items: List<CounterUiItem>?) {
        hideEmptyState()
        hideLoader()
        countersAdapter.submitList(items)
    }

    private fun onError(errorMessage: String?) {
        hideEmptyState()
        showError(errorMessage)
        hideLoader()
    }

    private fun goToCreateItemScreen() {
        val action = actionCountersDestToCreateItemDest()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        viewModel.clearInfo()
        _binding = null
        super.onDestroy()
    }
}
