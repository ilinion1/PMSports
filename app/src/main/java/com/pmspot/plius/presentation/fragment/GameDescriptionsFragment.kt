package com.pmspot.plius.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.pmspot.plius.R
import com.pmspot.plius.databinding.FragmentGameDescriptionsBinding


class GameDescriptionsFragment : Fragment() {
  lateinit var binding: FragmentGameDescriptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameDescriptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.action_gameDescriptionsFragment_to_gameMenuFragment)
        }
    }

}