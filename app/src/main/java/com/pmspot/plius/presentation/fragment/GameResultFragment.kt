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
import com.pmspot.plius.databinding.FragmentGameResultBinding
import com.pmspot.plius.presentation.GameAdapter
import com.pmspot.plius.presentation.GameViewModel


class GameResultFragment : Fragment() {
    lateinit var binding: FragmentGameResultBinding
    lateinit var adapter: GameAdapter
    private val viewModel by lazy { ViewModelProvider(requireActivity())[GameViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Glide.with(this).load(R.drawable.money2).into(binding.imLogo)
        adapter = GameAdapter()
        binding.recyclerView.adapter = adapter
        adapter.countList.clear()
        adapter.betList.clear()
        adapter.resultList.clear()
        adapter.countList.addAll(viewModel.countList)
        adapter.betList.addAll(viewModel.betList)
        adapter.resultList.addAll(viewModel.resultList)
        adapter.notifyDataSetChanged()

        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.action_gameResultFragment_to_gameMenuFragment)
        }
    }
}