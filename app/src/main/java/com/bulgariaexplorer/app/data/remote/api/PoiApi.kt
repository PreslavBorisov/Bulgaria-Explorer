package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.PoiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PoiApi {

    @GET("api/pois")
    suspend fun getAllPois(): Response<List<PoiResponse>>

    @GET("api/pois/{id}")
    suspend fun getPoiById(
        @Path("id") id: Long
    ): Response<PoiResponse>

    @GET("api/pois/search")
    suspend fun searchPois(
        @Query("query") query: String
    ): Response<List<PoiResponse>>
}
