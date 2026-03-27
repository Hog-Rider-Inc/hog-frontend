package lt.hogfood.hogfood.ui.swipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.data.repository.FoodRepository

class SwipeViewModel : ViewModel() {

    private val repository = FoodRepository()

    private val _currentItem = MutableStateFlow<RecommendationItem?>(null)
    val currentItem = _currentItem.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _finished = MutableStateFlow(false)
    val finished = _finished.asStateFlow()

    init {
        loadItem()
    }

    private fun loadItem() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.getInteractionItem()
                .onSuccess { item ->
                    if (item == null) {
                        _finished.value = true
                    } else {
                        _finished.value = false
                        _currentItem.value = item
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Nepavyko gauti patiekalo"
                }
            _isLoading.value = false
        }
    }

    fun like() {
        val item = _currentItem.value ?: return
        viewModelScope.launch {
            repository.likeItem(item.menu_item_id)
            loadItem()
        }
    }

    fun dislike() {
        val item = _currentItem.value ?: return
        viewModelScope.launch {
            repository.dislikeItem(item.menu_item_id)
            loadItem()
        }
    }

    fun restart() {
        _finished.value = false
        loadItem()
    }
}