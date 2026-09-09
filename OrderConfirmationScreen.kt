package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
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
import com.madinamart.app.theme.*
import com.madinamart.app.ui.components.OrderStatusBadge
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun OrderConfirmationScreen(
    orderId: String,
    viewModel: StoreViewModel,
    onTrackOrder: (String) -> Unit,
    onContinueShopping: () -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()
    val order = orders.find { it.id == orderId }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order details not found")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Success Icon & Message
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Emerald100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Success",
                tint = Emerald700,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Order Placed Successfully!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = Emerald900
        )

        Text(
            text = "Your order #${order.id} has been received and is being prepared for fast doorstep delivery.",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral600,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Order Details Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Order Status", style = MaterialTheme.typography.bodyMedium, color = Neutral500)
                    OrderStatusBadge(status = order.orderStatus)
                }

                Divider(color = Neutral200)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Customer Name", color = Neutral500, fontSize = 13.sp)
                    Text(order.customerName, fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Contact Number", color = Neutral500, fontSize = 13.sp)
                    Text(order.contactNumber, fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Address", color = Neutral500, fontSize = 13.sp)
                    Text(
                        order.fullAddress,
                        fontWeight = FontWeight.SemiBold,
                        color = Neutral900,
                        fontSize = 13.sp,
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                }

                if (order.landmark.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Landmark", color = Neutral500, fontSize = 13.sp)
                        Text(order.landmark, fontWeight = FontWeight.SemiBold, color = Neutral900, fontSize = 13.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Live Location Status", color = Neutral500, fontSize = 13.sp)
                    Text(
                        if (order.locationShared) "📍 Live GPS Shared" else "Manual Address Only",
                        fontWeight = FontWeight.Bold,
                        color = if (order.locationShared) Emerald700 else Neutral600,
                        fontSize = 12.sp
                    )
                }

                Divider(color = Neutral200)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment Method", color = Neutral500, fontSize = 13.sp)
                    Text("Cash on Delivery (COD)", fontWeight = FontWeight.Bold, color = Amber600, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Final Payable Amount", fontWeight = FontWeight.Bold, color = Neutral900)
                    Text("₹${order.finalTotal.toInt()}", fontWeight = FontWeight.Black, color = Emerald800, fontSize = 18.sp)
                }
            }
        }

        // Action Buttons
        Button(
            onClick = { onTrackOrder(order.id) },
            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track This Order", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(18.dp), tint = Emerald700)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue Shopping", fontWeight = FontWeight.Bold, color = Emerald700)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
