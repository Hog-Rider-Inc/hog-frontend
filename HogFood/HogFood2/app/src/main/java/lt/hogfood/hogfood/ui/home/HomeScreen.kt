package lt.hogfood.hogfood.ui.home

import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import lt.hogfood.hogfood.data.model.FoodItem
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary

fun getCategoryColor(category: String?): Color {
    return when {
        category == null -> Color(0xFF7F77DD)
        category.lowercase().contains("sriub") -> Color(0xFF1D9E75)
        category.lowercase().contains("pic") -> Color(0xFF185FA5)
        category.lowercase().contains("desert") || category.lowercase().contains("tort") || category.lowercase().contains("pyrag") -> Color(0xFFD4537E)
        category.lowercase().contains("burger") || category.lowercase().contains("mėsain") -> Color(0xFFD85A30)
        category.lowercase().contains("užkand") || category.lowercase().contains("salotos") -> Color(0xFFBA7517)
        category.lowercase().contains("suši") || category.lowercase().contains("rolin") || category.lowercase().contains("nigiri") -> Color(0xFF2196A6)
        else -> Color(0xFF7F77DD)
    }
}

fun fixGithubUrl(url: String?): String? {
    return url
        ?.replace("github.com", "raw.githubusercontent.com")
        ?.replace("/blob/", "/")
}

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit = {},
    onDishClick: (Int) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val foodItems by viewModel.foodItems.collectAsState()
    val recommendations by viewModel.recommendations.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Box(
                modifier = Modifier.fillMaxWidth().statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = "Ready to eat like a Hog?", style = MaterialTheme.typography.headlineMedium, color = PrimaryBlue)
            }
        }

        if (isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            return@LazyColumn
        }

        if (error != null) {
            item { Text("Klaida: $error", color = Color.Red, modifier = Modifier.padding(16.dp)) }
            return@LazyColumn
        }

        if (recommendations.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✦  Tau gali patikti", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Spacer(Modifier.height(12.dp))
            }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(recommendations.take(3)) { rec -> RecommendationCardSimple(rec) }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        item {
            Text("Visi patiekalai", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))
        }

        items(foodItems) { item ->
            FoodCardSimple(item = item, onClick = { onDishClick(item.id) })
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
fun RecommendationCardSimple(rec: RecommendationItem) {
    val color = getCategoryColor(rec.categories.firstOrNull())
    Surface(shape = RoundedCornerShape(16.dp), color = CardBackground, modifier = Modifier.width(160.dp)) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp)
                    .background(color, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentAlignment = Alignment.TopStart
            ) {
                val imageUrl = fixGithubUrl(rec.image_url)
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = rec.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Column(modifier = Modifier.padding(10.dp)) {
                Text(rec.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, maxLines = 2, lineHeight = 16.sp)
                Text(rec.restaurant_name, fontSize = 11.sp, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text("€${rec.price}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun FoodCardSimple(item: FoodItem, onClick: () -> Unit = {}) {
    val color = getCategoryColor(item.categories.firstOrNull()?.title)
    val imageUrl = fixGithubUrl(item.imageUrl)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(item.restaurantName, fontSize = 12.sp, color = TextSecondary)
                if (item.dietaryTags.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Surface(shape = RoundedCornerShape(50.dp), color = Color(0xFFEAF3DE)) {
                        Text(item.dietaryTags.first().title, fontSize = 10.sp, color = Color(0xFF3B6D11),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Text("€%.2f".format(item.price), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}