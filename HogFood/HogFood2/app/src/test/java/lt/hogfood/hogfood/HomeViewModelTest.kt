package lt.hogfood.hogfood

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.data.repository.FoodRepository
import lt.hogfood.hogfood.ui.home.HomeViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FoodRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadData success - foodItems updated`() = runTest {
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai", restaurantName = "Gurmanai", price = 12.90),
            FoodItem(id = 2, name = "Šaltibarščiai", restaurantName = "Gurmanai", price = 7.50)
        )
        coEvery { repository.getAllDishes() } returns Result.success(mockDishes)
        coEvery { repository.getRecommendations() } returns Result.success(emptyList())

        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(2, viewModel.foodItems.value.size)
        assertEquals("Cepelinai", viewModel.foodItems.value[0].name)
    }

    @Test
    fun `loadData failure - error message set`() = runTest {
        coEvery { repository.getAllDishes() } returns Result.failure(Exception("Tinklo klaida"))
        coEvery { repository.getRecommendations() } returns Result.success(emptyList())

        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Tinklo klaida", viewModel.error.value)
        assertTrue(viewModel.foodItems.value.isEmpty())
    }

    @Test
    fun `loadData - recommendations loaded successfully`() = runTest {
        val mockRecs = listOf(
            RecommendationItem(menu_item_id = 1, title = "Šaltibarščiai", restaurant_name = "Gurmanai", price = "7.5")
        )
        coEvery { repository.getAllDishes() } returns Result.success(emptyList())
        coEvery { repository.getRecommendations() } returns Result.success(mockRecs)

        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.recommendations.value.size)
        assertEquals("Šaltibarščiai", viewModel.recommendations.value[0].title)
    }

    @Test
    fun `isLoading is false after loadData completes`() = runTest {
        coEvery { repository.getAllDishes() } returns Result.success(emptyList())
        coEvery { repository.getRecommendations() } returns Result.success(emptyList())

        viewModel = HomeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }
}