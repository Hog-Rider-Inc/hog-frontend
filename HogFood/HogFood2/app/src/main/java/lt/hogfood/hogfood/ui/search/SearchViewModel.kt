package lt.hogfood.hogfood.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.FoodItem

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel : ViewModel() {

    private val foodApi = RetrofitClient.foodApi

    val query = MutableStateFlow("")
    val selectedCategory = MutableStateFlow<Category?>(null)
    val selectedDiet = MutableStateFlow<DietaryTag?>(null)

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _dietaryTags = MutableStateFlow<List<DietaryTag>>(emptyList())
    val dietaryTags: StateFlow<List<DietaryTag>> = _dietaryTags

    private val _results = MutableStateFlow<List<FoodItem>>(emptyList())
    val results: StateFlow<List<FoodItem>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadFilters()
        setupSearch()
    }

    private fun loadFilters() {
        viewModelScope.launch {
            try {
                val categoriesResponse = foodApi.getCategories()
                if (categoriesResponse.isSuccessful) {
                    _categories.value = categoriesResponse.body() ?: emptyList()
                }

                val dietaryResponse = foodApi.getDietaryTags()
                if (dietaryResponse.isSuccessful) {
                    _dietaryTags.value = dietaryResponse.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = "Klaida kraunant filtrus: ${e.message}"
            }
        }
    }

    private fun setupSearch() {
        combine(query, selectedCategory, selectedDiet) { q, cat, diet ->
            Triple(q, cat, diet)
        }
            .debounce(300)
            .distinctUntilChanged()
            .mapLatest { (q, cat, diet) -> performSearch(q, cat, diet) }
            .launchIn(viewModelScope)
    }

    fun refresh() {
        loadFilters()
        viewModelScope.launch {
            performSearch(query.value, selectedCategory.value, selectedDiet.value)
        }
    }

    private suspend fun performSearch(
        searchQuery: String,
        category: Category?,
        diet: DietaryTag?
    ) {
        _isLoading.value = true
        try {
            val response = foodApi.searchDishes(
                query = searchQuery,
                category = category?.title,
                dietary = diet?.title
            )
            if (response.isSuccessful) {
                _results.value = response.body() ?: emptyList()
            } else {
                _results.value = emptyList()
            }
        } catch (e: Exception) {
            _error.value = "Klaida: ${e.message}"
            _results.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun setCategory(category: Category?) {
        selectedCategory.value = category
    }

    fun setDiet(diet: DietaryTag?) {
        selectedDiet.value = diet
    }
}