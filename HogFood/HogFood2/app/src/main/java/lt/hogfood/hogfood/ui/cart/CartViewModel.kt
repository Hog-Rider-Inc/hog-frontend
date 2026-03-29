package lt.hogfood.hogfood.ui.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.model.CartItem
import lt.hogfood.hogfood.data.model.DishDetails
import lt.hogfood.hogfood.data.model.OrderedMenuItem
import lt.hogfood.hogfood.data.model.OrderRequest

class CartViewModel : ViewModel() {

    private val _items = MutableStateFlow<List<CartItem>>(emptyList())
    val items = _items.asStateFlow()

    private val _isPlacingOrder = MutableStateFlow(false)
    val isPlacingOrder = _isPlacingOrder.asStateFlow()

    private val _orderError = MutableStateFlow<String?>(null)
    val orderError = _orderError.asStateFlow()

    private val foodApi = RetrofitClient.foodApi

    val totalPrice: Double
        get() = _items.value.sumOf { it.price * it.quantity }

    val totalCount: Int
        get() = _items.value.sumOf { it.quantity }

    fun addToCart(dish: DishDetails, quantity: Int) {
        val current = _items.value.toMutableList()
        val existing = current.indexOfFirst { it.dishId == dish.id }
        if (existing >= 0) {
            current[existing] = current[existing].copy(quantity = current[existing].quantity + quantity)
        } else {
            current.add(
                CartItem(
                    dishId = dish.id,
                    name = dish.name,
                    restaurantName = dish.restaurantName,
                    price = dish.price,
                    imageUrl = dish.images.firstOrNull(),
                    quantity = quantity
                )
            )
        }
        _items.value = current
    }

    fun increaseQuantity(dishId: Int) {
        _items.value = _items.value.map {
            if (it.dishId == dishId) it.copy(quantity = it.quantity + 1) else it
        }
    }

    fun decreaseQuantity(dishId: Int) {
        val current = _items.value.toMutableList()
        val index = current.indexOfFirst { it.dishId == dishId }
        if (index >= 0) {
            if (current[index].quantity <= 1) {
                current.removeAt(index)
            } else {
                current[index] = current[index].copy(quantity = current[index].quantity - 1)
            }
        }
        _items.value = current
    }

    fun removeItem(dishId: Int) {
        _items.value = _items.value.filter { it.dishId != dishId }
    }

    fun clearCart() {
        _items.value = emptyList()
    }

    fun placeOrder(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isPlacingOrder.value = true
            _orderError.value = null
            try {
                val orderedItems = _items.value.map { item ->
                    OrderedMenuItem(
                        id = item.dishId,
                        quantity = item.quantity
                    )
                }
                val orderRequest = OrderRequest(
                    orderedMenuItems = orderedItems
                )
                val response = foodApi.placeOrder(userId = 1, request = orderRequest)
                if (response.isSuccessful) {
                    clearCart()
                    onSuccess()
                } else {
                    _orderError.value = "Nepavyko pateikti užsakymo"
                }
            } catch (e: Exception) {
                _orderError.value = "Klaida: ${e.message}"
            } finally {
                _isPlacingOrder.value = false
            }
        }
    }
}