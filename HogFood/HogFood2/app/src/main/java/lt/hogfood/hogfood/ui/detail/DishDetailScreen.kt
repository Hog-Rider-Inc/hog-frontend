package lt.hogfood.hogfood.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import lt.hogfood.hogfood.ui.home.getCategoryColor
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary

@Composable
fun DishDetailScreen(
    dishId: Int,
    onBack: () -> Unit,
    viewModel: DishDetailViewModel = viewModel()
) {
    LaunchedEffect(dishId) {
        viewModel.loadDish(dishId)
    }

    val dish by viewModel.dish.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Klaida: $error", color = Color.Red)
                }
            }

            dish != null -> {
                val d = dish!!
                val color = getCategoryColor(d.categories.firstOrNull())
                val imageUrl = d.images.firstOrNull()

                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                    // Viršutinė zona — nuotrauka arba spalvota
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        if (imageUrl != null) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = d.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            // Tamsus overlay kad mygtukas matytųsi
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.15f))
                            )
                        } else {
                            // Jei nuotraukos nėra — spalvota zona
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                            )
                        }

                        // Atgal mygtukas
                        Surface(
                            onClick = onBack,
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Atgal",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Informacija
                    Column(modifier = Modifier.padding(16.dp)) {

                        // Pavadinimas ir kaina
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                d.name,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "€%.2f".format(d.price),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            "Restoranas: ${d.restaurantName}",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )

                        Spacer(Modifier.height(16.dp))

                        // Kategorijos ir mitybos žymos
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            d.categories.forEach { category ->
                                Surface(shape = RoundedCornerShape(50.dp), color = color.copy(alpha = 0.15f)) {
                                    Text(category, fontSize = 12.sp, color = color, fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                }
                            }
                            d.dietaryTags.forEach { tag ->
                                Surface(shape = RoundedCornerShape(50.dp), color = Color(0xFFEAF3DE)) {
                                    Text(tag, fontSize = 12.sp, color = Color(0xFF3B6D11), fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                                }
                            }
                        }

                        // Aprašymas
                        if (d.description.isNotEmpty()) {
                            Spacer(Modifier.height(20.dp))
                            Text("Aprašymas", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            Surface(shape = RoundedCornerShape(12.dp), color = CardBackground, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    d.description,
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}