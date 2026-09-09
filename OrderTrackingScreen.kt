package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.data.model.OrderStatus
import com.madinamart.app.theme.*
import com.madinamart.app.ui.components.OrderStatusBadge
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun OrderTrackingScreen(
    orderId: String,
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val order = allOrders.find { it.id == orderId }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order not found")
        }
        return
    }

    val steps = listOf(
        Pair(OrderStatus.ORDER_PLACED, "Order Placed"),
        Pair(OrderStatus.ORDER_CONFIRMED, "Order Confirmed"),
        Pair(OrderStatus.PACKED, "Packed at Madina Mart"),
        Pair(OrderStatus.SHIPPED, "Shipped for Delivery"),
        Pair(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery"),
        Pair(OrderStatus.DELIVERED, "Delivered to Doorstep")
    )

    val currentStepIndex = when (order.orderStatus) {
        OrderStatus.ORDER_PLACED -> 0
        OrderStatus.ORDER_CONFIRMED -> 1
        OrderStatus.PACKED -> 2
        OrderStatus.SHIPPED -> 3
        OrderStatus.OUT_FOR_DELIVERY -> 4
        OrderStatus.DELIVERED -> 5
        OrderStatus.CANCELLED -> -1
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Order #${order.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(order.orderDate, style = MaterialTheme.typography.labelSmall, color = Neutral400)
                    }
                    OrderStatusBadge(status = order.orderStatus)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Emerald50,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Emerald700, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estimated Delivery: ${order.estimatedDelivery}",
                            fontWeight = FontWeight.Bold,
                            color = Emerald900,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Stepper Progress Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Live Tracking Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900
                )
                Spacer(modifier = Modifier.height(16.dp))

                steps.forEachIndexed { idx, step ->
                    val isCompleted = idx <= currentStepIndex
                    val isCurrent = idx == currentStepIndex

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isCompleted) Emerald700 else Neutral200
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                            } else {
                                Text((idx + 1).toString(), fontSize = 11.sp, color = Neutral500, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = step.second,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCompleted) Neutral900 else Neutral400,
                                fontSize = 13.sp
                            )
                            if (isCurrent) {
                                Text(
                                    text = "In Progress",
                                    color = Emerald700,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    if (idx < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .padding(start = 13.dp)
                                .width(2.dp)
                                .height(22.dp)
                                .background(if (idx < currentStepIndex) Emerald700 else Neutral200)
                        )
                    }
                }
            }
        }

        // Delivery Destination Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Delivery Destination",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900
                )
                Text(text = "Customer: ${order.customerName}", fontSize = 13.sp, color = Neutral800)
                Text(text = "Contact: ${order.contactNumber}", fontSize = 13.sp, color = Neutral800)
                Text(text = "Address: ${order.fullAddress}", fontSize = 13.sp, color = Neutral800)
                if (order.landmark.isNotBlank()) {
                    Text(text = "Landmark: ${order.landmark}", fontSize = 13.sp, color = Neutral600)
                }
                if (order.locationShared && order.latitude != null && order.longitude != null) {
                    Text(
                        text = "📍 Live Location: Lat ${order.latitude}, Lng ${order.longitude}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Emerald700
                    )
                }
            }
        }
    }
}
