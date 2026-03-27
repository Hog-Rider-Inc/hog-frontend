package lt.hogfood.hogfood.ui.swipe

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import lt.hogfood.hogfood.data.model.RecommendationItem
import lt.hogfood.hogfood.ui.home.getCategoryColor
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.HeartRed
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary

@Composable
fun SwipeScreen(viewModel: SwipeViewModel = viewModel()) {
    val item by viewModel.currentItem.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val finished by viewModel.finished.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Klaida: $error",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            finished || item == null -> {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎉", fontSize = 48.sp)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Peržiūrėti visi patiekalai!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Grįžk vėliau naujoms rekomendacijoms",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        Surface(
                            onClick = { viewModel.restart() },
                            shape = RoundedCornerShape(50.dp),
                            color = PrimaryBlue
                        ) {
                            Text(
                                "Pradėti iš naujo",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }

            else -> {
                SwipeCard(
                    item = item!!,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.height(24.dp))
                SwipeButtons(
                    onDislike = { viewModel.dislike() },
                    onLike = { viewModel.like() }
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SwipeCard(item: RecommendationItem, modifier: Modifier = Modifier) {
    val color = getCategoryColor(item.categories.firstOrNull())

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = CardBackground,
        shadowElevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Image / color area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(color, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                contentAlignment = Alignment.TopStart
            ) {
                // Match badge
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFFE6F1FB),
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        "✦ Rekomenduojama",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Info section
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    item.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Restoranas \"${item.restaurant_name}\"",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        "€${item.price}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Tags row
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item.categories.take(2).forEach { category ->
                        TagChip(label = category, color = color.copy(alpha = 0.15f), textColor = color)
                    }
                    item.dietary_tags.take(1).forEach { tag ->
                        TagChip(
                            label = tag,
                            color = Color(0xFFEAF3DE),
                            textColor = Color(0xFF3B6D11)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagChip(label: String, color: Color, textColor: Color) {
    Surface(shape = RoundedCornerShape(50.dp), color = color) {
        Text(
            label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SwipeButtons(onDislike: () -> Unit, onLike: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Dislike button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                onClick = onDislike,
                shape = CircleShape,
                color = Color(0xFFF5F5F3),
                shadowElevation = 4.dp,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Nepatinka",
                        tint = Color(0xFF888888),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Nepatinka", fontSize = 12.sp, color = TextSecondary)
        }

        // Like button
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                onClick = onLike,
                shape = CircleShape,
                color = Color(0xFFFFF0F0),
                shadowElevation = 4.dp,
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Patinka",
                        tint = HeartRed,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("Patinka", fontSize = 12.sp, color = HeartRed, fontWeight = FontWeight.SemiBold)
        }
    }
}