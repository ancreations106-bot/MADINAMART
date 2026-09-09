package com.madinamart.app.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Categories : Screen("categories")
    object Search : Screen("search")
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: String) = "order_confirmation/$orderId"
    }
    object Orders : Screen("orders")
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: String) = "order_tracking/$orderId"
    }
    object NoticeBoard : Screen("notice_board")
    object Auth : Screen("auth")
    object Profile : Screen("profile")
    object SavedAddress : Screen("saved_address")

    // Admin Routes
    object AdminLogin : Screen("admin_login")
    object AdminDashboard : Screen("admin_dashboard")
    object AdminOrders : Screen("admin_orders")
    object AdminNoticeBoard : Screen("admin_notice_board")
    object AdminFeeSettings : Screen("admin_fee_settings")
    object AdminPasswordChange : Screen("admin_password_change")
    object AdminCustomers : Screen("admin_customers")
}
