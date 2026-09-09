package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import com.madinamart.app.ui.components.ProductCard
import com.madinamart.app.viewmodel.StoreViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesSearchScreen(
    viewModel: StoreViewModel,
    onNavigateToProduct: (String) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val products by viewModel.filteredProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        // Search Input Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setSearchQuery(it) },
            placeholder = { Text("Search products, brands, groceries...", color = Neutral400, fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Emerald700)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Neutral400)
                    }
                }
            },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Emerald700,
                unfocusedBorderColor = Neutral300,
                focusedContainerColor = White,
                unfocusedContainerColor = White
            ),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        // Categories selector chips
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                Text(
                    text = if (searchQuery.isNotBlank()) "Search Results (${products.size})" else "Filter by Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }

            if (products.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No products found", style = MaterialTheme.typography.titleMedium, color = Neutral700)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Try searching with different keywords like rice, oil, atta, or ghee.", color = Neutral400, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(products.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (product in pair) {
                            val cartQty = cartItems.find { it.product.id == product.id }?.quantity ?: 0
                            ProductCard(
                                product = product,
                                cartQuantity = cartQty,
                                onAddToCart = { viewModel.addToCart(product) },
                                onIncrement = { viewModel.addToCart(product, 1) },
                                onDecrement = { viewModel.updateCartQuantity(product.id, cartQty - 1) },
                                onClick = { onNavigateToProduct(product.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
