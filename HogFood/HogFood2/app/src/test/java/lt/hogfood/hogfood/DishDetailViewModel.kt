package lt.hogfood.hogfood

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lt.hogfood.hogfood.data.model.DishDetails
import lt.hogfood.hogfood.data.repository.FoodRepository
import lt.hogfood.hogfood.ui.detail.DishDetailViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DishDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FoodRepository
    private lateinit var viewModel: DishDetailViewModel

    private val mockDish = DishDetails(
        id = 1,
        name = "Cepelinai su mėsa",
        description = "Tradicinis lietuviškas patiekalas",
        price = 12.90,
        restaurantName = "Gurmanai",
        images = listOf("https://example.com/cepelinai.jpg"),
        categories = listOf("Pagrindiniai patiekalai"),
        dietaryTags = listOf("Su mėsa")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // FR-16 / TA-01 (HOG-82): Paspausti ant patiekalo — atidaromas detalių langas
    @Test
    fun `FR16-TA01 dish detail screen loads successfully`() = runTest {
        coEvery { repository.getDishById(1) } returns Result.success(mockDish)

        viewModel = DishDetailViewModel(repository)
        viewModel.loadDish(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.dish.value)
        assertFalse(viewModel.isLoading.value)
        assertNull(viewModel.error.value)
    }

    // FR-16 / TA-02 (HOG-83): Detalių lange rodomas patiekalo pavadinimas
    @Test
    fun `FR16-TA02 dish name is displayed correctly`() = runTest {
        coEvery { repository.getDishById(1) } returns Result.success(mockDish)

        viewModel = DishDetailViewModel(repository)
        viewModel.loadDish(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Cepelinai su mėsa", viewModel.dish.value?.name)
    }

    // FR-16 / TA-03 (HOG-83): Rodomas restorano pavadinimas ir kaina
    @Test
    fun `FR16-TA03 restaurant name and price displayed`() = runTest {
        coEvery { repository.getDishById(1) } returns Result.success(mockDish)

        viewModel = DishDetailViewModel(repository)
        viewModel.loadDish(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("Gurmanai", viewModel.dish.value?.restaurantName)
        assertEquals(12.90, viewModel.dish.value?.price ?: 0.0, 0.01)
    }

    // FR-16 / TA-04 (HOG-84): Rodomos kategorijos ir mitybos žymos
    @Test
    fun `FR16-TA04 categories and dietary tags displayed`() = runTest {
        coEvery { repository.getDishById(1) } returns Result.success(mockDish)

        viewModel = DishDetailViewModel(repository)
        viewModel.loadDish(1)
        testDispatcher.scheduler.advanceUntilIdle()

        val dish = viewModel.dish.value!!
        assertTrue(dish.categories.contains("Pagrindiniai patiekalai"))
        assertTrue(dish.dietaryTags.contains("Su mėsa"))
    }

    // FR-16 / TA-05 (HOG-84): Rodomas aprašymas
    @Test
    fun `FR16-TA05 description is displayed`() = runTest {
        coEvery { repository.getDishById(1) } returns Result.success(mockDish)

        viewModel = DishDetailViewModel(repository)
        viewModel.loadDish(1)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.dish.value!!.description.isNotEmpty())
        assertEquals("Tradicinis lietuviškas patiekalas", viewModel.dish.value?.description)
    }
}