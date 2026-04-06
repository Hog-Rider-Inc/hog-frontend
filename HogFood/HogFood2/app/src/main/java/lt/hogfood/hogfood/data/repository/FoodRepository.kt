package lt.hogfood.hogfood.data.repository

import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.DishDetails
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.Order
import lt.hogfood.hogfood.data.model.RecommendationItem

class FoodRepository {

    private val foodApi = RetrofitClient.foodApi
    private val recommendApi = RetrofitClient.recommendApi
    private val interactionApi = RetrofitClient.interactionApi

    suspend fun getAllDishes(): Result<List<FoodItem>> = runCatching {
        val response = foodApi.searchDishes()
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun searchDishes(
        query: String = "",
        category: String? = null,
        dietary: String? = null
    ): Result<List<FoodItem>> = runCatching {
        val response = foodApi.searchDishes(query = query, category = category, dietary = dietary)
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun getCategories(): Result<List<Category>> = runCatching {
        val response = foodApi.getCategories()
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun getDietaryTags(): Result<List<DietaryTag>> = runCatching {
        val response = foodApi.getDietaryTags()
        response.body() ?: error("Tuščias atsakymas")
    }

    suspend fun getDishById(id: Int): Result<DishDetails> = runCatching {
        val response = foodApi.getDishById(id)
        response.body() ?: error("Patiekalas nerastas")
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

    suspend fun getOrders(clientId: Int): Result<List<Order>> = runCatching {
        val response = foodApi.getOrders(clientId)
        response.body() ?: error("Tuščias atsakymas")
    }
}