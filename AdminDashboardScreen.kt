package com.madinamart.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.data.model.OrderStatus
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel

data class AdminNavTile(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val routeAction: () -> Unit
)

@Composable
fun AdminDashboardScreen(
    viewModel: StoreViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToFees: () -> Unit,
    onNavigateToPassword: () -> Unit,
    onNavigateToCustomers: () -> Unit,
    onLogout: () -> Unit
) {
    val orders by viewModel.allOrders.collectAsState()
    val notices by viewModel.notices.collectAsState()
    val feeSettings by viewModel.feeSettings.collectAsState()

    val totalOrders = orders.size
    val pendingOrders = orders.count { it.orderStatus != OrderStatus.DELIVERED && it.orderStatus != OrderStatus.CANCELLED }
    val totalRevenue = orders.filter { it.orderStatus == OrderStatus.DELIVERED }.sumOf { it.finalTotal }
    val activeNotices = notices.count { it.active }

    val navTiles = listOf(
        AdminNavTile("Customer Orders", "$totalOrders orders ($pendingOrders pending)", Icons.Default.ReceiptLong, onNavigateToOrders),
        AdminNavTile("Notice Board", "$activeNotices active notices", Icons.Default.Campaign, onNavigateToNotices),
        AdminNavTile("Fee Settings", "Platform: ₹${feeSettings.platformFee.toInt()}, Delivery: ₹${feeSettings.deliveryFee.toInt()}", Icons.Default.Tune, onNavigateToFees),
        AdminNavTile("Registered Customers", "${viewModel.allCustomers.size} customers", Icons.Default.People, onNavigateToCustomers),
        AdminNavTile("Change Password", "Secure Admin Portal password", Icons.Default.Password, onNavigateToPassword)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Overview Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Emerald800)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Madina Mart Administration", color = Emerald100, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Merchant Control Center", color = White, fontSize = 20.sp, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Orders", color = Emerald100, fontSize = 11.sp)
                        Text(totalOrders.toString(), color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Pending Dispatch", color = Amber500, fontSize = 11.sp)
                        Text(pendingOrders.toString(), color = Amber500, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text("Delivered Revenue", color = Emerald100, fontSize = 11.sp)
                        Text("₹${totalRevenue.toInt()}", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Navigation Grid
        Text(
            text = "Management Portals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Neutral900
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(navTiles) { tile ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { tile.routeAction() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = White),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Emerald50,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(tile.icon, contentDescription = null, tint = Emerald700, modifier = Modifier.size(24.dp))
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(tile.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Neutral900)
                            Text(tile.subtitle, fontSize = 12.sp, color = Neutral500)
                        }

                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Neutral400)
                    }
                }
            }
        }

        // Logout Button
        OutlinedButton(
            onClick = {
                viewModel.logoutAdmin()
                onLogout()
            },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
            border = androidx.compose.foundation.BorderStroke(1.dp, Rose500),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out of Admin Portal", fontWeight = FontWeight.Bold)
        }
    }
}
