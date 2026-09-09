package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun AuthScreen(
    viewModel: StoreViewModel,
    onAuthSuccess: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App Branding
        Text(
            text = "Madina Mart",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = Emerald800
        )
        Text(
            text = "Your Local Shopping Store",
            style = MaterialTheme.typography.bodyMedium,
            color = Neutral500
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tab Selector (Login vs Register)
        TabRow(
            selectedTabIndex = if (isRegisterMode) 1 else 0,
            containerColor = White,
            contentColor = Emerald700,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = !isRegisterMode,
                onClick = {
                    isRegisterMode = false
                    errorMessage = null
                    successMessage = null
                },
                text = { Text("Log In", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = isRegisterMode,
                onClick = {
                    isRegisterMode = true
                    errorMessage = null
                    successMessage = null
                },
                text = { Text("Register", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Auth Form Card
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
                        Text(
                            text = successMessage ?: "",
                            color = Emerald800,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Emerald700) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = mobile,
                    onValueChange = {
                        if (it.length <= 10) mobile = it.filter { ch -> ch.isDigit() }
                    },
                    label = { Text("Mobile Number (10-digit) *") },
                    placeholder = { Text("e.g. 9876543210") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Emerald700) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password *") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald700) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Neutral400
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )

                Button(
                    onClick = {
                        errorMessage = null
                        successMessage = null
                        if (isRegisterMode) {
                            val res = viewModel.registerCustomer(name, mobile, password)
                            res.onSuccess {
                                successMessage = "Registration successful! Welcome to Madina Mart."
                                onAuthSuccess()
                            }.onFailure { ex ->
                                errorMessage = ex.localizedMessage ?: "Registration failed."
                            }
                        } else {
                            val res = viewModel.loginCustomer(mobile, password)
                            res.onSuccess {
                                successMessage = "Welcome back!"
                                onAuthSuccess()
                            }.onFailure { ex ->
                                errorMessage = ex.localizedMessage ?: "Login failed."
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = if (isRegisterMode) "Create Account" else "Log In to Madina Mart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
