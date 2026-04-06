package lt.hogfood.hogfood.ui.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import lt.hogfood.hogfood.data.model.CartItem
import lt.hogfood.hogfood.ui.home.fixGithubUrl
import lt.hogfood.hogfood.ui.home.getCategoryColor
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary

@Composable
fun CartScreen(cartViewModel: CartViewModel) {
    val items by cartViewModel.items.collectAsState()
    val isPlacingOrder by cartViewModel.isPlacingOrder.collectAsState()
    val orderError by cartViewModel.orderError.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }


    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Surface(
                    onClick = {
                        showSuccessDialog = false
                        cartViewModel.clearCart()
                    },
                    shape = RoundedCornerShape(50.dp),
                    color = PrimaryBlue
                ) {
                    Text(
                        "Gerai",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 10.dp)
                    )
                }
            },
            title = {
                Text("Užsakymas pateiktas! 🎉", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Text(
                    "Jūsų užsakymas sėkmingai pateiktas. Netrukus pradėsime jį ruošti!",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text("Krepšelis", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            if (items.isNotEmpty()) {
                Text(
                    "${items.sumOf { it.quantity }} prekės",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }

        if (items.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛒", fontSize = 48.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Krepšelis tuščias", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(8.dp))
                    Text("Pridėk patiekalų iš meniu", fontSize = 14.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { item ->
                    CartItemRow(
                        item = item,
                        onIncrease = { cartViewModel.increaseQuantity(item.dishId) },
                        onDecrease = { cartViewModel.decreaseQuantity(item.dishId) },
                        onRemove = { cartViewModel.removeItem(item.dishId) }
                    )
                    HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(horizontal = 16.dp))
                }
            }

            // Apačios suma
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    // Summary card
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CardBackground,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Užsakymo suvestinė",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(12.dp))

                            val subtotal = cartViewModel.totalPrice
                            val delivery = 2.99
                            val serviceFee = 0.99
                            val total = subtotal + delivery + serviceFee

                            SummaryRow("Tarpinė suma", subtotal)
                            Spacer(Modifier.height(6.dp))
                            SummaryRow("Pristatymas", delivery)
                            Spacer(Modifier.height(6.dp))
                            SummaryRow("Aptarnavimo mokestis", serviceFee)
                            Spacer(Modifier.height(12.dp))

                            // Divider
                            androidx.compose.material3.HorizontalDivider(color = Color(0xFFE0E0E0))
                            Spacer(Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Viso:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("€%.2f".format(total), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (orderError != null) {
                        Text(
                            "Klaida: $orderError",
                            color = Color.Red,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Surface(
                        onClick = {
                            if (!isPlacingOrder) {
                                cartViewModel.placeOrder(onSuccess = { showSuccessDialog = true })
                            }
                        },
                        shape = RoundedCornerShape(50.dp),
                        color = if (isPlacingOrder) PrimaryBlue.copy(alpha = 0.6f) else PrimaryBlue,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (isPlacingOrder) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 10.dp).size(24.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Užsakyti · €%.2f".format(cartViewModel.totalPrice + 2.99 + 0.99),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val color = getCategoryColor(null)
    val imageUrl = fixGithubUrl(item.imageUrl)

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nuotrauka
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color)
        ) {
            if (imageUrl != null) {
                AsyncImage(model = imageUrl, contentDescription = item.name, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 2)
            Text(item.restaurantName, fontSize = 12.sp, color = TextSecondary)
            Spacer(Modifier.height(4.dp))
            Text("€%.2f".format(item.price * item.quantity), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }

        // Kiekio valdymas
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(onClick = { if (item.quantity > 1) onDecrease() }, shape = CircleShape, color = CardBackground, modifier = Modifier.size(30.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            }
            Text("${item.quantity}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Surface(onClick = onIncrease, shape = CircleShape, color = CardBackground, modifier = Modifier.size(30.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Add, contentDescription = "Pridėti", tint = TextPrimary, modifier = Modifier.size(16.dp))
                }
            }
            Spacer(Modifier.width(4.dp))
            Surface(onClick = onRemove, shape = CircleShape, color = Color(0xFFFFF0F0), modifier = Modifier.size(30.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Delete, contentDescription = "Ištrinti", tint = Color(0xFFE24B4A), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 13.sp, color = TextSecondary)
        Text("€%.2f".format(amount), fontSize = 13.sp, color = TextSecondary)
    }
}