package com.ejemplo.biblioteca.data.model

import com.google.gson.annotations.SerializedName

data class Libro(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("titulo") val titulo: String? = "",
    @SerializedName("autor") val autor: String? = "",
    @SerializedName("isbn") val isbn: String? = "",
    @SerializedName("ejemplares") val ejemplares: Int? = 0,
    @SerializedName("genero") val genero: String? = "",
    @SerializedName("anioPublicacion") val anioPublicacion: Int? = 0
)