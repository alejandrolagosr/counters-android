package com.flagos.cscounters.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.flagos.cscounters.databinding.FragmentWelcomeBinding
import com.flagos.cscounters.welcome.WelcomeFragmentDirections.Companion.actionWelcomeDestToCountersDest

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWelcomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonWelcomeGetStarted.setOnClickListener { goToMainScreen() }
    }

    private fun goToMainScreen() {
        val action = actionWelcomeDestToCountersDest()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
