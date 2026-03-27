package lt.hogfood.hogfood.data.api

import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @GET("dishes/search")
    suspend fun searchDishes(
        @Query("q") query: String = "",
        @Query("categoryId") categoryId: Int? = null,
        @Query("dietaryTagId") dietaryTagId: Int? = null
    ): Response<List<FoodItem>>
}

interface RecommendApi {
    @GET("api/users/{userId}/recommendations")
    suspend fun getRecommendations(
        @Path("userId") userId: Int = 1,
        @Header("Authorization") token: String = "Bearer 43fd60ac24a0111cb317362da10645abaa2980670a28768f366dfdd5cb76f0ff"
    ): Response<List<RecommendationItem>>
}