package lt.hogfood.hogfood.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import lt.hogfood.hogfood.ui.home.getCategoryColor
import lt.hogfood.hogfood.ui.theme.CardBackground
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextPrimary
import lt.hogfood.hogfood.ui.theme.TextSecondary
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    onDishClick: (Int) -> Unit = {},
    viewModel: SearchViewModel = viewModel()
) {
    val results by viewModel.results.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedDiet by viewModel.selectedDiet.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val dietaryTags by viewModel.dietaryTags.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.refresh() }
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pullRefresh(pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(16.dp))
                    Text("Paieška", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.query.value = it },
                        placeholder = { Text("Ieškoti patiekalų...", color = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = CardBackground,
                            focusedContainerColor = CardBackground,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = PrimaryBlue
                        ),
                        singleLine = true
                    )
                    Spacer(Modifier.height(16.dp))
                    Text("MITYBOS FILTRAI", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(dietaryTags) { diet ->
                        FilterChip(label = diet.title, selected = selectedDiet?.id == diet.id, onClick = {
                            viewModel.setDiet(if (selectedDiet?.id == diet.id) null else diet)
                        })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("KATEGORIJOS", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                }
            }

            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        FilterChip(label = cat.title, selected = selectedCategory?.id == cat.id, onClick = {
                            viewModel.setCategory(if (selectedCategory?.id == cat.id) null else cat)
                        })
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            item {
                Text("${results.size} rezultatai", fontSize = 13.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(12.dp))
            }

            when {
                isLoading -> item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
                results.isEmpty() -> item {
                    Text("Nieko nerasta", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                }
                else -> items(results) { dish ->
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DishCardHorizontal(
                            name = dish.name,
                            restaurant = dish.restaurantName,
                            price = "€%.2f".format(dish.price),
                            categoryColor = getCategoryColor(dish.name),
                            imageUrl = dish.imageUrl,
                            onClick = { onDishClick(dish.id) }
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(50.dp), color = if (selected) PrimaryBlue else Color(0xFFE6F1FB), modifier = Modifier.clickable { onClick() }) {
        Text(text = label, fontSize = 12.sp, color = if (selected) Color.White else TextSecondary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
    }
}

@Composable
fun DishCardHorizontal(name: String, restaurant: String, price: String, categoryColor: Color, imageUrl: String?, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = CardBackground, modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(72.dp).background(categoryColor, RoundedCornerShape(12.dp))
            ) {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(restaurant, fontSize = 12.sp, color = TextSecondary)
            }
            Text(price, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
    }
}