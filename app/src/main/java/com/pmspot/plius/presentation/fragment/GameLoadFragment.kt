package com.pmspot.plius.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pmspot.plius.R
import com.pmspot.plius.databinding.FragmentGameLoadBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameLoadFragment : Fragment() {
    lateinit var binding: FragmentGameLoadBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameLoadBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(requireActivity()).load(R.drawable.bask).into(binding.imBask)
        lifecycleScope.launch {
            delay(3000)
            findNavController().navigate(R.id.action_gameLoadFragment_to_gameMenuFragment)
        }
    }

}