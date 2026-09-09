package com.madinamart.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminCustomersScreen(
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val customers = viewModel.allCustomers

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        if (customers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.People, contentDescription = null, tint = Neutral300, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Registered Customers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Customer accounts created in the app will appear here.", color = Neutral500, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Registered Customer Accounts (${customers.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Text(
                        text = "Customer profiles are strictly isolated; customers can only access their own orders.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Neutral500
                    )
                }

                items(customers) { customer ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val dateStr = sdf.format(Date(customer.createdAt))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Emerald100),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = customer.name.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = Emerald800,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(customer.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Neutral900)
                                Text("+91 ${customer.mobile}", fontSize = 13.sp, color = Neutral700)
                                Text("Customer ID: ${customer.id}", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.SemiBold)
                                Text("Registered: $dateStr", fontSize = 11.sp, color = Neutral400)
                            }
                        }
                    }
                }
            }
        }
    }
}
