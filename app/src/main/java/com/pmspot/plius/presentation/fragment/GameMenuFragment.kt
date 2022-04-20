package com.pmspot.plius.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pmspot.plius.R
import com.pmspot.plius.databinding.FragmentGameMenuBinding


class GameMenuFragment : Fragment() {
    lateinit var binding: FragmentGameMenuBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameMenuBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireActivity()).load(R.drawable.bax).into(binding.imLogo)

        binding.btStartGame.setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_gameFragment)
        }
        binding.btResult.setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_gameResultFragment)
        }
        binding.btSettings.setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_gameSettingFragment)
        }
        binding.btDescription.setOnClickListener {
            findNavController().navigate(R.id.action_gameMenuFragment_to_gameDescriptionsFragment)
        }
    }
}