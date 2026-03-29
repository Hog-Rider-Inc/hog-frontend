package lt.hogfood.hogfood.data.api

import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.DishDetails
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @GET("dishes/search")
    suspend fun searchDishes(
        @Query("q") query: String = "",
        @Query("categoryId") categoryId: Int? = null,
        @Query("dietaryTagId") dietaryTagId: Int? = null
    ): Response<List<FoodItem>>

    @GET("dishes/{id}")
    suspend fun getDishById(
        @Path("id") id: Int
    ): Response<DishDetails>

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("dietary-tags")
    suspend fun getDietaryTags(): Response<List<DietaryTag>>
}

interface RecommendApi {
    @GET("api/users/{userId}/recommendations")
    suspend fun getRecommendations(
        @Path("userId") userId: Int = 1,
        @Header("Authorization") token: String = "Bearer ${BuildConfig.BEARER_TOKEN}"
    ): Response<List<RecommendationItem>>
}

interface InteractionApi {
    @GET("api/users/{userId}/recommendations/item_interactions")
    suspend fun getInteractionItem(
        @Path("userId") userId: Int = 1,
        @Header("Authorization") token: String = "Bearer ${BuildConfig.BEARER_TOKEN}"
    ): Response<RecommendationItem>

    @POST("api/users/{userId}/recommendations/item_interactions/{itemId}/like")
    suspend fun likeItem(
        @Path("userId") userId: Int = 1,
        @Path("itemId") itemId: Int,
        @Header("Authorization") token: String = "Bearer ${BuildConfig.BEARER_TOKEN}"
    ): Response<Unit>

    @POST("api/users/{userId}/recommendations/item_interactions/{itemId}/dislike")
    suspend fun dislikeItem(
        @Path("userId") userId: Int = 1,
        @Path("itemId") itemId: Int,
        @Header("Authorization") token: String = "Bearer ${BuildConfig.BEARER_TOKEN}"
    ): Response<Unit>
}