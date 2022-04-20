package com.pmspot.plius.presentation

import androidx.lifecycle.ViewModel

class GameViewModel: ViewModel() {
    var hardStatus = false
    var count = 0
    var countList = arrayListOf<String>()
    var betList = arrayListOf<String>()
    var resultList = arrayListOf<String>()
    var balans = 0
    var sumBet = 0
    var result = 0
}