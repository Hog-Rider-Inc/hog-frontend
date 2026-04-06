package lt.hogfood.hogfood

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import lt.hogfood.hogfood.data.model.Order
import lt.hogfood.hogfood.data.repository.FoodRepository
import lt.hogfood.hogfood.ui.history.OrderHistoryViewModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OrderHistoryViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FoodRepository
    private lateinit var viewModel: OrderHistoryViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // FR-19: TA-01 (HOG-138) — Užsakymai sėkmingai užkraunami
    @Test
    fun `HOG138 orders loaded successfully when user has orders`() = runTest {
        val mockOrders = listOf(
            Order(id = 1, status = "Delivered", totalPrice = 20.00, createdAt = "2026-03-29T17:40:33"),
            Order(id = 2, status = "Preparing", totalPrice = 21.50, createdAt = "2026-03-29T17:40:33"),
            Order(id = 3, status = "pending_acceptance", totalPrice = 30.00, createdAt = "2026-03-29T17:40:33")
        )
        coEvery { repository.getOrders(1) } returns Result.success(mockOrders)

        viewModel = OrderHistoryViewModel(repository, enablePolling = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(3, viewModel.orders.value.size)
        assertEquals("Delivered", viewModel.orders.value[0].status)
    }

    // FR-19: TA-02 (HOG-139) — Tuščias sąrašas kai nėra užsakymų
    @Test
    fun `HOG139 empty list shown when user has no orders`() = runTest {
        coEvery { repository.getOrders(1) } returns Result.success(emptyList())

        viewModel = OrderHistoryViewModel(repository, enablePolling = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.orders.value.isEmpty())
        assertFalse(viewModel.isLoading.value)
    }

    // FR-19: TA-03 (HOG-140) — Klaida rodoma kai API neprieinamas
    @Test
    fun `HOG140 error state set when api fails`() = runTest {
        coEvery { repository.getOrders(1) } returns Result.failure(Exception("Tinklo klaida"))

        viewModel = OrderHistoryViewModel(repository, enablePolling = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.error.value)
        assertEquals("Tinklo klaida", viewModel.error.value)
    }

    // FR-19: TA-04 (HOG-141) — Užsakymai surikiuoti naujausi viršuje
    @Test
    fun `HOG141 orders are sorted newest first`() = runTest {
        val mockOrders = listOf(
            Order(id = 5, status = "pending_acceptance", totalPrice = 30.00, createdAt = "2026-03-29T17:40:33"),
            Order(id = 1, status = "Delivered", totalPrice = 20.00, createdAt = "2026-03-29T17:40:33")
        )
        coEvery { repository.getOrders(1) } returns Result.success(mockOrders)

        viewModel = OrderHistoryViewModel(repository, enablePolling = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(5, viewModel.orders.value[0].id)
    }

    // FR-19: TA-05 (HOG-142) — isLoading false po krovimo
    @Test
    fun `HOG142 isLoading is false after orders load`() = runTest {
        coEvery { repository.getOrders(1) } returns Result.success(emptyList())

        viewModel = OrderHistoryViewModel(repository, enablePolling = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isLoading.value)
    }
}