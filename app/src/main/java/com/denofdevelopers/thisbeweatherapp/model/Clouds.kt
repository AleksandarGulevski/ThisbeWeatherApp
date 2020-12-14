package com.denofdevelopers.thisbeweatherapp.model

import com.google.gson.annotations.SerializedName

data class Clouds (
    @SerializedName("all")
    val all: Int
)