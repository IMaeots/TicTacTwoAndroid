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

    private lateinit var binding: FragmentOnboardingBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FragmentOnboardingBinding.inflate(layoutInflater, container, false).apply { binding = this }.root

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
}
