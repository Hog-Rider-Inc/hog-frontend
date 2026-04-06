package lt.hogfood.hogfood

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lt.hogfood.hogfood.data.model.Category
import lt.hogfood.hogfood.data.model.DietaryTag
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.repository.FoodRepository
import lt.hogfood.hogfood.ui.search.SearchViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FoodRepository
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getCategories() } returns Result.success(emptyList())
        coEvery { repository.getDietaryTags() } returns Result.success(emptyList())
        coEvery { repository.searchDishes(any(), any(), any()) } returns Result.success(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // FR-2: TA-01 (HOG-157) — Paieška pagal pavadinimą grąžina rezultatus
    @Test
    fun `HOG157 search by name returns matching dishes`() = runTest {
        val mockDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai su mėsa", restaurantName = "Užeiga", price = 8.50)
        )
        coEvery { repository.searchDishes(query = "Cepelinai", any(), any()) } returns Result.success(mockDishes)

        viewModel = SearchViewModel(repository)
        viewModel.query.value = "Cepelinai"
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.runCurrent()

        assertEquals(1, viewModel.results.value.size)
        assertEquals("Cepelinai su mėsa", viewModel.results.value[0].name)
    }

    // FR-2: TA-02 (HOG-153) — Tuščia paieška grąžina visus patiekalus
    @Test
    fun `HOG153 empty search returns all dishes`() = runTest {
        val allDishes = listOf(
            FoodItem(id = 1, name = "Cepelinai", restaurantName = "R", price = 8.50),
            FoodItem(id = 2, name = "Sriuba", restaurantName = "R", price = 5.00),
            FoodItem(id = 3, name = "Kugelis", restaurantName = "R", price = 7.00)
        )
        coEvery { repository.searchDishes(query = "", any(), any()) } returns Result.success(allDishes)

        viewModel = SearchViewModel(repository)
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.runCurrent()

        assertEquals(3, viewModel.results.value.size)
    }

    // FR-2: TA-03 (HOG-156) — Nerastas patiekalas grąžina tuščią sąrašą
    @Test
    fun `HOG156 search with no match returns empty list`() = runTest {
        coEvery { repository.searchDishes(query = "Pizza", any(), any()) } returns Result.success(emptyList())

        viewModel = SearchViewModel(repository)
        viewModel.query.value = "Pizza"
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.results.value.isEmpty())
    }

    // FR-2: TA-04 (HOG-154) — isLoading false po paieškos
    @Test
    fun `HOG154 isLoading is false after search completes`() = runTest {
        coEvery { repository.searchDishes(any(), any(), any()) } returns Result.success(emptyList())

        viewModel = SearchViewModel(repository)
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.runCurrent()

        assertFalse(viewModel.isLoading.value)
    }

    // FR-2: TA-05 (HOG-155) — API klaida — error state nustatomas
    @Test
    fun `HOG155 search api failure sets error state`() = runTest {
        coEvery { repository.searchDishes(any(), any(), any()) } returns Result.failure(Exception("Tinklo klaida"))

        viewModel = SearchViewModel(repository)
        testDispatcher.scheduler.advanceTimeBy(400)
        testDispatcher.scheduler.runCurrent()

        assertNotNull(viewModel.error.value)
    }
}