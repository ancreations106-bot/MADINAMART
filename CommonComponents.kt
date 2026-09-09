package com.madinamart.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.madinamart.app.data.model.Notice
import com.madinamart.app.data.model.OrderStatus
import com.madinamart.app.data.model.Product
import com.madinamart.app.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MadinaMartTopBar(
    title: String = "Madina Mart",
    subtitle: String = "Your Local Shopping Store",
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    cartCount: Int = 0,
    onCartClick: () -> Unit = {},
    noticeCount: Int = 0,
    onNoticeClick: () -> Unit = {},
    onAdminClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = White,
                    fontWeight = FontWeight.Black
                )
                if (subtitle.isNotBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = Emerald100
                    )
                }
            }
        },
        navigationIcon = {
            if (showBack) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = White
                    )
                }
            } else {
                IconButton(onClick = onAdminClick) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Emerald800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Madina Mart Store",
                            tint = Amber500,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        actions = {
            // Notice Board Icon
            IconButton(onClick = onNoticeClick) {
                BadgedBox(
                    badge = {
                        if (noticeCount > 0) {
                            Badge(
                                containerColor = Amber500,
                                contentColor = Neutral950
                            ) {
                                Text(
                                    text = noticeCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Campaign,
                        contentDescription = "Notice Board",
                        tint = White
                    )
                }
            }

            // Cart Icon
            IconButton(onClick = onCartClick) {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = Amber500,
                                contentColor = Neutral950
                            ) {
                                Text(
                                    text = cartCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart",
                        tint = White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Emerald700
        )
    )
}

@Composable
fun MadinaMartBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    cartCount: Int = 0
) {
    NavigationBar(
        containerColor = White,
        tonalElevation = 8.dp,
        modifier = Modifier.border(width = 1.dp, color = Neutral200)
    ) {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { onNavigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald50
            )
        )
        NavigationBarItem(
            selected = currentRoute == "categories",
            onClick = { onNavigate("categories") },
            icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
            label = { Text("Categories", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald50
            )
        )
        NavigationBarItem(
            selected = currentRoute == "cart",
            onClick = { onNavigate("cart") },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(
                                containerColor = Amber500,
                                contentColor = Neutral950
                            ) {
                                Text(cartCount.toString(), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                }
            },
            label = { Text("Cart", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald50
            )
        )
        NavigationBarItem(
            selected = currentRoute == "orders",
            onClick = { onNavigate("orders") },
            icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
            label = { Text("My Orders", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald50
            )
        )
        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { onNavigate("profile") },
            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
            label = { Text("Account", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Emerald700,
                selectedTextColor = Emerald700,
                indicatorColor = Emerald50
            )
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Product Image & Badges
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Neutral100)
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (product.originalPrice > product.price) {
                    val discount = (((product.originalPrice - product.price) / product.originalPrice) * 100).toInt()
                    Surface(
                        color = Amber500,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = "$discount% OFF",
                            color = Neutral950,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                color = Neutral900,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = product.unit,
                style = MaterialTheme.typography.labelSmall,
                color = Neutral500,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Pricing & Add Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "₹${product.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = Emerald800
                    )
                    if (product.originalPrice > product.price) {
                        Text(
                            text = "₹${product.originalPrice.toInt()}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = Neutral400
                            )
                        )
                    }
                }

                if (cartQuantity == 0) {
                    Button(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald700,
                            contentColor = White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("ADD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Emerald50, RoundedCornerShape(8.dp))
                            .border(1.dp, Emerald600, RoundedCornerShape(8.dp))
                            .height(34.dp)
                    ) {
                        IconButton(
                            onClick = onDecrement,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Emerald800, modifier = Modifier.size(16.dp))
                        }
                        Text(
                            text = cartQuantity.toString(),
                            fontWeight = FontWeight.Bold,
                            color = Emerald900,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = onIncrement,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = Emerald800, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.ORDER_PLACED -> Pair(Emerald50, Emerald800)
        OrderStatus.ORDER_CONFIRMED -> Pair(Emerald100, Emerald900)
        OrderStatus.PACKED -> Pair(Amber50, Amber600)
        OrderStatus.SHIPPED -> Pair(Emerald50, Emerald700)
        OrderStatus.OUT_FOR_DELIVERY -> Pair(Amber50, Amber600)
        OrderStatus.DELIVERED -> Pair(Emerald100, Emerald900)
        OrderStatus.CANCELLED -> Pair(Rose50, Rose600)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(50),
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Text(
            text = status.displayName,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun NoticeCard(notice: Notice, modifier: Modifier = Modifier) {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateStr = sdf.format(Date(notice.createdAt))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            if (!notice.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = notice.imageUrl,
                    contentDescription = notice.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Neutral100)
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NOTICE",
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp,
                            color = Emerald800
                        )
                    }
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = Neutral400
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notice.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Neutral900,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notice.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Neutral600
                )
            }
        }
    }
}
