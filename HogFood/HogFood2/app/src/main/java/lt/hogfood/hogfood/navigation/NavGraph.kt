package lt.hogfood.hogfood.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lt.hogfood.hogfood.ui.cart.CartViewModel
import lt.hogfood.hogfood.ui.detail.DishDetailScreen
import lt.hogfood.hogfood.ui.history.OrderHistoryScreen
import lt.hogfood.hogfood.ui.home.HomeScreen
import lt.hogfood.hogfood.ui.search.SearchScreen
import lt.hogfood.hogfood.ui.swipe.SwipeScreen
import lt.hogfood.hogfood.ui.theme.PrimaryBlue
import lt.hogfood.hogfood.ui.theme.TextSecondary

data class NavItem(val route: String, val label: String, val icon: String)

val navItems = listOf(
    NavItem("home", "Pradžia", "⌂"),
    NavItem("search", "Paieška", "⌕"),
    NavItem("history", "Istorija", "⧗"),
    NavItem("recommend", "Tau patiks", "✦"),
)

val routesWithoutBottomBar = listOf("dish")

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = routesWithoutBottomBar.none { currentRoute?.startsWith(it) == true }
    val cartViewModel: CartViewModel = viewModel()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = Color.White) {
                    navItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Text(item.icon, color = if (currentRoute == item.route) PrimaryBlue else TextSecondary)
                            },
                            label = {
                                Text(item.label, color = if (currentRoute == item.route) PrimaryBlue else TextSecondary)
                            },
                            colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFE6F1FB))
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") {
                HomeScreen(
                    onSearchClick = { navController.navigate("search") },
                    onDishClick = { dishId -> navController.navigate("dish/$dishId") }
                )
            }
            composable("search") {
                SearchScreen(onDishClick = { dishId -> navController.navigate("dish/$dishId") })
            }
            composable("history") {
                OrderHistoryScreen()
            }
            composable("recommend") {
                SwipeScreen()
            }
            composable("dish/{dishId}") { backStackEntry ->
                val dishId = backStackEntry.arguments?.getString("dishId")?.toIntOrNull() ?: 0
                DishDetailScreen(
                    dishId = dishId,
                    onBack = { navController.popBackStack() },
                    cartViewModel = cartViewModel
                )
            }
        }
    }
}