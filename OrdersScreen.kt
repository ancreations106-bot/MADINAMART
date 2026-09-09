package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.theme.*
import com.madinamart.app.ui.components.OrderStatusBadge
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun OrdersScreen(
    viewModel: StoreViewModel,
    onTrackOrder: (String) -> Unit,
    onNavigateToAuth: () -> Unit,
    onStartShopping: () -> Unit
) {
    val currentCustomer by viewModel.currentCustomer.collectAsState()
    val orders by viewModel.customerOrders.collectAsState()

    if (currentCustomer == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Neutral300,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please Log In",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Neutral800
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Log in to view your orders and track live deliveries.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onNavigateToAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Log In or Register", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Neutral300,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Orders Yet",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Neutral800
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You haven't placed any orders with this account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onStartShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Start Shopping", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "My Order History (${orders.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Neutral900
            )
        }

        items(orders) { order ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Order #${order.id}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Neutral900
                            )
                            Text(
                                text = order.orderDate,
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral400
                            )
                        }
                        OrderStatusBadge(status = order.orderStatus)
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Neutral200)

                    // Items summary
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        order.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${item.quantity}x ${item.productName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Neutral700,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "₹${(item.price * item.quantity).toInt()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Neutral900
                                )
                            }
                        }
                    }

                    // Location / Address snippet
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = Neutral100,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = Emerald700, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = order.fullAddress + if (order.landmark.isNotBlank()) " (Near: ${order.landmark})" else "",
                                fontSize = 11.sp,
                                color = Neutral600,
                                maxLines = 1
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp), color = Neutral200)

                    // Total & Track Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Paid (COD)", style = MaterialTheme.typography.labelSmall, color = Neutral400)
                            Text(
                                text = "₹${order.finalTotal.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Emerald800
                            )
                        }

                        Button(
                            onClick = { onTrackOrder(order.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text("Track Order", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }
    }
}
