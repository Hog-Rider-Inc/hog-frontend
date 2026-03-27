package lt.hogfood.hogfood.ui.home

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary

fun getCategoryColor(category: String?): Color {
    return when (category?.lowercase()) {
        "sriubos" -> Color(0xFF1D9E75)
        "karštieji", "karstieji" -> Color(0xFF7F77DD)
        "desertai" -> Color(0xFFD4537E)
        "užkandžiai", "uzkandziai" -> Color(0xFFBA7517)
        "burgeriai" -> Color(0xFFD85A30)
        "picos" -> Color(0xFF185FA5)
        else -> Color(0xFF7F77DD)
    }
}

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val foodItems by viewModel.foodItems.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Ko nori pavalgyti?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(12.dp))
                Surface(
                    onClick = onSearchClick,
                    shape = RoundedCornerShape(12.dp),
                    color = CardBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔍", fontSize = 14.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Ieškoti patiekalų...",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // Loading
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            return@LazyColumn
        }

        // Error
        if (error != null) {
            item {
                Text(
                    "Klaida: $error",
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@LazyColumn
        }

        // AI Rekomendacijos
        if (recommendations.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "✦  Tau gali patikti",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(recommendations.take(3)) { rec ->
                        RecommendationCardSimple(rec)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        // Patiekalų sąrašas
        item {
            Text(
                "Visi patiekalai",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        items(foodItems) { item ->
            FoodCardSimple(item)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecommendationCardSimple(rec: RecommendationItem) {
    val color = getCategoryColor(rec.categories.firstOrNull())
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        modifier = Modifier.width(160.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(color, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentAlignment = Alignment.TopStart
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFE6F1FB),
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        "✦ AI",
                        fontSize = 10.sp,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    rec.title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
                Text(rec.restaurant_name, fontSize = 11.sp, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text(
                    "€${rec.price}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun FoodCardSimple(item: FoodItem) {
    val color = getCategoryColor(item.categories.firstOrNull()?.title)
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(color, RoundedCornerShape(12.dp))
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(item.restaurantName, fontSize = 12.sp, color = TextSecondary)
                if (item.dietaryTags.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFFEAF3DE)
                    ) {
                        Text(
                            item.dietaryTags.first().title,
                            fontSize = 10.sp,
                            color = Color(0xFF3B6D11),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                "€%.2f".format(item.price),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
    }
}