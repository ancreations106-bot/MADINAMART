package com.madinamart.app.ui.screens.admin

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.data.model.OrderStatus
import com.madinamart.app.theme.*
import com.madinamart.app.ui.components.OrderStatusBadge
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun AdminOrdersScreen(
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val orders by viewModel.allOrders.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders placed yet.", style = MaterialTheme.typography.titleMedium, color = Neutral500)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "All Store Orders (${orders.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                }

                items(orders) { order ->
                    var showStatusMenu by remember { mutableStateOf(false) }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Order ID & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Neutral900)
                                    Text(order.orderDate, fontSize = 11.sp, color = Neutral400)
                                }
                                Box {
                                    Surface(
                                        onClick = { showStatusMenu = true },
                                        shape = RoundedCornerShape(50)
                                    ) {
                                        OrderStatusBadge(status = order.orderStatus)
                                    }
                                    DropdownMenu(
                                        expanded = showStatusMenu,
                                        onDismissRequest = { showStatusMenu = false }
                                    ) {
                                        OrderStatus.values().forEach { status ->
                                            DropdownMenuItem(
                                                text = { Text(status.displayName) },
                                                onClick = {
                                                    viewModel.updateOrderStatus(order.id, status)
                                                    showStatusMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            Divider(color = Neutral200)

                            // Customer Details
                            Text("Customer Information:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Neutral900)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Name: ${order.customerName}", fontSize = 13.sp, color = Neutral800)
                                    Text("Mobile: ${order.contactNumber}", fontSize = 13.sp, color = Neutral800)
                                }
                                IconButton(onClick = {
                                    try {
                                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${order.contactNumber}"))
                                        context.startActivity(dialIntent)
                                    } catch (_: Exception) {}
                                }) {
                                    Icon(Icons.Default.Call, contentDescription = "Call Customer", tint = Emerald700)
                                }
                            }

                            // Delivery Address & Landmark
                            Text("Delivery Address:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Neutral900)
                            Text(order.fullAddress, fontSize = 12.sp, color = Neutral700)
                            if (order.landmark.isNotBlank()) {
                                Text("Landmark: ${order.landmark}", fontSize = 12.sp, color = Neutral600)
                            }

                            // Live GPS Coordinates Section
                            Surface(
                                color = if (order.locationShared) Emerald50 else Neutral100,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Place,
                                            contentDescription = null,
                                            tint = if (order.locationShared) Emerald700 else Neutral500,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (order.locationShared) "📍 Live GPS Shared by Customer" else "Manual Address Entry Only",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (order.locationShared) Emerald900 else Neutral600
                                        )
                                    }
                                    if (order.locationShared && order.latitude != null && order.longitude != null) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Latitude: ${order.latitude}, Longitude: ${order.longitude}",
                                            fontSize = 11.sp,
                                            color = Emerald800
                                        )
                                        if (order.locationAccuracy != null) {
                                            Text(
                                                text = "Accuracy: ±%.1f m".format(order.locationAccuracy),
                                                fontSize = 11.sp,
                                                color = Emerald800
                                            )
                                        }
                                        if (!order.locationAddress.isNullOrBlank()) {
                                            Text(
                                                text = "Detected Area: ${order.locationAddress}",
                                                fontSize = 11.sp,
                                                color = Neutral600
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        // Open Customer Location Button
                                        Button(
                                            onClick = {
                                                try {
                                                    val uri = Uri.parse("geo:${order.latitude},${order.longitude}?q=${order.latitude},${order.longitude}(Customer Location)")
                                                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                                    context.startActivity(mapIntent)
                                                } catch (_: Exception) {
                                                    try {
                                                        val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=${order.latitude},${order.longitude}")
                                                        val webIntent = Intent(Intent.ACTION_VIEW, webUri)
                                                        context.startActivity(webIntent)
                                                    } catch (_: Exception) {}
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                                            shape = RoundedCornerShape(6.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("📍 Open Customer Location in Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Items List
                            Text("Items Ordered (${order.items.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Neutral900)
                            order.items.forEach { item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("${item.quantity}x ${item.productName}", fontSize = 12.sp, color = Neutral700)
                                    Text("₹${(item.price * item.quantity).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Divider(color = Neutral200)

                            // Fee Breakdown & Total
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Platform Fee: ₹${order.platformFee.toInt()} | Delivery Fee: ₹${order.deliveryFee.toInt()}", fontSize = 11.sp, color = Neutral500)
                                Text("Total: ₹${order.finalTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = Emerald800)
                            }
                        }
                    }
                }
            }
        }
    }
}
