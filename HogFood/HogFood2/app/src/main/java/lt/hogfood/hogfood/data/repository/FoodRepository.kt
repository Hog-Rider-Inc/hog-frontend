package lt.hogfood.hogfood.data.repository

import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem

class FoodRepository {

    private val foodApi = RetrofitClient.foodApi
    private val recommendApi = RetrofitClient.recommendApi
    private val interactionApi = RetrofitClient.interactionApi

    suspend fun getAllDishes(): Result<List<FoodItem>> = runCatching {
        val response = foodApi.searchDishes()
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun getRecommendations(): Result<List<RecommendationItem>> = runCatching {
        val response = recommendApi.getRecommendations()
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun getInteractionItem(): Result<RecommendationItem?> = runCatching {
        val response = interactionApi.getInteractionItem()
        response.body()
    }

    suspend fun likeItem(itemId: Int): Result<Unit> = runCatching {
        interactionApi.likeItem(itemId = itemId)
    }

    suspend fun dislikeItem(itemId: Int): Result<Unit> = runCatching {
        interactionApi.dislikeItem(itemId = itemId)
    }
}