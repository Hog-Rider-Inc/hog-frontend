package lt.hogfood.hogfood.data.mock

import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem

val mockFoodItems = listOf(
    FoodItem(
        id = 1,
        name = "Cepelinai su mėsa",
        restaurantName = "Gurmanai",
        price = 12.90,
        categories = listOf(Category(1, "Karštieji"))
    ),
    FoodItem(
        id = 2,
        name = "Šaltibarščiai",
        restaurantName = "Gurmanai",
        price = 7.50,
        categories = listOf(Category(2, "Sriubos")),
        dietaryTags = listOf(DietaryTag(1, "Veganiška"))
    ),
    FoodItem(
        id = 3,
        name = "Klasikinis Drama Burger",
        restaurantName = "Drama Burger",
        price = 13.50,
        categories = listOf(Category(3, "Burgeriai"))
    ),
    FoodItem(
        id = 4,
        name = "Margarita",
        restaurantName = "Casa La Familia",
        price = 11.00,
        categories = listOf(Category(4, "Picos"))
    ),
    FoodItem(
        id = 5,
        name = "Kugelis",
        restaurantName = "Gurmanai",
        price = 11.50,
        categories = listOf(Category(1, "Karštieji"))
    ),
    FoodItem(
        id = 6,
        name = "Tiramisu",
        restaurantName = "Casa La Familia",
        price = 6.50,
        categories = listOf(Category(5, "Desertai"))
    ),
    FoodItem(
        id = 7,
        name = "Varškės virtinukai",
        restaurantName = "Etno Dvaras",
        price = 9.50,
        categories = listOf(Category(1, "Karštieji")),
        dietaryTags = listOf(DietaryTag(2, "Vegetariška"))
    ),
    FoodItem(
        id = 8,
        name = "Kepinta duona su česnaku",
        restaurantName = "Šnekutis",
        price = 5.00,
        categories = listOf(Category(6, "Užkandžiai"))
    )
)

val mockRecommendations = listOf(
    RecommendationItem(
        menu_item_id = 2,
        title = "Šaltibarščiai",
        restaurant_name = "Gurmanai",
        price = "7.50",
        description = "Gaivi ir kąsnis maltos daržovės su šaltu burokėlių sultiniu, tradicinis lietuviškas patiekalas.",
        categories = listOf("Sriubos"),
        dietary_tags = listOf("Veganiška")
    ),
    RecommendationItem(
        menu_item_id = 3,
        title = "Klasikinis Drama Burger",
        restaurant_name = "Drama Burger",
        price = "13.50",
        description = "Sultingas jautienos mėsainis su klasikiniu padažu ir traškiais priedais.",
        categories = listOf("Burgeriai"),
        dietary_tags = emptyList()
    ),
    RecommendationItem(
        menu_item_id = 4,
        title = "Margarita",
        restaurant_name = "Casa La Familia",
        price = "11.00",
        description = "Klasikinė Neapolio stiliaus pica su pomidorų padažu ir mocarela.",
        categories = listOf("Picos"),
        dietary_tags = emptyList()
    )
)