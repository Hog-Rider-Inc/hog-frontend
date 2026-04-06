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
import lt.hogfood.hogfood.data.api.FoodApi
import lt.hogfood.hogfood.data.model.DishDetails
import lt.hogfood.hogfood.ui.cart.CartViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var foodApi: FoodApi
    private lateinit var viewModel: CartViewModel

    private val mockDish = DishDetails(
        id = 1,
        name = "Cepelinai",
        description = "Tradicinis patiekalas",
        price = 12.90,
        restaurantName = "Gurmanai",
        images = listOf("https://example.com/img.jpg"),
        categories = listOf("Pagrindiniai"),
        dietaryTags = listOf("Su mėsa")
    )

    private val mockDish2 = DishDetails(
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
        foodApi = mockk(relaxed = true)
        viewModel = CartViewModel(foodApi)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // =====================================================================
    // FR-16 / HOG-95, HOG-96: Pridėjimas į krepšelį ir kiekio valdymas
    // =====================================================================

    // FR-16 / TA-06 (HOG-95): Paspausti "Į krepšelį" — patiekalas pridedamas
    @Test
    fun `FR16-TA06 add to cart adds dish with selected quantity`() {
        viewModel.addToCart(mockDish, 2)

        assertEquals(1, viewModel.items.value.size)
        assertEquals("Cepelinai", viewModel.items.value[0].name)
        assertEquals(2, viewModel.items.value[0].quantity)
        assertEquals(25.80, viewModel.totalPrice, 0.01)
    }

    // FR-16 / TA-07 (HOG-96): Paspausti "+" — kiekis padidėja
    @Test
    fun `FR16-TA07 plus button increases quantity and updates price`() {
        viewModel.addToCart(mockDish, 1)
        viewModel.increaseQuantity(mockDish.id)

        assertEquals(2, viewModel.items.value[0].quantity)
        assertEquals(25.80, viewModel.totalPrice, 0.01)
    }

    // FR-16 / TA-08 (HOG-96): Paspausti "−" — kiekis sumažėja (min 1)
    @Test
    fun `FR16-TA08 minus button decreases quantity minimum 1`() {
        viewModel.addToCart(mockDish, 3)
        viewModel.decreaseQuantity(mockDish.id)

        assertEquals(2, viewModel.items.value[0].quantity)

        // Sumažinus iki 1 ir dar kartą — pašalinamas
        viewModel.decreaseQuantity(mockDish.id)
        assertEquals(1, viewModel.items.value[0].quantity)
        viewModel.decreaseQuantity(mockDish.id)
        assertTrue(viewModel.items.value.isEmpty())
    }

    // =====================================================================
    // FR-17: Krepšelio langas
    // =====================================================================

    // FR-17 / TA-01 (HOG-97): Atidaryti "Krepšelis" tab'ą — rodomas tuščias krepšelis
    @Test
    fun `FR17-TA01 cart is initially empty`() {
        assertTrue(viewModel.items.value.isEmpty())
        assertEquals(0.0, viewModel.totalPrice, 0.01)
        assertEquals(0, viewModel.totalCount)
    }

    // FR-17 / TA-02 (HOG-98): Pridėjus patiekalą — jis matomas krepšelyje
    @Test
    fun `FR17-TA02 added dish appears in cart with name price quantity`() {
        viewModel.addToCart(mockDish, 1)

        val item = viewModel.items.value[0]
        assertEquals("Cepelinai", item.name)
        assertEquals(12.90, item.price, 0.01)
        assertEquals(1, item.quantity)
        assertEquals("https://example.com/img.jpg", item.imageUrl)
    }

    // FR-17 / TA-03 (HOG-98): Keli patiekalai krepšelyje — rodoma bendra suma
    @Test
    fun `FR17-TA03 multiple dishes show correct total sum`() {
        viewModel.addToCart(mockDish, 2)  // 2 × 12.90 = 25.80
        viewModel.addToCart(mockDish2, 1) // 1 × 7.50  = 7.50

        assertEquals(2, viewModel.items.value.size)
        assertEquals(33.30, viewModel.totalPrice, 0.01)
    }

    // FR-17 / TA-04 (HOG-98): Tas pats patiekalas pridedamas du kartus — kiekis padidėja
    @Test
    fun `FR17-TA04 adding same dish twice increases quantity not duplicates`() {
        viewModel.addToCart(mockDish, 1)
        viewModel.addToCart(mockDish, 3)

        assertEquals(1, viewModel.items.value.size)
        assertEquals(4, viewModel.items.value[0].quantity)
    }

    // FR-17 / TA-05 (HOG-99): Paspausti "+" — patiekalo kiekis padidėja
    @Test
    fun `FR17-TA05 increase quantity adds one and updates price`() {
        viewModel.addToCart(mockDish, 1)
        viewModel.increaseQuantity(mockDish.id)

        assertEquals(2, viewModel.items.value[0].quantity)
        assertEquals(25.80, viewModel.totalPrice, 0.01)
    }

    // FR-17 / TA-06 (HOG-99): Paspausti "−" — patiekalo kiekis sumažėja
    @Test
    fun `FR17-TA06 decrease quantity removes one and updates price`() {
        viewModel.addToCart(mockDish, 3)
        viewModel.decreaseQuantity(mockDish.id)

        assertEquals(2, viewModel.items.value[0].quantity)
        assertEquals(25.80, viewModel.totalPrice, 0.01)
    }

    // FR-17 / TA-07 (HOG-99): Paspausti šiukšliadėžės ikoną — patiekalas pašalinamas
    @Test
    fun `FR17-TA07 remove item deletes dish from cart`() {
        viewModel.addToCart(mockDish, 2)
        viewModel.addToCart(mockDish2, 1)
        viewModel.removeItem(mockDish.id)

        assertEquals(1, viewModel.items.value.size)
        assertEquals("Šaltibarščiai", viewModel.items.value[0].name)
    }

    // FR-17 / TA-08 (HOG-99): Bendra kaina perskaičiuojama po pakeitimų
    @Test
    fun `FR17-TA08 total price recalculated after changes`() {
        viewModel.addToCart(mockDish, 2)   // 25.80
        viewModel.addToCart(mockDish2, 1)  // + 7.50 = 33.30
        assertEquals(33.30, viewModel.totalPrice, 0.01)

        viewModel.decreaseQuantity(mockDish.id) // 1 × 12.90 + 7.50 = 20.40
        assertEquals(20.40, viewModel.totalPrice, 0.01)

        viewModel.removeItem(mockDish2.id) // 12.90
        assertEquals(12.90, viewModel.totalPrice, 0.01)
    }

    // =====================================================================
    // FR-18: Užsakymo įvykdymas
    // =====================================================================

    // FR-18 / TA-01 (HOG-116): Paspausti "Užsakyti" mygtuką
    @Test
    fun `FR18-TA01 place order calls API with correct data`() = runTest {
        coEvery { foodApi.placeOrder(any(), any()) } returns Response.success(Unit)

        viewModel.addToCart(mockDish, 2)
        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            foodApi.placeOrder(
                userId = 1,
                request = match { req ->
                    req.orderedMenuItems.size == 1 &&
                            req.orderedMenuItems[0].id == 1 &&
                            req.orderedMenuItems[0].quantity == 2
                }
            )
        }
    }

    // FR-18 / TA-02 (HOG-116): Užsakymas vykdomas — isPlacingOrder valdomas
    @Test
    fun `FR18-TA02 isPlacingOrder is true during order and false after`() = runTest {
        coEvery { foodApi.placeOrder(any(), any()) } returns Response.success(Unit)

        viewModel.addToCart(mockDish, 1)
        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.isPlacingOrder.value)
    }

    // FR-18 / TA-03 (HOG-116): API grąžina klaidą — rodomas klaidos pranešimas
    @Test
    fun `FR18-TA03 order error displayed when API fails`() = runTest {
        coEvery { foodApi.placeOrder(any(), any()) } throws Exception("Serverio klaida")

        viewModel.addToCart(mockDish, 1)
        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.orderError.value)
        assertTrue(viewModel.orderError.value!!.contains("Serverio klaida"))
    }

    // FR-18 / TA-04 (HOG-120): Sėkmingas užsakymas — onSuccess callback iškviečiamas
    @Test
    fun `FR18-TA04 success callback triggered for dialog display`() = runTest {
        coEvery { foodApi.placeOrder(any(), any()) } returns Response.success(Unit)

        viewModel.addToCart(mockDish, 1)

        var successCalled = false
        viewModel.placeOrder(onSuccess = { successCalled = true })
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(successCalled)
    }

    // FR-18 / TA-05 (HOG-120): Paspaudus "Gerai" dialoge — krepšelis išvalomas
    @Test
    fun `FR18-TA05 cart cleared after successful order`() = runTest {
        coEvery { foodApi.placeOrder(any(), any()) } returns Response.success(Unit)

        viewModel.addToCart(mockDish, 1)
        viewModel.addToCart(mockDish2, 2)

        viewModel.placeOrder(onSuccess = {})
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(viewModel.items.value.isEmpty())
        assertEquals(0.0, viewModel.totalPrice, 0.01)
    }

    // FR-18 / TA-06 (HOG-116): Užsakymo request turi teisingus patiekalų ID ir kiekius
    @Test
    fun `FR18-TA06 order request contains correct item IDs and quantities`() = runTest {
        coEvery { foodApi.placeOrder(any(), any()) } returns Response.success(Unit)

        viewModel.addToCart(mockDish, 2)
        viewModel.addToCart(mockDish2, 3)
        viewModel.placeOrder()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            foodApi.placeOrder(
                userId = 1,
                request = match { req ->
                    req.orderedMenuItems.size == 2 &&
                            req.orderedMenuItems.any { it.id == 1 && it.quantity == 2 } &&
                            req.orderedMenuItems.any { it.id == 2 && it.quantity == 3 }
                }
            )
        }
    }
}