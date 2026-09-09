package com.madinamart.app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun AdminFeeSettingsScreen(
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val feeSettings by viewModel.feeSettings.collectAsState()

    var platformFeeInput by remember(feeSettings) { mutableStateOf(feeSettings.platformFee.toInt().toString()) }
    var deliveryFeeInput by remember(feeSettings) { mutableStateOf(feeSettings.deliveryFee.toInt().toString()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Tune, contentDescription = null, tint = Emerald700, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "Store Fee Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Neutral900
                )
                Text(
                    text = "Configure platform charges and delivery fees",
                    style = MaterialTheme.typography.bodySmall,
                    color = Neutral500
                )
            }
        }

        // Info Card
        Surface(
            color = Emerald50,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(14.dp)) {
                Icon(Icons.Default.Info, contentDescription = null, tint = Emerald700, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "New orders will immediately use these updated fee amounts. Past orders retain the historical fee values saved at the time of order creation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Emerald900,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = White),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (errorMessage != null) {
                    Surface(
                        color = Rose50,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Rose500)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = Rose600,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (successMessage != null) {
                    Surface(
                        color = Emerald50,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Emerald600)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Emerald700, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = successMessage ?: "",
                                color = Emerald800,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = platformFeeInput,
                    onValueChange = { platformFeeInput = it },
                    label = { Text("Platform Fee (₹)") },
                    supportingText = { Text("Standard handling fee added to each order") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deliveryFeeInput,
                    onValueChange = { deliveryFeeInput = it },
                    label = { Text("Delivery Fee (₹)") },
                    supportingText = { Text("Enter 0 for Free Delivery") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null

                        val pFee = platformFeeInput.trim().toDoubleOrNull()
                        val dFee = deliveryFeeInput.trim().toDoubleOrNull()

                        if (pFee == null || dFee == null) {
                            errorMessage = "Please enter valid numeric fee amounts."
                            return@Button
                        }
                        if (pFee < 0 || dFee < 0) {
                            errorMessage = "Fees cannot be negative."
                            return@Button
                        }

                        val result = viewModel.updateFeeSettings(pFee, dFee)
                        result.onSuccess {
                            successMessage = "Fee settings updated successfully!"
                        }.onFailure {
                            errorMessage = it.localizedMessage ?: "Failed to update fee settings."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Save Fee Settings", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
