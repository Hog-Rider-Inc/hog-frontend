package lt.hogfood.hogfood.data.mock

import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.Order
import lt.hogfood.hogfood.data.model.OrderItem
import lt.hogfood.hogfood.data.model.RecommendationItem

val mockFoodItems = listOf(
    FoodItem(
        id = 1,
        name = "Cepelinai su mėsa",
        restaurantName = "Gurmanai",
        price = 12.90,
        rating = 4.8,
        categories = listOf(Category(1, "Karštieji"))
    ),
    FoodItem(
        id = 2,
        name = "Šaltibarščiai",
        restaurantName = "Gurmanai",
        price = 7.50,
        rating = 4.6,
        categories = listOf(Category(2, "Sriubos")),
        dietaryTags = listOf(DietaryTag(1, "Veganiška"))
    ),
    FoodItem(
        id = 3,
        name = "Klasikinis Drama Burger",
        restaurantName = "Drama Burger",
        price = 13.50,
        rating = 4.7,
        categories = listOf(Category(3, "Burgeriai"))
    ),
    FoodItem(
        id = 4,
        name = "Margarita",
        restaurantName = "Casa La Familia",
        price = 11.00,
        rating = 4.5,
        categories = listOf(Category(4, "Picos"))
    ),
    FoodItem(
        id = 5,
        name = "Kugelis",
        restaurantName = "Gurmanai",
        price = 11.50,
        rating = 4.9,
        categories = listOf(Category(1, "Karštieji"))
    ),
    FoodItem(
        id = 6,
        name = "Tiramisu",
        restaurantName = "Casa La Familia",
        price = 6.50,
        rating = 4.7,
        categories = listOf(Category(5, "Desertai"))
    ),
    FoodItem(
        id = 7,
        name = "Varškės virtinukai",
        restaurantName = "Etno Dvaras",
        price = 9.50,
        rating = 4.6,
        categories = listOf(Category(1, "Karštieji")),
        dietaryTags = listOf(DietaryTag(2, "Vegetariška"))
    ),
    FoodItem(
        id = 8,
        name = "Kepinta duona su česnaku",
        restaurantName = "Šnekutis",
        price = 5.00,
        rating = 4.4,
        categories = listOf(Category(6, "Užkandžiai"))
    ),
    FoodItem(
        id = 9,
        name = "Lašišos sushi rinkinys",
        restaurantName = "Tokyo Garden",
        price = 18.90,
        rating = 4.8,
        categories = listOf(Category(7, "Suši")),
        dietaryTags = listOf(DietaryTag(3, "Be glitimo"))
    ),
    FoodItem(
        id = 10,
        name = "Graikų salotos",
        restaurantName = "Mediterrano",
        price = 8.90,
        rating = 4.5,
        categories = listOf(Category(6, "Užkandžiai")),
        dietaryTags = listOf(DietaryTag(1, "Veganiška"), DietaryTag(2, "Vegetariška"))
    ),
    FoodItem(
        id = 11,
        name = "Pepperoni pica",
        restaurantName = "Casa La Familia",
        price = 13.00,
        rating = 4.6,
        categories = listOf(Category(4, "Picos"))
    ),
    FoodItem(
        id = 12,
        name = "Vištienos sriuba",
        restaurantName = "Etno Dvaras",
        price = 7.90,
        rating = 4.4,
        categories = listOf(Category(2, "Sriubos"))
    )
)

val mockCategories = listOf(
    Category(1, "Karštieji"),
    Category(2, "Sriubos"),
    Category(3, "Burgeriai"),
    Category(4, "Picos"),
    Category(5, "Desertai"),
    Category(6, "Užkandžiai"),
    Category(7, "Suši")
)

val mockDietaryTags = listOf(
    DietaryTag(1, "Veganiška"),
    DietaryTag(2, "Vegetariška"),
    DietaryTag(3, "Be glitimo"),
    DietaryTag(4, "Be laktozės")
)

val mockRecommendations = listOf(
    RecommendationItem(
        menu_item_id = 1,
        title = "Cepelinai su mėsa",
        restaurant_name = "Gurmanai",
        price = "12.90",
        description = "Tradicinis lietuviškas patiekalas — dideli bulvių koldūnai su maltos mėsos įdaru ir grietinės padažu.",
        categories = listOf("Karštieji"),
        dietary_tags = emptyList()
    ),
    RecommendationItem(
        menu_item_id = 2,
        title = "Šaltibarščiai",
        restaurant_name = "Gurmanai",
        price = "7.50",
        description = "Gaivi vasariška sriuba su burokėliais, agurkais, kiaušiniais ir grietine. Patiekama su karštomis bulvėmis.",
        categories = listOf("Sriubos"),
        dietary_tags = listOf("Veganiška")
    ),
    RecommendationItem(
        menu_item_id = 3,
        title = "Klasikinis Drama Burger",
        restaurant_name = "Drama Burger",
        price = "13.50",
        description = "Sultingas jautienos mėsainis su klasikiniu padažu, traškiais agurkais ir šviežiomis salotomis.",
        categories = listOf("Burgeriai"),
        dietary_tags = emptyList()
    ),
    RecommendationItem(
        menu_item_id = 9,
        title = "Lašišos sushi rinkinys",
        restaurant_name = "Tokyo Garden",
        price = "18.90",
        description = "12 vnt. rinkinis su lašišos nigiri, philadelphia ir california suši, patiekiama su wasabi ir sojų padažu.",
        categories = listOf("Suši"),
        dietary_tags = listOf("Be glitimo")
    ),
    RecommendationItem(
        menu_item_id = 5,
        title = "Kugelis",
        restaurant_name = "Gurmanai",
        price = "11.50",
        description = "Keptas bulvių pyragas su šonine ir svogūnais, patiekiamas su grietine.",
        categories = listOf("Karštieji"),
        dietary_tags = emptyList()
    )
)

val mockOrders = listOf(
    Order(
        id = 1042,
        status = "COMPLETED",
        totalPrice = 26.40,
        createdAt = "2025-04-18T14:32:00",
        updatedAt = "2025-04-18T15:10:00",
        clientName = "Andrius",
        address = "Gedimino pr. 9, Vilnius",
        items = listOf(
            OrderItem(name = "Cepelinai su mėsa", quantity = 1, price = 12.90),
            OrderItem(name = "Šaltibarščiai", quantity = 1, price = 7.50),
            OrderItem(name = "Tiramisu", quantity = 1, price = 6.50)
        )
    ),
    Order(
        id = 1031,
        status = "COMPLETED",
        totalPrice = 31.40,
        createdAt = "2025-04-10T19:15:00",
        updatedAt = "2025-04-10T20:05:00",
        clientName = "Andrius",
        address = "Gedimino pr. 9, Vilnius",
        items = listOf(
            OrderItem(name = "Klasikinis Drama Burger", quantity = 2, price = 13.50),
            OrderItem(name = "Kepinta duona su česnaku", quantity = 1, price = 5.00)
        )
    ),
    Order(
        id = 1019,
        status = "COMPLETED",
        totalPrice = 29.90,
        createdAt = "2025-03-28T12:45:00",
        updatedAt = "2025-03-28T13:30:00",
        clientName = "Andrius",
        address = "Gedimino pr. 9, Vilnius",
        items = listOf(
            OrderItem(name = "Lašišos sushi rinkinys", quantity = 1, price = 18.90),
            OrderItem(name = "Graikų salotos", quantity = 1, price = 8.90)
        )
    )
)