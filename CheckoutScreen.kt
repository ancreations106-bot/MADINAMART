package com.madinamart.app.ui.screens.customer

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun CheckoutScreen(
    viewModel: StoreViewModel,
    onOrderPlaced: (String) -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val context = LocalContext.current
    val currentCustomer by viewModel.currentCustomer.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val isLocationLoading by viewModel.isLocationLoading.collectAsState()
    val locationError by viewModel.locationError.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartTotal.collectAsState()
    val feeSettings by viewModel.feeSettings.collectAsState()

    // Redirect if not logged in
    LaunchedEffect(currentCustomer) {
        if (currentCustomer == null) {
            onNavigateToAuth()
        }
    }

    var customerName by remember(currentCustomer) { mutableStateOf(currentCustomer?.name ?: "") }
    var contactNumber by remember(currentCustomer) { mutableStateOf(currentCustomer?.mobile ?: "") }
    var fullAddress by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPlacingOrder by remember { mutableStateOf(false) }

    val platformFee = feeSettings.platformFee
    val deliveryFee = feeSettings.deliveryFee
    val finalTotal = subtotal + platformFee + deliveryFee

    // Location Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            viewModel.requestLiveLocation(
                onSuccess = { loc ->
                    if (fullAddress.isBlank() && loc.addressText.isNotBlank()) {
                        fullAddress = loc.addressText
                    }
                },
                onError = { /* handled by state */ }
            )
        } else {
            errorMessage = "Location permission was denied. You can still enter your delivery address manually below."
        }
    }

    fun handleShareLocationClick() {
        val fineCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarseCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (fineCheck == PackageManager.PERMISSION_GRANTED || coarseCheck == PackageManager.PERMISSION_GRANTED) {
            viewModel.requestLiveLocation(
                onSuccess = { loc ->
                    if (fullAddress.isBlank() && loc.addressText.isNotBlank()) {
                        fullAddress = loc.addressText
                    }
                },
                onError = { /* error state updated */ }
            )
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        bottomBar = {
            Surface(
                color = White,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Neutral200)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Payable (COD)", style = MaterialTheme.typography.labelSmall, color = Neutral500)
                            Text(
                                text = "₹${finalTotal.toInt()}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Emerald800
                            )
                        }
                        Button(
                            onClick = {
                                errorMessage = null
                                if (customerName.trim().length < 2) {
                                    errorMessage = "Please enter your full name."
                                    return@Button
                                }
                                val cleanPhone = contactNumber.replace(Regex("\\D"), "")
                                if (!cleanPhone.matches(Regex("^[6-9]\\d{9}$"))) {
                                    errorMessage = "Please enter a valid 10-digit Indian mobile number."
                                    return@Button
                                }
                                if (fullAddress.trim().length < 5) {
                                    errorMessage = "Please provide your full delivery address."
                                    return@Button
                                }
                                if (landmark.trim().length < 2) {
                                    errorMessage = "Please provide a landmark for delivery."
                                    return@Button
                                }

                                isPlacingOrder = true
                                val result = viewModel.placeOrder(
                                    customerName = customerName,
                                    contactNumber = cleanPhone,
                                    fullAddress = fullAddress,
                                    landmark = landmark
                                )
                                isPlacingOrder = false

                                result.onSuccess { order ->
                                    onOrderPlaced(order.id)
                                }.onFailure { ex ->
                                    errorMessage = ex.localizedMessage ?: "Failed to place order."
                                }
                            },
                            enabled = !isPlacingOrder && cartItems.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            if (isPlacingOrder) {
                                CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp))
                            } else {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Place Order (Cash on Delivery)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Neutral50)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Text(
                text = "Delivery & Contact Details",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Neutral900
            )

            if (errorMessage != null) {
                Surface(
                    color = Rose50,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Rose500)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Rose600)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Rose600,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Customer Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "1. Customer Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Customer Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Emerald700) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = contactNumber,
                        onValueChange = {
                            if (it.length <= 10) contactNumber = it.filter { ch -> ch.isDigit() }
                        },
                        label = { Text("Contact Mobile Number *") },
                        placeholder = { Text("10-digit Indian Mobile") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Emerald700) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }

            // Live Location Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "2. Live GPS Location",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    Text(
                        text = "Share your device's live location to ensure our delivery executive navigates directly to your doorstep.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral600
                    )

                    Button(
                        onClick = { handleShareLocationClick() },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLocationLoading) {
                            CircularProgressIndicator(color = White, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Reading Real Device GPS...")
                        } else {
                            Text(
                                text = if (userLocation != null) "📍 Update Live Location" else "📍 Share Live Location",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (userLocation != null) {
                        Surface(
                            color = Emerald50,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald600),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald700, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Live Location Shared Successfully",
                                        fontWeight = FontWeight.Bold,
                                        color = Emerald900,
                                        fontSize = 13.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                userLocation?.let { loc ->
                                    Text(
                                        text = "GPS: %.5f, %.5f (Accuracy: ±%.1f m)".format(
                                            loc.latitude,
                                            loc.longitude,
                                            loc.accuracy
                                        ),
                                        fontSize = 11.sp,
                                        color = Emerald800
                                    )
                                    if (loc.addressText.isNotBlank()) {
                                        Text(
                                            text = "Detected Area: ${loc.addressText}",
                                            fontSize = 11.sp,
                                            color = Neutral600
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (locationError != null) {
                        Text(
                            text = locationError ?: "",
                            color = Rose600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Address Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "3. Delivery Address",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    OutlinedTextField(
                        value = fullAddress,
                        onValueChange = { fullAddress = it },
                        label = { Text("Full Delivery Address *") },
                        placeholder = { Text("House/Flat No., Street, Colony, City") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Emerald700) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = landmark,
                        onValueChange = { landmark = it },
                        label = { Text("Landmark *") },
                        placeholder = { Text("Near Mosque, School, Market or Water Tank") },
                        leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = Emerald700) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }

            // Order Summary Bill Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal (${cartItems.size} items)", color = Neutral600, fontSize = 13.sp)
                        Text("₹${subtotal.toInt()}", fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Platform Fee", color = Neutral600, fontSize = 13.sp)
                        Text("₹${platformFee.toInt()}", fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Fee", color = Neutral600, fontSize = 13.sp)
                        Text(if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}", fontWeight = FontWeight.Bold, color = Emerald700, fontSize = 13.sp)
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Neutral200)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Final Total", fontWeight = FontWeight.Bold, color = Neutral900)
                        Text("₹${finalTotal.toInt()}", fontWeight = FontWeight.Black, color = Emerald800, fontSize = 16.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
