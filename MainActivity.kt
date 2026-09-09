package com.madinamart.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.madinamart.app.theme.Emerald700
import com.madinamart.app.theme.MadinaMartTheme
import com.madinamart.app.theme.White
import com.madinamart.app.ui.components.MadinaMartBottomBar
import com.madinamart.app.ui.components.MadinaMartTopBar
import com.madinamart.app.ui.navigation.Screen
import com.madinamart.app.ui.screens.admin.*
import com.madinamart.app.ui.screens.customer.*
import com.madinamart.app.viewmodel.StoreViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MadinaMartTheme {
                val navController = rememberNavController()
                MainAppContent(navController = navController, viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    navController: NavHostController,
    viewModel: StoreViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val cartCount by viewModel.cartItemCount.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    // Top-level destinations showing bottom nav bar
    val bottomBarRoutes = listOf(
        Screen.Home.route,
        Screen.Categories.route,
        Screen.Cart.route,
        Screen.Orders.route,
        Screen.Profile.route
    )
    val shouldShowBottomBar = currentRoute in bottomBarRoutes

    // Calculate Top Bar title & actions
    val isCustomerTopLevel = currentRoute == Screen.Home.route
    val screenTitle = when {
        currentRoute == Screen.Home.route -> "Madina Mart"
        currentRoute == Screen.Categories.route -> "Categories & Search"
        currentRoute == Screen.Cart.route -> "My Cart"
        currentRoute == Screen.Checkout.route -> "Checkout"
        currentRoute?.startsWith("order_confirmation/") == true -> "Order Confirmed"
        currentRoute == Screen.Orders.route -> "My Orders"
        currentRoute?.startsWith("order_tracking/") == true -> "Order Tracking"
        currentRoute == Screen.NoticeBoard.route -> "Notice Board"
        currentRoute == Screen.Auth.route -> "Customer Account"
        currentRoute == Screen.Profile.route -> "My Account"
        currentRoute?.startsWith("product/") == true -> "Product Details"
        currentRoute == Screen.AdminLogin.route -> "Admin Portal"
        currentRoute == Screen.AdminDashboard.route -> "Admin Dashboard"
        currentRoute == Screen.AdminOrders.route -> "Orders Management"
        currentRoute == Screen.AdminNoticeBoard.route -> "Notice Board Management"
        currentRoute == Screen.AdminFeeSettings.route -> "Fee Settings"
        currentRoute == Screen.AdminPasswordChange.route -> "Change Admin Password"
        currentRoute == Screen.AdminCustomers.route -> "Registered Customers"
        else -> "Madina Mart"
    }

    Scaffold(
        topBar = {
            if (isCustomerTopLevel) {
                MadinaMartTopBar(
                    onNoticeClick = { navController.navigate(Screen.NoticeBoard.route) },
                    onAdminClick = {
                        if (isAdminLoggedIn) {
                            navController.navigate(Screen.AdminDashboard.route)
                        } else {
                            navController.navigate(Screen.AdminLogin.route)
                        }
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = screenTitle,
                            fontWeight = FontWeight.Bold,
                            color = White
                        )
                    },
                    navigationIcon = {
                        if (!shouldShowBottomBar) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = White
                                )
                            }
                        }
                    },
                    actions = {
                        if (currentRoute == Screen.Profile.route) {
                            IconButton(onClick = {
                                if (isAdminLoggedIn) {
                                    navController.navigate(Screen.AdminDashboard.route)
                                } else {
                                    navController.navigate(Screen.AdminLogin.route)
                                }
                            }) {
                                Icon(Icons.Default.Security, contentDescription = "Admin Portal", tint = White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Emerald700)
                )
            }
        },
        bottomBar = {
            if (shouldShowBottomBar) {
                MadinaMartBottomBar(
                    navController = navController,
                    cartCount = cartCount
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Customer Routes
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    },
                    onNavigateToCategory = { category ->
                        viewModel.setSelectedCategory(category)
                        navController.navigate(Screen.Categories.route)
                    },
                    onNavigateToNotices = {
                        navController.navigate(Screen.NoticeBoard.route)
                    },
                    onNavigateToSearch = {
                        navController.navigate(Screen.Categories.route)
                    }
                )
            }

            composable(Screen.Categories.route) {
                CategoriesSearchScreen(
                    viewModel = viewModel,
                    onNavigateToProduct = { productId ->
                        navController.navigate(Screen.ProductDetail.createRoute(productId))
                    }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    viewModel = viewModel,
                    onNavigateToCheckout = {
                        navController.navigate(Screen.Checkout.route)
                    },
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route)
                    },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            composable(Screen.Checkout.route) {
                CheckoutScreen(
                    viewModel = viewModel,
                    onOrderPlaced = { orderId ->
                        navController.navigate(Screen.OrderConfirmation.createRoute(orderId)) {
                            popUpTo(Screen.Cart.route) { inclusive = true }
                        }
                    },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            composable(
                route = Screen.OrderConfirmation.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderConfirmationScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onTrackOrder = { id ->
                        navController.navigate(Screen.OrderTracking.createRoute(id))
                    },
                    onContinueShopping = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Orders.route) {
                OrdersScreen(
                    viewModel = viewModel,
                    onTrackOrder = { orderId ->
                        navController.navigate(Screen.OrderTracking.createRoute(orderId))
                    },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route)
                    },
                    onStartShopping = {
                        navController.navigate(Screen.Home.route)
                    }
                )
            }

            composable(
                route = Screen.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                OrderTrackingScreen(
                    orderId = orderId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.NoticeBoard.route) {
                NoticeBoardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = viewModel,
                    onAuthSuccess = {
                        navController.navigateUp()
                    }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = viewModel,
                    onNavigateToAuth = { navController.navigate(Screen.Auth.route) },
                    onNavigateToOrders = { navController.navigate(Screen.Orders.route) },
                    onNavigateToNotices = { navController.navigate(Screen.NoticeBoard.route) },
                    onNavigateToAdmin = {
                        if (isAdminLoggedIn) {
                            navController.navigate(Screen.AdminDashboard.route)
                        } else {
                            navController.navigate(Screen.AdminLogin.route)
                        }
                    }
                )
            }

            composable(
                route = Screen.ProductDetail.route,
                arguments = listOf(navArgument("productId") { type = NavType.StringType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getString("productId") ?: ""
                ProductDetailScreen(
                    productId = productId,
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToCart = { navController.navigate(Screen.Cart.route) }
                )
            }

            // Admin Routes
            composable(Screen.AdminLogin.route) {
                AdminLoginScreen(
                    viewModel = viewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.AdminLogin.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.AdminDashboard.route) {
                AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToOrders = { navController.navigate(Screen.AdminOrders.route) },
                    onNavigateToNotices = { navController.navigate(Screen.AdminNoticeBoard.route) },
                    onNavigateToFees = { navController.navigate(Screen.AdminFeeSettings.route) },
                    onNavigateToPassword = { navController.navigate(Screen.AdminPasswordChange.route) },
                    onNavigateToCustomers = { navController.navigate(Screen.AdminCustomers.route) },
                    onLogout = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.AdminDashboard.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.AdminOrders.route) {
                AdminOrdersScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.AdminNoticeBoard.route) {
                AdminNoticeBoardScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.AdminFeeSettings.route) {
                AdminFeeSettingsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.AdminPasswordChange.route) {
                AdminPasswordChangeScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }

            composable(Screen.AdminCustomers.route) {
                AdminCustomersScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
