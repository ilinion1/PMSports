package com.pmspot.plius.data

import com.google.gson.annotations.SerializedName

data class Container(@SerializedName("users" ) var users : ArrayList<Users> = arrayListOf())
