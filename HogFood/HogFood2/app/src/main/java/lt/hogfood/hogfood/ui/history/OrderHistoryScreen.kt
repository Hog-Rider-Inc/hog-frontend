package lt.hogfood.hogfood.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import lt.hogfood.hogfood.data.model.Order
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun OrderHistoryScreen(
    viewModel: OrderHistoryViewModel = viewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadOrders()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { viewModel.loadOrders() },
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Užsakymų istorija",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }

            when {
                isLoading -> item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                error != null -> item {
                    Text(
                        "Klaida: $error",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                orders.isEmpty() -> item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⧗", fontSize = 48.sp, color = TextSecondary)
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "Užsakymų nėra",
                                fontSize = 16.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }
                else -> items(orders) { order ->
                    OrderCard(order = order)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: Order) {
    val statusColor = Color(0xFF1D9E75)
    val statusLabel = "COMPLETED"

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Viršutinė eilutė — numeris ir statusas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Užsakymas #${order.id}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        statusLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Data
            Text(
                formatDate(order.createdAt),
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(Modifier.height(8.dp))

            // Patiekalai
            order.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${item.quantity}x ${item.name}",
                        fontSize = 13.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "€%.2f".format(item.price * item.quantity),
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(Modifier.height(8.dp))

            // Bendra suma
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Viso",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    "€%.2f".format(order.totalPrice),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }
    }
}

fun formatDate(dateStr: String): String {
    return try {
        val parts = dateStr.split("T")
        val date = parts[0].split("-")
        "${date[2]}.${date[1]}.${date[0]}"
    } catch (_: Exception) {
        dateStr
    }
}