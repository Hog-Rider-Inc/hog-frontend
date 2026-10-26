package lt.hogfood.hogfood.data.repository

import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.mock.mockCategories
import lt.hogfood.hogfood.data.mock.mockDietaryTags
import lt.hogfood.hogfood.data.mock.mockFoodItems
import lt.hogfood.hogfood.data.mock.mockOrders
import lt.hogfood.hogfood.data.mock.mockRecommendations
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
        val body = response.body()
        if (body.isNullOrEmpty()) mockFoodItems else body
    }.recoverCatching { mockFoodItems }

    suspend fun searchDishes(
        query: String = "",
        category: String? = null,
        dietary: String? = null
    ): Result<List<FoodItem>> = runCatching {
        val response = foodApi.searchDishes(query = query, category = category, dietary = dietary)
        val body = response.body()
        if (body.isNullOrEmpty()) filterMock(query, category, dietary) else body
    }.recoverCatching { filterMock(query, category, dietary) }

    private fun filterMock(query: String, category: String?, dietary: String?): List<FoodItem> {
        return mockFoodItems.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.restaurantName.contains(query, ignoreCase = true)
            val matchesCategory = category == null ||
                    item.categories.any { it.title.equals(category, ignoreCase = true) }
            val matchesDiet = dietary == null ||
                    item.dietaryTags.any { it.title.equals(dietary, ignoreCase = true) }
            matchesQuery && matchesCategory && matchesDiet
        }
    }

    suspend fun getCategories(): Result<List<Category>> = runCatching {
        val response = foodApi.getCategories()
        val body = response.body()
        if (body.isNullOrEmpty()) mockCategories else body
    }.recoverCatching { mockCategories }

    suspend fun getDietaryTags(): Result<List<DietaryTag>> = runCatching {
        val response = foodApi.getDietaryTags()
        val body = response.body()
        if (body.isNullOrEmpty()) mockDietaryTags else body
    }.recoverCatching { mockDietaryTags }

    suspend fun getDishById(id: Int): Result<DishDetails> = runCatching {
        val response = foodApi.getDishById(id)
        response.body() ?: mockFoodItems.find { it.id == id }?.toDetails()
        ?: error("Patiekalas nerastas")
    }.recoverCatching {
        mockFoodItems.find { it.id == id }?.toDetails()
            ?: mockFoodItems.first().toDetails()
    }

    suspend fun getRecommendations(): Result<List<RecommendationItem>> = runCatching {
        val response = recommendApi.getRecommendations()
        val body = response.body()
        if (body.isNullOrEmpty()) mockRecommendations else body
    }.recoverCatching { mockRecommendations }

    private var mockSwipeIndex = 0

    suspend fun getInteractionItem(): Result<RecommendationItem?> = runCatching {
        val response = interactionApi.getInteractionItem()
        response.body() ?: mockSwipeItem()
    }.recoverCatching { mockSwipeItem() }

    private fun mockSwipeItem(): RecommendationItem? {
        if (mockSwipeIndex >= mockRecommendations.size) return null
        return mockRecommendations[mockSwipeIndex]
    }

    suspend fun likeItem(itemId: Int): Result<Unit> = runCatching {
        interactionApi.likeItem(itemId = itemId)
        mockSwipeIndex++
        Unit
    }.recoverCatching { mockSwipeIndex++; Unit }

    suspend fun dislikeItem(itemId: Int): Result<Unit> = runCatching {
        interactionApi.dislikeItem(itemId = itemId)
        mockSwipeIndex++
        Unit
    }.recoverCatching { mockSwipeIndex++; Unit }

    suspend fun getOrders(clientId: Int): Result<List<Order>> = runCatching {
        val response = foodApi.getOrders(clientId)
        val body = response.body()
        if (body.isNullOrEmpty()) mockOrders else body
    }.recoverCatching { mockOrders }
}

private fun FoodItem.toDetails() = DishDetails(
    id = id,
    name = name,
    description = "Puikus patiekalas iš ${restaurantName}. Paruoštas iš šviežių ingredientų.",
    price = price,
    restaurantName = restaurantName,
    images = if (imageUrl != null) listOf(imageUrl) else emptyList(),
    categories = categories.map { it.title },
    dietaryTags = dietaryTags.map { it.title }
)