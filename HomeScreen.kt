package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.theme.*
import com.madinamart.app.ui.components.NoticeCard
import com.madinamart.app.ui.components.ProductCard
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun HomeScreen(
    viewModel: StoreViewModel,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToNoticeBoard: () -> Unit
) {
    val products by viewModel.filteredProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val activeNotices by viewModel.activeCustomerNotices.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Search Shortcut
        item {
            Surface(
                color = Emerald700,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(White)
                        .clickable { onNavigateToSearch() }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = Neutral400,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Search rice, atta, oils, ghee, spices...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral400
                        )
                    }
                }
            }
        }

        // Hero Promotion Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Emerald800, Emerald700)
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Surface(
                        color = Amber500,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "LOCAL FAST DELIVERY",
                            color = Neutral950,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fresh Daily Groceries &\nAuthentic Staples",
                        style = MaterialTheme.typography.headlineMedium,
                        color = White,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cash on Delivery • Accurate GPS Delivery • Doorstep in 2-4 Hours",
                        style = MaterialTheme.typography.bodySmall,
                        color = Emerald100
                    )
                }
            }
        }

        // Active Notice Board Banner (if notices exist)
        if (activeNotices.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Store Announcements",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Neutral900
                        )
                        TextButton(onClick = onNavigateToNoticeBoard) {
                            Text("View All (${activeNotices.size})", color = Emerald700, fontWeight = FontWeight.Bold)
                        }
                    }
                    NoticeCard(notice = activeNotices.first())
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Categories Horizontal Bar
        item {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                Text(
                    text = "Shop by Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(viewModel.categories) { cat ->
                        val isSelected = selectedCategory == cat.name
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                viewModel.selectCategory(cat.name)
                            },
                            label = {
                                Text(
                                    text = cat.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Emerald700,
                                selectedLabelColor = White,
                                containerColor = White,
                                labelColor = Neutral700
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) Emerald700 else Neutral200
                            )
                        )
                    }
                }
            }
        }

        // Popular Products Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (selectedCategory == "All Items") "Featured Groceries" else selectedCategory,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900
                )
                Text(
                    text = "${products.size} Products",
                    style = MaterialTheme.typography.labelSmall,
                    color = Neutral500
                )
            }
        }

        // 2-Column Products Grid items
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
