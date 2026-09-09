package com.madinamart.app.data.model

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Double,
    val originalPrice: Double,
    val costPrice: Double = price * 0.75,
    val unit: String,
    val description: String,
    val image: String,
    val inStock: Boolean = true,
    val stockQuantity: Int = 50,
    val rating: Double = 4.8,
    val reviewsCount: Int = 120,
    val isPopular: Boolean = false,
    val tags: List<String> = emptyList()
)

data class Category(
    val id: String,
    val name: String,
    val iconName: String
)

data class Customer(
    val id: String,
    val name: String,
    val mobile: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

data class Address(
    val id: String,
    val customerId: String,
    val name: String,
    val mobile: String,
    val houseFlat: String,
    val streetArea: String,
    val city: String,
    val state: String = "Local Area",
    val pincode: String,
    val landmark: String = "",
    val tag: String = "Home",
    val isDefault: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAccuracy: Float? = null,
    val locationShared: Boolean = false,
    val locationAddress: String? = null
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val productImage: String,
    val price: Double,
    val costPrice: Double,
    val quantity: Int,
    val unit: String = "1 pc"
)

enum class OrderStatus(val displayName: String) {
    ORDER_PLACED("Order Placed"),
    ORDER_CONFIRMED("Order Confirmed"),
    PACKED("Packed"),
    SHIPPED("Shipped"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}

data class Order(
    val id: String,
    val customerId: String,
    val customerName: String,
    val contactNumber: String,
    val fullAddress: String,
    val landmark: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationAccuracy: Float? = null,
    val locationAddress: String? = null,
    val locationShared: Boolean = false,
    val items: List<OrderItem>,
    val subtotal: Double,
    val platformFee: Double,
    val deliveryFee: Double,
    val finalTotal: Double,
    val orderDate: String,
    val orderStatus: OrderStatus = OrderStatus.ORDER_PLACED,
    val paymentMethod: String = "Cash on Delivery",
    val estimatedDelivery: String = "Today within 2-4 Hours",
    val createdAt: Long = System.currentTimeMillis()
)

data class CartItem(
    val product: Product,
    val quantity: Int
)

data class Notice(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class FeeSettings(
    val platformFee: Double = 5.0,
    val deliveryFee: Double = 0.0
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val addressText: String = ""
)
