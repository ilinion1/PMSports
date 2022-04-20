package com.pmspot.plius.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.pmspot.plius.R
import com.pmspot.plius.databinding.FragmentGameBinding
import com.pmspot.plius.presentation.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class GameFragment : Fragment() {
    lateinit var binding: FragmentGameBinding
    private val viewModel by lazy { ViewModelProvider(requireActivity())[GameViewModel::class.java] }
    private var firstImage = "noFocus"
    private var firstGame = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvBalans.text = viewModel.balans.toString()
        binding.btPay.setOnClickListener {
            binding.cardPay.visibility = View.VISIBLE
            if (binding.edPay.text.isNotEmpty()){
                viewModel.balans += binding.edPay.text.toString().toInt()
                binding.tvBalans.text = viewModel.balans.toString()
                binding.cardPay.visibility = View.GONE
                binding.edPay.setText("")
            }
        }
        binding.btBack.setOnClickListener {
            findNavController().navigate(R.id.action_gameFragment_to_gameMenuFragment)
        }
        checkBet()
        focusImage()
    }


    private fun checkBet() {
        binding.btBet.setOnClickListener {
            if (binding.edBet.text.isNotEmpty()){
                viewModel.sumBet = binding.edBet.text.toString().toInt()
                binding.edBet.setText("")
            }
            if (viewModel.sumBet > viewModel.balans){
                lifecycleScope.launch {
                    binding.tvnoMany.text = "Not enough money!"
                    binding.cardNoMany.visibility = View.VISIBLE
                    delay(3000)
                    binding.cardNoMany.visibility = View.GONE
                }
            } else {
                if (firstImage == "noFocus"){
                    lifecycleScope.launch {
                        binding.tvnoMany.text = "Make a team choice!!"
                        binding.cardNoMany.visibility = View.VISIBLE
                        delay(3000)
                        binding.cardNoMany.visibility = View.GONE
                    }
                } else{
                    firstGame = !firstGame
                    startBet()
                    viewModel.count++
                    viewModel.countList.add(viewModel.count.toString())
                    viewModel.betList.add(viewModel.sumBet.toString())
                    viewModel.resultList.add(viewModel.result.toString())
                    viewModel.result = 0
                }
            }
        }
    }

    private fun focusImage(){
        binding.im1.setOnClickListener {
            it.setPadding(20)
            binding.im2.setPadding(0)
            firstImage = "im1"
        }
        binding.im2.setOnClickListener {
            it.setPadding(20)
            binding.im1.setPadding(0)
            firstImage = "im2"
        }
    }

    private fun startBet(){
        val random = (1..2).random()
        if (firstImage == "im1" && random == 1) {
            lifecycleScope.launch {
                if (!viewModel.hardStatus){
                    viewModel.balans += viewModel.sumBet
                    viewModel.result = viewModel.sumBet
                    binding.tvBalans.text = viewModel.balans.toString()
                    binding.tvnoMany.text = "Victory!"
                    binding.cardNoMany.visibility = View.VISIBLE
                    delay(3000)
                    binding.cardNoMany.visibility = View.GONE
                }else {
                    viewModel.balans += viewModel.sumBet * 2
                    viewModel.result = viewModel.sumBet * 2
                    binding.tvBalans.text = viewModel.balans.toString()
                    binding.tvnoMany.text = "Victory!"
                    binding.cardNoMany.visibility = View.VISIBLE
                    delay(3000)
                    binding.cardNoMany.visibility = View.GONE
                }
            }
        }else {
            if (!viewModel.hardStatus){
                lifecycleScope.launch {
                    viewModel.balans -= viewModel.sumBet
                    viewModel.result -= viewModel.sumBet
                    binding.tvBalans.text = viewModel.balans.toString()
                    binding.tvnoMany.text = "Losing!"
                    binding.cardNoMany.visibility = View.VISIBLE
                    delay(3000)
                    binding.cardNoMany.visibility = View.GONE
                }
            } else {
                lifecycleScope.launch {
                    viewModel.balans -= viewModel.sumBet * 2
                    viewModel.result -= viewModel.sumBet * 2
                    binding.tvBalans.text = viewModel.balans.toString()
                    binding.tvnoMany.text = "Losing!"
                    binding.cardNoMany.visibility = View.VISIBLE
                    delay(3000)
                    binding.cardNoMany.visibility = View.GONE
                }
            }
        }
        binding.im1.setPadding(0)
        binding.im2.setPadding(0)
        nextImage()
    }

    private fun nextImage(){
        if (firstGame){
            binding.im1.setImageResource(R.drawable.chelsea)
            binding.im2.setImageResource(R.drawable.manchester)
        } else {
            binding.im1.setImageResource(R.drawable.boston)
            binding.im2.setImageResource(R.drawable.toronto)
        }
    }
}