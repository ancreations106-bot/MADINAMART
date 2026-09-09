package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun CartScreen(
    viewModel: StoreViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.cartTotal.collectAsState()
    val feeSettings by viewModel.feeSettings.collectAsState()
    val currentCustomer by viewModel.currentCustomer.collectAsState()

    val platformFee = feeSettings.platformFee
    val deliveryFee = feeSettings.deliveryFee
    val finalTotal = subtotal + platformFee + deliveryFee

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Neutral50)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Empty Cart",
                    tint = Neutral300,
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Neutral800
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Add fresh groceries and staples to get started.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral500
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onNavigateToHome,
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Start Shopping", fontWeight = FontWeight.Bold)
                }
            }
        }
        return
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Final Payable Amount", style = MaterialTheme.typography.labelSmall, color = Neutral500)
                        Text(
                            text = "₹${finalTotal.toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Emerald800
                        )
                    }
                    Button(
                        onClick = {
                            if (currentCustomer == null) {
                                onNavigateToAuth()
                            } else {
                                onNavigateToCheckout()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = if (currentCustomer == null) "Login to Checkout" else "Proceed to Checkout",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Neutral50),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Cart Items (${cartItems.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    TextButton(onClick = { viewModel.clearCart() }) {
                        Text("Clear All", color = Rose600, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            // Cart Items
            items(cartItems) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = item.product.image,
                            contentDescription = item.product.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Neutral100)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                color = Neutral900
                            )
                            Text(
                                text = item.product.unit,
                                style = MaterialTheme.typography.labelSmall,
                                color = Neutral500
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "₹${(item.product.price * item.quantity).toInt()}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Emerald800
                            )
                        }

                        // Counter Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Emerald50, RoundedCornerShape(8.dp))
                                .border(1.dp, Emerald600, RoundedCornerShape(8.dp))
                                .height(32.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.updateCartQuantity(item.product.id, item.quantity - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Emerald800, modifier = Modifier.size(14.dp))
                            }
                            Text(
                                text = item.quantity.toString(),
                                fontWeight = FontWeight.Bold,
                                color = Emerald900,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = { viewModel.addToCart(item.product, 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Emerald800, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }

            // Bill Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bill Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Neutral900
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Items Subtotal", style = MaterialTheme.typography.bodyMedium, color = Neutral600)
                            Text("₹${subtotal.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Neutral900)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Platform Fee", style = MaterialTheme.typography.bodyMedium, color = Neutral600)
                            Text("₹${platformFee.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Neutral900)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery Fee", style = MaterialTheme.typography.bodyMedium, color = Neutral600)
                            if (deliveryFee == 0.0) {
                                Text("FREE", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Emerald700)
                            } else {
                                Text("₹${deliveryFee.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Neutral900)
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Neutral200)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Final Total", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Neutral900)
                            Text("₹${finalTotal.toInt()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Emerald800)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Amber50,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Payments, contentDescription = null, tint = Amber600, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Payment Mode: Cash on Delivery (COD)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Neutral900
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
