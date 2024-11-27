package com.inmaeo.tictactwo.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.inmaeo.tictactwo.R
import com.inmaeo.tictactwo.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment()  {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            newGameButton.setOnClickListener {
                val action = OnboardingFragmentDirections.actionOnboardingFragmentToGameFragment(null)
                findNavController().navigate(action)
            }
            savedGamesButton.setOnClickListener {
                findNavController().navigate(R.id.action_onboardingFragment_to_savedGamesFragment)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
