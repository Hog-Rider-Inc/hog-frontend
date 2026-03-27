package lt.hogfood.hogfood.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import lt.hogfood.hogfood.data.mock.mockFoodItems
import lt.hogfood.hogfood.data.mock.mockRecommendations
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem

class HomeViewModel : ViewModel() {

    private val _foodItems = MutableStateFlow<List<FoodItem>>(mockFoodItems)
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    private val _recommendations = MutableStateFlow<List<RecommendationItem>>(mockRecommendations)
    val recommendations: StateFlow<List<RecommendationItem>> = _recommendations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
}