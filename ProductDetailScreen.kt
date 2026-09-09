package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit
) {
    val product = viewModel.products.find { it.id == productId }
    val cartItems by viewModel.cartItems.collectAsState()
    val cartQty = cartItems.find { it.product.id == productId }?.quantity ?: 0

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found", style = MaterialTheme.typography.titleMedium)
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
                        Text("Total Price", style = MaterialTheme.typography.labelSmall, color = Neutral500)
                        Text(
                            text = "₹${(product.price * (if (cartQty > 0) cartQty else 1)).toInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Emerald800
                        )
                    }

                    if (cartQty == 0) {
                        Button(
                            onClick = { viewModel.addToCart(product, 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Emerald50, RoundedCornerShape(10.dp))
                                .border(1.dp, Emerald700, RoundedCornerShape(10.dp))
                                .height(46.dp)
                                .padding(horizontal = 4.dp)
                        ) {
                            IconButton(onClick = { viewModel.updateCartQuantity(product.id, cartQty - 1) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Emerald800)
                            }
                            Text(
                                text = cartQty.toString(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Emerald900,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            IconButton(onClick = { viewModel.addToCart(product, 1) }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Emerald800)
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
        ) {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(White)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )

                if (product.originalPrice > product.price) {
                    val discount = (((product.originalPrice - product.price) / product.originalPrice) * 100).toInt()
                    Surface(
                        color = Amber500,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "$discount% OFF",
                            color = Neutral950,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Product Details Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = White),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = product.category,
                            color = Emerald800,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = product.unit,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${product.price.toInt()}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = Emerald800
                        )
                        if (product.originalPrice > product.price) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "₹${product.originalPrice.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    textDecoration = TextDecoration.LineThrough,
                                    color = Neutral400
                                )
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 14.dp), color = Neutral200)

                    // Delivery Guarantees
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fast Doorstep Delivery", style = MaterialTheme.typography.labelSmall, color = Neutral700)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cash on Delivery", style = MaterialTheme.typography.labelSmall, color = Neutral700)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 14.dp), color = Neutral200)

                    Text(
                        text = "Product Description",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = product.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral600,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
