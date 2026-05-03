package com.bulgariaexplorer.app.data.remote.api

import com.bulgariaexplorer.app.data.remote.dto.FavoriteResponse
import retrofit2.Response
import retrofit2.http.*

interface FavoriteApi {

    @GET("api/favorites/me")
    suspend fun getFavorites(): Response<List<FavoriteResponse>>

    @POST("api/favorites/{poiId}")
    suspend fun addFavorite(
        @Path("poiId") poiId: Long
    ): Response<FavoriteResponse>

    @DELETE("api/favorites/{poiId}")
    suspend fun removeFavorite(
        @Path("poiId") poiId: Long
    ): Response<Void>
}
