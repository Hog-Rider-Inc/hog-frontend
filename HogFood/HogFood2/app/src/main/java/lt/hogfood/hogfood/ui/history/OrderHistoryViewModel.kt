package lt.hogfood.hogfood.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.model.Order
import lt.hogfood.hogfood.data.repository.FoodRepository

class OrderHistoryViewModel(
    private val repository: FoodRepository = FoodRepository(),
    private val enablePolling: Boolean = true
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadOrders()
<<<<<<< Updated upstream
=======
        if(enablePolling) startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                delay(10_000)
                val newOrders = repository.getOrders(clientId = 1).getOrNull()
                if (newOrders != null && newOrders != _orders.value) {
                    _orders.value = newOrders
                }
            }
        }
>>>>>>> Stashed changes
    }

    fun loadOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getOrders(clientId = 1)
                .onSuccess { _orders.value = it }
                .onFailure { _error.value = it.message }

            _isLoading.value = false
        }
    }
}