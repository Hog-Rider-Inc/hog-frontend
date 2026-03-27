package lt.hogfood.hogfood.data.repository

import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem

class FoodRepository {

    private val foodApi = RetrofitClient.foodApi
    private val recommendApi = RetrofitClient.recommendApi

    suspend fun getAllDishes(): Result<List<FoodItem>> = runCatching {
        val response = foodApi.searchDishes()
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun getRecommendations(): Result<List<RecommendationItem>> = runCatching {
        val response = recommendApi.getRecommendations()
        response.body() ?: error("Tuščias atsakymas")
    }
}