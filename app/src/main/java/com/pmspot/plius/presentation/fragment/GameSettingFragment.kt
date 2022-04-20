package com.pmspot.plius.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pmspot.plius.R
import com.pmspot.plius.databinding.FragmentGameSettingBinding
import com.pmspot.plius.presentation.GameViewModel

class GameSettingFragment : Fragment() {
    lateinit var binding: FragmentGameSettingBinding
    private val viewModel by lazy { ViewModelProvider(requireActivity())[GameViewModel::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameSettingBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (viewModel.hardStatus){
            binding.rd2.isChecked = true
        }else {
            binding.rd1.isChecked = true
        }

        Glide.with(requireActivity()).load(R.drawable.money).into(binding.imMoney)
        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.action_gameSettingFragment_to_gameMenuFragment)
        }
        binding.rd1.setOnClickListener {
            viewModel.hardStatus = false
            binding.rd2.isChecked = false
        }
        binding.rd2.setOnClickListener {
            viewModel.hardStatus = true
            binding.rd1.isChecked = false
        }
    }

}