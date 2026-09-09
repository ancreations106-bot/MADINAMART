package com.madinamart.app.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.madinamart.app.data.model.*
import com.madinamart.app.data.repository.StoreRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

class StoreViewModel(
    private val repository: StoreRepository,
    private val appContext: Context
) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(appContext)

    // Products & Search
    val products = repository.products
    val categories = repository.categories

    private val _selectedCategory = MutableStateFlow("All Items")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val filteredProducts: StateFlow<List<Product>> = combine(
        _selectedCategory,
        _searchQuery
    ) { category, query ->
        products.filter { p ->
            val matchCat = category == "All Items" || p.category.equals(category, ignoreCase = true)
            val matchQuery = query.isBlank() ||
                    p.name.contains(query, ignoreCase = true) ||
                    p.description.contains(query, ignoreCase = true) ||
                    p.tags.any { it.contains(query, ignoreCase = true) }
            matchCat && matchQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, products)

    // Cart
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
    val cartTotal: StateFlow<Double> = cartItems.map { list ->
        list.sumOf { it.product.price * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0.0)

    val cartCount: StateFlow<Int> = cartItems.map { list ->
        list.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    // Customer & Isolation
    val currentCustomer: StateFlow<Customer?> = repository.currentCustomer
    val allCustomers: List<Customer>
        get() = repository.getAllCustomers()

    val customerOrders: StateFlow<List<Order>> = combine(
        repository.orders,
        repository.currentCustomer
    ) { allOrders, customer ->
        if (customer == null) emptyList()
        else allOrders.filter { it.customerId == customer.id }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Admin
    val isAdminLoggedIn: StateFlow<Boolean> = repository.isAdminLoggedIn
    val allOrders: StateFlow<List<Order>> = repository.orders
    val notices: StateFlow<List<Notice>> = repository.notices
    val feeSettings: StateFlow<FeeSettings> = repository.feeSettings

    val activeCustomerNotices: StateFlow<List<Notice>> = notices.map { list ->
        list.filter { it.active }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Location State
    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation.asStateFlow()

    private val _isLocationLoading = MutableStateFlow(false)
    val isLocationLoading: StateFlow<Boolean> = _isLocationLoading.asStateFlow()

    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()

    // --- Search & Filter Actions ---
    fun selectCategory(cat: String) {
        _selectedCategory.value = cat
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // --- Cart Actions ---
    fun addToCart(product: Product, quantity: Int = 1) {
        repository.addToCart(product, quantity)
    }

    fun updateCartQuantity(productId: String, quantity: Int) {
        repository.updateCartQuantity(productId, quantity)
    }

    fun clearCart() {
        repository.clearCart()
    }

    // --- Customer Authentication Actions ---
    fun registerCustomer(name: String, mobile: String, password: String): Result<Customer> {
        return repository.registerCustomer(name, mobile, password)
    }

    fun loginCustomer(mobile: String, password: String): Result<Customer> {
        return repository.loginCustomer(mobile, password)
    }

    fun logoutCustomer() {
        repository.logoutCustomer()
    }

    // --- Admin Actions ---
    fun loginAdmin(password: String): Boolean {
        return repository.loginAdmin(password)
    }

    fun logoutAdmin() {
        repository.logoutAdmin()
    }

    fun changeAdminPassword(currentPass: String, newPass: String, confirmPass: String): Result<Unit> {
        return repository.changeAdminPassword(currentPass, newPass, confirmPass)
    }

    fun updateFeeSettings(platformFee: Double, deliveryFee: Double): Result<Unit> {
        return repository.updateFeeSettings(platformFee, deliveryFee)
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        repository.updateOrderStatus(orderId, newStatus)
    }

    // --- Notice Actions ---
    fun createNotice(title: String, description: String, imageUrl: String?): Result<Notice> {
        return repository.createNotice(title, description, imageUrl)
    }

    fun updateNotice(id: String, title: String, description: String, imageUrl: String?, active: Boolean): Result<Notice> {
        return repository.updateNotice(id, title, description, imageUrl, active)
    }

    fun deleteNotice(id: String) {
        repository.deleteNotice(id)
    }

    fun toggleNoticeStatus(id: String) {
        repository.toggleNoticeStatus(id)
    }

    // --- Real Device GPS Live Location ---
    @SuppressLint("MissingPermission")
    fun requestLiveLocation(onSuccess: (UserLocation) -> Unit, onError: (String) -> Unit) {
        _isLocationLoading.value = true
        _locationError.value = null

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { loc ->
                _isLocationLoading.value = false
                if (loc != null) {
                    var addressStr = ""
                    try {
                        val geocoder = Geocoder(appContext, Locale.getDefault())
                        @Suppress("DEPRECATION")
                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                        if (!addresses.isNullOrEmpty()) {
                            val addr = addresses[0]
                            val parts = listOfNotNull(
                                addr.subLocality ?: addr.locality,
                                addr.subAdminArea ?: addr.adminArea,
                                addr.postalCode
                            )
                            addressStr = parts.joinToString(", ")
                        }
                    } catch (_: Exception) {
                        addressStr = "Lat: %.4f, Lng: %.4f".format(loc.latitude, loc.longitude)
                    }

                    val userLoc = UserLocation(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        accuracy = loc.accuracy,
                        addressText = addressStr
                    )
                    _userLocation.value = userLoc
                    onSuccess(userLoc)
                } else {
                    val err = "Unable to fetch current GPS coordinates. Please ensure GPS/Location is enabled on your device."
                    _locationError.value = err
                    onError(err)
                }
            }
            .addOnFailureListener { ex ->
                _isLocationLoading.value = false
                val err = ex.localizedMessage ?: "Failed to retrieve device location."
                _locationError.value = err
                onError(err)
            }
    }

    fun clearLocation() {
        _userLocation.value = null
        _locationError.value = null
    }

    // --- Order Placement ---
    fun placeOrder(
        customerName: String,
        contactNumber: String,
        fullAddress: String,
        landmark: String
    ): Result<Order> {
        val loc = _userLocation.value
        return repository.placeOrder(
            customerName = customerName,
            contactNumber = contactNumber,
            fullAddress = fullAddress,
            landmark = landmark,
            latitude = loc?.latitude,
            longitude = loc?.longitude,
            locationAccuracy = loc?.accuracy,
            locationAddress = loc?.addressText,
            locationShared = loc != null
        )
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = context.applicationContext as MadinaMartApplication
            return StoreViewModel(app.repository, context.applicationContext) as T
        }
    }
}
