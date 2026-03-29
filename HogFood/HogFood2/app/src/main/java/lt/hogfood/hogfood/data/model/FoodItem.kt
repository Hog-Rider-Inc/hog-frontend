package lt.hogfood.hogfood.data.model

data class FoodItem(
    val id: Int = 0,
    val name: String = "",
    val restaurantName: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val rating: Double = 4.5,
    val categories: List<Category> = emptyList(),
    val dietaryTags: List<DietaryTag> = emptyList()
)

data class Category(
    val id: Int = 0,
    val title: String = ""
)

data class DietaryTag(
    val id: Int = 0,
    val title: String = ""
)

data class RecommendationItem(
    val menu_item_id: Int = 0,
    val title: String = "",
    val restaurant_name: String = "",
    val price: String = "",
    val image_url: String? = null,
    val categories: List<String> = emptyList(),
    val dietary_tags: List<String> = emptyList()
)

data class DishDetails(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val restaurantName: String = "",
    val images: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val dietaryTags: List<String> = emptyList()
)

data class Order(
    val id: Int = 0,
    val status: String = "",
    val totalPrice: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = "",
    val clientName: String = "",
    val address: String = "",
    val items: List<OrderItem> = emptyList()
)

data class OrderItem(
    val name: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
)