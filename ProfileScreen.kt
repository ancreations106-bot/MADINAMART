package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun ProfileScreen(
    viewModel: StoreViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToNotices: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    val currentCustomer by viewModel.currentCustomer.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            if (currentCustomer != null) {
                val customer = currentCustomer!!
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Emerald100),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = customer.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Emerald800
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = customer.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Neutral900
                        )
                        Text(
                            text = "+91 ${customer.mobile}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Neutral500
                        )
                        Text(
                            text = "Customer ID: ${customer.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Emerald700,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Sign In to Your Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Access your saved addresses, past orders, and live delivery status.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = onNavigateToAuth,
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Log In or Register", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Action Options Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            Column {
                ProfileOptionRow(
                    icon = Icons.Default.ReceiptLong,
                    title = "My Orders",
                    subtitle = "View order history and track deliveries",
                    onClick = onNavigateToOrders
                )
                Divider(color = Neutral200, modifier = Modifier.padding(horizontal = 16.dp))
                ProfileOptionRow(
                    icon = Icons.Default.Campaign,
                    title = "Notice Board",
                    subtitle = "Store announcements and updates",
                    onClick = onNavigateToNotices
                )
                Divider(color = Neutral200, modifier = Modifier.padding(horizontal = 16.dp))
                ProfileOptionRow(
                    icon = Icons.Default.Security,
                    title = "Admin Portal",
                    subtitle = "Notice Board, Orders, and Fee Settings",
                    onClick = onNavigateToAdmin
                )
            }
        }

        // Logout Button if logged in
        if (currentCustomer != null) {
            OutlinedButton(
                onClick = { viewModel.logoutCustomer() },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Rose600),
                border = androidx.compose.foundation.BorderStroke(1.dp, Rose500),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out of Madina Mart", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun ProfileOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Emerald50),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = Neutral900)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = Neutral500)
        }

        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Neutral400)
    }
}
