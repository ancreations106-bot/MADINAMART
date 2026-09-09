package com.madinamart.app.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.madinamart.app.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

class StoreRepository(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("madina_mart_store_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // 1. Fee Settings
    private val _feeSettings = MutableStateFlow(loadFeeSettings())
    val feeSettings: StateFlow<FeeSettings> = _feeSettings.asStateFlow()

    // 2. Customers & Auth
    private val _currentCustomer = MutableStateFlow<Customer?>(loadCurrentCustomer())
    val currentCustomer: StateFlow<Customer?> = _currentCustomer.asStateFlow()

    // 3. Admin Auth State
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    // 4. Cart Items
    private val _cartItems = MutableStateFlow<List<CartItem>>(loadCart())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    // 5. Notices
    private val _notices = MutableStateFlow<List<Notice>>(loadNotices())
    val notices: StateFlow<List<Notice>> = _notices.asStateFlow()

    // 6. Orders
    private val _orders = MutableStateFlow<List<Order>>(loadOrders())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    // 7. Products Catalog
    val products: List<Product> = listOf(
        Product(
            id = "prod-groc-01",
            name = "Daawat Rozana Super Basmati Rice (5 kg)",
            category = "Grocery & Staples",
            price = 385.0,
            originalPrice = 475.0,
            costPrice = 290.0,
            unit = "5 kg Bag",
            description = "Rich aroma, long slender grains aged for perfection. Ideal for daily pulao, biryani, and steamed rice.",
            image = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=800&auto=format&fit=crop&q=80",
            isPopular = true,
            tags = listOf("Rice", "Staple", "Basmati")
        ),
        Product(
            id = "prod-groc-02",
            name = "Aashirvaad Shudh Chakki Atta (10 kg)",
            category = "Grocery & Staples",
            price = 430.0,
            originalPrice = 495.0,
            costPrice = 345.0,
            unit = "10 kg Bag",
            description = "100% pure whole wheat grain flour. Rotis stay soft and fluffy for hours.",
            image = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=800&auto=format&fit=crop&q=80",
            isPopular = true,
            tags = listOf("Atta", "Flour", "Wheat")
        ),
        Product(
            id = "prod-groc-03",
            name = "Tata Salt Vacuum Evaporated (1 kg)",
            category = "Grocery & Staples",
            price = 28.0,
            originalPrice = 30.0,
            costPrice = 21.0,
            unit = "1 kg Pack",
            description = "Desh Ka Namak. Pure, hygienic and iodine enriched table salt for everyday cooking.",
            image = "https://images.unsplash.com/photo-1518110925495-5fe2fda0442c?w=800&auto=format&fit=crop&q=80",
            tags = listOf("Salt", "Tata")
        ),
        Product(
            id = "prod-groc-04",
            name = "Fortune Sunlite Refined Sunflower Oil (1 L Pouch)",
            category = "Edible Oils & Ghee",
            price = 142.0,
            originalPrice = 175.0,
            costPrice = 110.0,
            unit = "1 L Pouch",
            description = "Light and healthy cooking oil enriched with Vitamin A & D. Low absorption technology.",
            image = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=800&auto=format&fit=crop&q=80",
            isPopular = true,
            tags = listOf("Oil", "Sunflower", "Cooking")
        ),
        Product(
            id = "prod-groc-05",
            name = "Amul Pure Cow Ghee (1 L Tin)",
            category = "Edible Oils & Ghee",
            price = 615.0,
            originalPrice = 670.0,
            costPrice = 510.0,
            unit = "1 L Tin",
            description = "Granular texture with authentic traditional aroma. Made from fresh cow milk.",
            image = "https://images.unsplash.com/photo-1628088062854-d1870b4553da?w=800&auto=format&fit=crop&q=80",
            isPopular = true,
            tags = listOf("Ghee", "Amul", "Cow Ghee")
        ),
        Product(
            id = "prod-groc-06",
            name = "Everest Royal Garam Masala (100 g)",
            category = "Spices & Masalas",
            price = 92.0,
            originalPrice = 105.0,
            costPrice = 68.0,
            unit = "100 g Box",
            description = "Handpicked aromatic spices roasted and blended to add royal flavor to curries.",
            image = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=800&auto=format&fit=crop&q=80",
            tags = listOf("Spices", "Garam Masala")
        ),
        Product(
            id = "prod-groc-07",
            name = "Premium California Almonds (Badam 500 g)",
            category = "Dry Fruits & Nuts",
            price = 449.0,
            originalPrice = 580.0,
            costPrice = 330.0,
            unit = "500 g Pouch",
            description = "Crunchy, sweet, and nutrient-rich California almonds. Vacuum sealed freshness.",
            image = "https://images.unsplash.com/photo-1508061253366-f7da158b6d46?w=800&auto=format&fit=crop&q=80",
            isPopular = true,
            tags = listOf("Almonds", "Dry Fruits")
        ),
        Product(
            id = "prod-groc-08",
            name = "Surf Excel Easy Wash Detergent Powder (1 kg)",
            category = "Household & Cleaning",
            price = 145.0,
            originalPrice = 160.0,
            costPrice = 115.0,
            unit = "1 kg Pack",
            description = "Removes tough stains like tea, coffee, and ketchup easily without dulling colors.",
            image = "https://images.unsplash.com/photo-1585670210693-e7fdd16b142e?w=800&auto=format&fit=crop&q=80",
            tags = listOf("Detergent", "Cleaning")
        )
    )

    val categories: List<Category> = listOf(
        Category("cat-all", "All Items", "grid_view"),
        Category("Grocery & Staples", "Grocery & Staples", "shopping_basket"),
        Category("Edible Oils & Ghee", "Edible Oils & Ghee", "water_drop"),
        Category("Spices & Masalas", "Spices & Masalas", "local_fire_department"),
        Category("Dry Fruits & Nuts", "Dry Fruits & Nuts", "spa"),
        Category("Household & Cleaning", "Household & Cleaning", "cleaning_services")
    )

    // --- Customer Authentication & Isolation ---

    fun registerCustomer(name: String, mobile: String, password: String):Result<Customer> {
        val cleanMobile = mobile.replace(Regex("\\D"), "")
        if (!cleanMobile.matches(Regex("^[6-9]\\d{9}$"))) {
            return Result.failure(IllegalArgumentException("Please enter a valid 10-digit Indian mobile number (starting with 6-9)"))
        }
        if (name.trim().length < 2) {
            return Result.failure(IllegalArgumentException("Please enter your full name"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Password must be at least 6 characters"))
        }

        val customers = getAllCustomers().toMutableList()
        if (customers.any { it.mobile == cleanMobile }) {
            return Result.failure(IllegalArgumentException("An account with this mobile number already exists. Please log in."))
        }

        val newCustomer = Customer(
            id = "cust_" + UUID.randomUUID().toString().substring(0, 8),
            name = name.trim(),
            mobile = cleanMobile,
            passwordHash = sha256(password)
        )
        customers.add(newCustomer)
        saveAllCustomers(customers)
        setCurrentCustomer(newCustomer)
        return Result.success(newCustomer)
    }

    fun loginCustomer(mobile: String, password: String): Result<Customer> {
        val cleanMobile = mobile.replace(Regex("\\D"), "")
        val customers = getAllCustomers()
        val customer = customers.find { it.mobile == cleanMobile }
            ?: return Result.failure(IllegalArgumentException("No account found with this mobile number. Please register first."))

        if (customer.passwordHash != sha256(password)) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        setCurrentCustomer(customer)
        return Result.success(customer)
    }

    fun logoutCustomer() {
        _currentCustomer.value = null
        prefs.edit().remove("current_customer_id").apply()
    }

    private fun setCurrentCustomer(customer: Customer) {
        _currentCustomer.value = customer
        prefs.edit().putString("current_customer_id", customer.id).apply()
    }

    private fun loadCurrentCustomer(): Customer? {
        val savedId = prefs.getString("current_customer_id", null) ?: return null
        return getAllCustomers().find { it.id == savedId }
    }

    fun getAllCustomers(): List<Customer> {
        val json = prefs.getString("customers_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Customer>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveAllCustomers(list: List<Customer>) {
        val json = gson.toJson(list)
        prefs.edit().putString("customers_list", json).apply()
    }

    // --- Admin Authentication & Password Change ---

    fun loginAdmin(password: String): Boolean {
        val savedHash = prefs.getString("admin_password_hash", null) ?: sha256("Admin@Madina2026")
        if (sha256(password) == savedHash) {
            _isAdminLoggedIn.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun changeAdminPassword(currentPass: String, newPass: String, confirmPass: String): Result<Unit> {
        val savedHash = prefs.getString("admin_password_hash", null) ?: sha256("Admin@Madina2026")
        if (sha256(currentPass) != savedHash) {
            return Result.failure(IllegalArgumentException("Current password is incorrect."))
        }
        if (newPass.length < 6) {
            return Result.failure(IllegalArgumentException("New password must be at least 6 characters."))
        }
        if (newPass != confirmPass) {
            return Result.failure(IllegalArgumentException("New password and confirmation do not match."))
        }

        val newHash = sha256(newPass)
        prefs.edit().putString("admin_password_hash", newHash).apply()
        return Result.success(Unit)
    }

    // --- Fee Settings ---

    fun updateFeeSettings(platformFee: Double, deliveryFee: Double): Result<Unit> {
        if (platformFee < 0 || deliveryFee < 0) {
            return Result.failure(IllegalArgumentException("Fees must be non-negative values."))
        }
        val settings = FeeSettings(platformFee, deliveryFee)
        _feeSettings.value = settings
        prefs.edit()
            .putFloat("platform_fee", platformFee.toFloat())
            .putFloat("delivery_fee", deliveryFee.toFloat())
            .apply()
        return Result.success(Unit)
    }

    private fun loadFeeSettings(): FeeSettings {
        val p = prefs.getFloat("platform_fee", 5.0f).toDouble()
        val d = prefs.getFloat("delivery_fee", 0.0f).toDouble()
        return FeeSettings(p, d)
    }

    // --- Cart Management ---

    fun addToCart(product: Product, quantity: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val idx = current.indexOfFirst { it.product.id == product.id }
        if (idx >= 0) {
            val item = current[idx]
            current[idx] = item.copy(quantity = item.quantity + quantity)
        } else {
            current.add(CartItem(product, quantity))
        }
        _cartItems.value = current
        saveCart(current)
    }

    fun updateCartQuantity(productId: String, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        val idx = current.indexOfFirst { it.product.id == productId }
        if (idx >= 0) {
            if (newQuantity <= 0) {
                current.removeAt(idx)
            } else {
                current[idx] = current[idx].copy(quantity = newQuantity)
            }
            _cartItems.value = current
            saveCart(current)
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        saveCart(emptyList())
    }

    private fun saveCart(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString("cart_items", json).apply()
    }

    private fun loadCart(): List<CartItem> {
        val json = prefs.getString("cart_items", null) ?: return emptyList()
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    // --- Notice Board Management ---

    fun createNotice(title: String, description: String, imageUrl: String?): Result<Notice> {
        if (title.isBlank() || description.isBlank()) {
            return Result.failure(IllegalArgumentException("Notice title and description are required."))
        }
        val newNotice = Notice(
            id = "notice_" + UUID.randomUUID().toString().substring(0, 8),
            title = title.trim(),
            description = description.trim(),
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            active = true,
            createdAt = System.currentTimeMillis()
        )
        val list = _notices.value.toMutableList()
        list.add(0, newNotice)
        _notices.value = list
        saveNotices(list)
        return Result.success(newNotice)
    }

    fun updateNotice(id: String, title: String, description: String, imageUrl: String?, active: Boolean): Result<Notice> {
        val list = _notices.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx < 0) return Result.failure(IllegalArgumentException("Notice not found."))

        val updated = list[idx].copy(
            title = title.trim(),
            description = description.trim(),
            imageUrl = imageUrl?.takeIf { it.isNotBlank() },
            active = active
        )
        list[idx] = updated
        _notices.value = list
        saveNotices(list)
        return Result.success(updated)
    }

    fun deleteNotice(id: String) {
        val list = _notices.value.filter { it.id != id }
        _notices.value = list
        saveNotices(list)
    }

    fun toggleNoticeStatus(id: String) {
        val list = _notices.value.toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx >= 0) {
            list[idx] = list[idx].copy(active = !list[idx].active)
            _notices.value = list
            saveNotices(list)
        }
    }

    fun getActiveNoticesForCustomer(): List<Notice> {
        return _notices.value
            .filter { it.active }
            .sortedByDescending { it.createdAt }
    }

    private fun saveNotices(list: List<Notice>) {
        val json = gson.toJson(list)
        prefs.edit().putString("notices_list", json).apply()
    }

    private fun loadNotices(): List<Notice> {
        val json = prefs.getString("notices_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Notice>>() {}.type
            val loaded: List<Notice>? = gson.fromJson(json, type)
            if (!loaded.isNullOrEmpty()) return loaded
        }
        // Seed default notice
        val defaultNotice = Notice(
            id = "notice_welcome",
            title = "Welcome to Madina Mart!",
            description = "Order genuine groceries, edible oils & daily staples with Cash on Delivery and fast doorstep delivery.",
            imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=800&auto=format&fit=crop&q=80",
            active = true,
            createdAt = System.currentTimeMillis()
        )
        return listOf(defaultNotice)
    }

    // --- Orders Management & Data Isolation ---

    fun placeOrder(
        customerName: String,
        contactNumber: String,
        fullAddress: String,
        landmark: String,
        latitude: Double?,
        longitude: Double?,
        locationAccuracy: Float?,
        locationAddress: String?,
        locationShared: Boolean
    ): Result<Order> {
        val customer = _currentCustomer.value
            ?: return Result.failure(IllegalStateException("Customer must be logged in to place an order."))

        if (customerName.isBlank()) {
            return Result.failure(IllegalArgumentException("Customer name is required."))
        }
        val cleanMobile = contactNumber.replace(Regex("\\D"), "")
        if (!cleanMobile.matches(Regex("^[6-9]\\d{9}$"))) {
            return Result.failure(IllegalArgumentException("Please provide a valid 10-digit Indian contact number."))
        }
        if (fullAddress.isBlank()) {
            return Result.failure(IllegalArgumentException("Full delivery address is required."))
        }
        if (landmark.isBlank()) {
            return Result.failure(IllegalArgumentException("Landmark is required for easy delivery."))
        }

        val cart = _cartItems.value
        if (cart.isEmpty()) {
            return Result.failure(IllegalStateException("Your cart is empty."))
        }

        val orderItems = cart.map {
            OrderItem(
                productId = it.product.id,
                productName = it.product.name,
                productImage = it.product.image,
                price = it.product.price,
                costPrice = it.product.costPrice,
                quantity = it.quantity,
                unit = it.product.unit
            )
        }

        val subtotal = orderItems.sumOf { it.price * it.quantity }
        val currentFees = _feeSettings.value
        val platformFee = currentFees.platformFee
        val deliveryFee = currentFees.deliveryFee
        val finalTotal = subtotal + platformFee + deliveryFee

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val orderDate = sdf.format(Date())
        val orderId = "MM-2026-" + (10000 + Random().nextInt(90000))

        val newOrder = Order(
            id = orderId,
            customerId = customer.id,
            customerName = customerName.trim(),
            contactNumber = cleanMobile,
            fullAddress = fullAddress.trim(),
            landmark = landmark.trim(),
            latitude = latitude,
            longitude = longitude,
            locationAccuracy = locationAccuracy,
            locationAddress = locationAddress,
            locationShared = locationShared,
            items = orderItems,
            subtotal = subtotal,
            platformFee = platformFee,
            deliveryFee = deliveryFee,
            finalTotal = finalTotal,
            orderDate = orderDate,
            orderStatus = OrderStatus.ORDER_PLACED
        )

        val ordersList = _orders.value.toMutableList()
        ordersList.add(0, newOrder)
        _orders.value = ordersList
        saveOrders(ordersList)
        clearCart()

        return Result.success(newOrder)
    }

    /**
     * Customer Isolation: Customer A only sees their own orders
     */
    fun getOrdersForCurrentCustomer(): List<Order> {
        val customer = _currentCustomer.value ?: return emptyList()
        return _orders.value.filter { it.customerId == customer.id }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        val list = _orders.value.toMutableList()
        val idx = list.indexOfFirst { it.id == orderId }
        if (idx >= 0) {
            list[idx] = list[idx].copy(orderStatus = newStatus)
            _orders.value = list
            saveOrders(list)
        }
    }

    private fun saveOrders(list: List<Order>) {
        val json = gson.toJson(list)
        prefs.edit().putString("orders_list", json).apply()
    }

    private fun loadOrders(): List<Order> {
        val json = prefs.getString("orders_list", null) ?: return emptyList()
        val type = object : TypeToken<List<Order>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
