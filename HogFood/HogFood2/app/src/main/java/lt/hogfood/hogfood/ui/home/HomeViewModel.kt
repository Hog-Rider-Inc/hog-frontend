package lt.hogfood.hogfood.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.mock.mockRecommendations
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.data.repository.FoodRepository

class HomeViewModel(
    private val repository: FoodRepository = FoodRepository(),
    private val enablePolling: Boolean = true
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
        if (enablePolling) startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                delay(10_000)
                val newRecs = repository.getRecommendations().getOrNull()
                if (newRecs != null && newRecs != _recommendations.value) {
                    _recommendations.value = newRecs
                }
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            println("Kraunami patiekalai...")
            repository.getAllDishes()
                .onSuccess {
                    println("Patiekalai gauti: ${it.size}")
                    _foodItems.value = it
                }
                .onFailure {
                    println("Patiekalų klaida: ${it.message}")
                    _error.value = it.message
                }

            println("Kraunamos rekomendacijos...")
            repository.getRecommendations()
                .onSuccess {
                    println("Rekomendacijos gautos: ${it.size}")
                    _recommendations.value = it
                }
                .onFailure {
                    _recommendations.value = mockRecommendations
                }

            _isLoading.value = false
        }
    }
}