package com.ejemplo.biblioteca.data.network

import com.ejemplo.biblioteca.data.model.Libro
import retrofit2.Response
import retrofit2.http.*

interface LibroApiService {
    @GET("libros")
    suspend fun obtenerLibros(): Response<List<Libro>>

    @POST("libros")
    suspend fun crearLibro(@Body libro: Libro): Response<Libro>

    @PUT("libros/{id}")
    suspend fun actualizarLibro(
        @Path("id") id: Int,
        @Body libro: Libro
    ): Response<Libro>

    @DELETE("libros/{id}")
    suspend fun eliminarLibro(@Path("id") id: Int): Response<Unit>
}