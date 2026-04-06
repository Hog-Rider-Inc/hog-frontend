package lt.hogfood.hogfood.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.model.DishDetails
import lt.hogfood.hogfood.data.repository.FoodRepository

class DishDetailViewModel(
    private val repository: FoodRepository = FoodRepository()
) : ViewModel() {

    private val _dish = MutableStateFlow<DishDetails?>(null)
    val dish = _dish.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun loadDish(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getDishById(id)
                .onSuccess { _dish.value = it }
                .onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }
}