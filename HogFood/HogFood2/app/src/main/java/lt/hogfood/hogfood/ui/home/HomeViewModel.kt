package lt.hogfood.hogfood.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.mock.mockRecommendations
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.data.repository.FoodRepository

class HomeViewModel(
    private val repository: FoodRepository = FoodRepository()
) : ViewModel() {

    private val _foodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val foodItems: StateFlow<List<FoodItem>> = _foodItems

    private val _recommendations = MutableStateFlow<List<RecommendationItem>>(emptyList())
    val recommendations: StateFlow<List<RecommendationItem>> = _recommendations

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            android.util.Log.d("HomeViewModel", "Kraunami patiekalai...")
            repository.getAllDishes()
                .onSuccess {
                    android.util.Log.d("HomeViewModel", "Patiekalai gauti: ${it.size}")
                    _foodItems.value = it
                }
                .onFailure {
                    android.util.Log.e("HomeViewModel", "Patiekalų klaida: ${it.message}")
                    _error.value = it.message
                }

            android.util.Log.d("HomeViewModel", "Kraunamos rekomendacijos...")
            repository.getRecommendations()
                .onSuccess {
                    android.util.Log.d("HomeViewModel", "Rekomendacijos gautos: ${it.size}")
                    _recommendations.value = it
                }
                .onFailure {
                    android.util.Log.e("HomeViewModel", "Rekomendacijų klaida: ${it.message}")
                    _recommendations.value = mockRecommendations
                }

            _isLoading.value = false
        }
    }
}