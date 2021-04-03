package com.flagos.cscounters.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.flagos.cscounters.databinding.FragmentMainBinding
import com.flagos.cscounters.main.MainFragmentDirections.Companion.actionMainFragmentDestToCreateItemFragmentDest

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.buttonMainAddCounter.setOnClickListener { goToCreateItemScreen() }
    }

    private fun goToCreateItemScreen() {
        val action = actionMainFragmentDestToCreateItemFragmentDest()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
