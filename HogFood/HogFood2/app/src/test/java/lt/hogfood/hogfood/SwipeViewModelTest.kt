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
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.data.repository.FoodRepository
import lt.hogfood.hogfood.ui.swipe.SwipeViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SwipeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FoodRepository
    private lateinit var viewModel: SwipeViewModel

    private val mockItem = RecommendationItem(
        menu_item_id = 1,
        title = "Cepelinai",
        restaurant_name = "Gurmanai",
        price = "12.90",
        image_url = "https://example.com/img.jpg",
        description = "Lietuviškas patiekalas",
        categories = listOf("Pagrindiniai"),
        dietary_tags = listOf("Su mėsa")
    )

    private val mockItem2 = RecommendationItem(
        menu_item_id = 2,
        title = "Šaltibarščiai",
        restaurant_name = "Užeiga",
        price = "7.50",
        description = "Šaltas burokėlių sriuba",
        categories = listOf("Sriubos"),
        dietary_tags = listOf("Vegetariška")
    )

    private val mockDishDetails = DishDetails(
        id = 1,
        name = "Cepelinai",
        description = "Lietuviškas patiekalas",
        price = 12.90,
        restaurantName = "Gurmanai",
        images = emptyList(),
        categories = listOf("Pagrindiniai"),
        dietaryTags = listOf("Su mėsa")
    )

    private val mockDishDetails2 = DishDetails(
        id = 2,
        name = "Šaltibarščiai",
        description = "Šaltas burokėlių sriuba",
        price = 7.50,
        restaurantName = "Užeiga",
        images = emptyList(),
        categories = listOf("Sriubos"),
        dietaryTags = listOf("Vegetariška")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // FR-5 / TA-01 (HOG-76): Atidaryti "Tau patiks" tab'ą — langas užkraunamas su patiekalu
    @Test
    fun `FR5-TA01 recommendation screen loads item with name restaurant and categories`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.success(mockItem)
        coEvery { repository.getDishById(1) } returns Result.success(mockDishDetails)

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val item = viewModel.currentItem.value
        assertNotNull(item)
        assertEquals("Cepelinai", item?.title)
        assertEquals("Gurmanai", item?.restaurant_name)
        assertTrue(item?.categories?.contains("Pagrindiniai") == true)
        assertFalse(viewModel.isLoading.value)
    }

    // FR-5 / TA-02 (HOG-76): API nepasiekiamas — rodoma klaida
    @Test
    fun `FR5-TA02 error displayed when API is unreachable`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.failure(Exception("Tinklo klaida"))

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertEquals("Tinklo klaida", viewModel.error.value)
        assertNull(viewModel.currentItem.value)
    }

    // FR-5 / TA-03 (HOG-77): Paspausti "Patinka" mygtuką
    @Test
    fun `FR5-TA03 like button sends like request and loads next item`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.success(mockItem) andThen Result.success(mockItem2)
        coEvery { repository.getDishById(1) } returns Result.success(mockDishDetails)
        coEvery { repository.getDishById(2) } returns Result.success(mockDishDetails2)
        coEvery { repository.likeItem(1) } returns Result.success(Unit)

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Cepelinai", viewModel.currentItem.value?.title)

        viewModel.like()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.likeItem(1) }
        assertEquals("Šaltibarščiai", viewModel.currentItem.value?.title)
    }

    // FR-5 / TA-04 (HOG-77): Paspausti "Nepatinka" mygtuką
    @Test
    fun `FR5-TA04 dislike button sends dislike request and loads next item`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.success(mockItem) andThen Result.success(mockItem2)
        coEvery { repository.getDishById(1) } returns Result.success(mockDishDetails)
        coEvery { repository.getDishById(2) } returns Result.success(mockDishDetails2)
        coEvery { repository.dislikeItem(1) } returns Result.success(Unit)

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dislike()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.dislikeItem(1) }
        assertEquals("Šaltibarščiai", viewModel.currentItem.value?.title)
    }

    // FR-5 / TA-05 (HOG-78): Gauti duomenys atvaizduojami kortelėje
    @Test
    fun `FR5-TA05 received data is correctly displayed on card`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.success(mockItem)
        coEvery { repository.getDishById(1) } returns Result.success(mockDishDetails)

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        val item = viewModel.currentItem.value!!
        assertEquals("Cepelinai", item.title)
        assertEquals("Gurmanai", item.restaurant_name)
        assertEquals("12.90", item.price)
        assertTrue(item.categories.contains("Pagrindiniai"))
    }

    // FR-5 / TA-06 (HOG-79): Paspaudus "Patinka" — like endpoint kviečiamas su teisingu itemId
    @Test
    fun `FR5-TA06 like endpoint called with correct item id`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.success(mockItem) andThen Result.success(null)
        coEvery { repository.getDishById(1) } returns Result.success(mockDishDetails)
        coEvery { repository.likeItem(any()) } returns Result.success(Unit)

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.like()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.likeItem(1) }
    }

    // FR-5 / TA-07 (HOG-79): Paspaudus "Nepatinka" — dislike endpoint kviečiamas su teisingu itemId
    @Test
    fun `FR5-TA07 dislike endpoint called with correct item id`() = runTest {
        coEvery { repository.getInteractionItem() } returns Result.success(mockItem) andThen Result.success(null)
        coEvery { repository.getDishById(1) } returns Result.success(mockDishDetails)
        coEvery { repository.dislikeItem(any()) } returns Result.success(Unit)

        viewModel = SwipeViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.dislike()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.dislikeItem(1) }
    }
}