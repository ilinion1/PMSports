package com.pmspot.plius.data

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("isdef" ) var isdef : String? = null,
    @SerializedName("Linka" ) var linka : String? = null)
