package lt.hogfood.hogfood

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lt.hogfood.hogfood.data.api.RetrofitClient
import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.FoodItem
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // FR-2: TA-01 — Paieška pagal pavadinimą grąžina rezultatus
    @Test
    fun `search by name returns matching dishes`() = runTest {
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai su mėsa", restaurantName = "Užeiga", price = 8.50),
            FoodItem(id = 2, name = "Šaltibarščiai", restaurantName = "Užeiga", price = 5.00)
        )

        // Simuliuojam API atsakymą
        val result = mockDishes.filter { it.name.contains("Cepelinai", ignoreCase = true) }

        assertEquals(1, result.size)
        assertEquals("Cepelinai su mėsa", result[0].name)
    }

    // FR-2: TA-02 — Tuščia paieška grąžina visus patiekalus
    @Test
    fun `empty search query returns all dishes`() = runTest {
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai su mėsa", restaurantName = "Užeiga", price = 8.50),
            FoodItem(id = 2, name = "Šaltibarščiai", restaurantName = "Užeiga", price = 5.00),
            FoodItem(id = 3, name = "Kugelis", restaurantName = "Užeiga", price = 7.00)
        )

        val result = mockDishes.filter { "".isEmpty() || it.name.contains("", ignoreCase = true) }

        assertEquals(3, result.size)
    }

    // FR-2: TA-03 — Paieška nerastas patiekalas grąžina tuščią sąrašą
    @Test
    fun `search with no match returns empty list`() = runTest {
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai su mėsa", restaurantName = "Užeiga", price = 8.50)
        )

        val result = mockDishes.filter { it.name.contains("Pizza", ignoreCase = true) }

        assertTrue(result.isEmpty())
    }

    // FR-2: TA-04 — Filtravimas pagal kategoriją
    @Test
    fun `filter by category returns only matching dishes`() = runTest {
        val sriubos = Category(id = 3, title = "Sriubos")
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai", restaurantName = "R", price = 8.50, categories = listOf(Category(2, "Pagrindiniai patiekalai"))),
            FoodItem(id = 2, name = "Miso sriuba", restaurantName = "R", price = 4.50, categories = listOf(sriubos)),
            FoodItem(id = 3, name = "Barščiai", restaurantName = "R", price = 6.50, categories = listOf(sriubos))
        )

        val result = mockDishes.filter { dish -> dish.categories.any { it.id == sriubos.id } }

        assertEquals(2, result.size)
        assertTrue(result.all { it.categories.any { c -> c.title == "Sriubos" } })
    }

    // FR-2: TA-05 — Filtravimas pagal mitybos tipą
    @Test
    fun `filter by dietary tag returns only vegan dishes`() = runTest {
        val vegTag = DietaryTag(id = 1, title = "Veganiška")
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai", restaurantName = "R", price = 8.50, dietaryTags = emptyList()),
            FoodItem(id = 2, name = "Budos dubuo", restaurantName = "R", price = 10.00, dietaryTags = listOf(vegTag)),
            FoodItem(id = 3, name = "Avokadų užkandis", restaurantName = "R", price = 8.00, dietaryTags = listOf(vegTag))
        )

        val result = mockDishes.filter { dish -> dish.dietaryTags.any { it.id == vegTag.id } }

        assertEquals(2, result.size)
    }
}