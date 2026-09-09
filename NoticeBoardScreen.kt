package com.madinamart.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.madinamart.app.theme.*
import com.madinamart.app.ui.components.NoticeCard
import com.madinamart.app.viewmodel.StoreViewModel

@Composable
fun NoticeBoardScreen(
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val notices by viewModel.activeCustomerNotices.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Neutral50)
    ) {
        if (notices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Neutral300,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Active Notices",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Neutral800
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All store announcements and offers will be posted here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Store Notice Board (${notices.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Text(
                        text = "Official updates, offers & service announcements from Madina Mart.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                items(notices) { notice ->
                    NoticeCard(notice = notice)
                }
            }
        }
    }
}
