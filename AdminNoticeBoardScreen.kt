package com.madinamart.app.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.madinamart.app.data.model.Notice
import com.madinamart.app.theme.*
import com.madinamart.app.viewmodel.StoreViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AdminNoticeBoardScreen(
    viewModel: StoreViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val notices by viewModel.notices.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingNotice by remember { mutableStateOf<Notice?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf<String?>("") }
    var dialogError by remember { mutableStateOf<String?>(null) }

    // Image Picker with 5MB validation
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val sizeBytes = inputStream?.available() ?: 0
                inputStream?.close()

                if (sizeBytes > 5 * 1024 * 1024) { // 5MB
                    dialogError = "Image must be 5 MB or smaller."
                } else {
                    imageUrl = uri.toString()
                    dialogError = null
                }
            } catch (_: Exception) {
                imageUrl = uri.toString()
            }
        }
    }

    fun openCreateDialog() {
        editingNotice = null
        title = ""
        description = ""
        imageUrl = ""
        dialogError = null
        showDialog = true
    }

    fun openEditDialog(notice: Notice) {
        editingNotice = notice
        title = notice.title
        description = notice.description
        imageUrl = notice.imageUrl ?: ""
        dialogError = null
        showDialog = true
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openCreateDialog() },
                containerColor = Emerald700,
                contentColor = White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Notice")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Neutral50)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Notice Board Management (${notices.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Neutral900
                    )
                    Text(
                        text = "Publish updates, announcements & festive offers to the customer app.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Neutral500
                    )
                }

                if (notices.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No notices created. Tap + to add a notice.")
                        }
                    }
                }

                items(notices) { notice ->
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val dateStr = sdf.format(Date(notice.createdAt))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = White),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Neutral200))
                    ) {
                        Column {
                            if (!notice.imageUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = notice.imageUrl,
                                    contentDescription = notice.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .background(Neutral100)
                                )
                            }
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (notice.active) "Active on Customer App" else "Disabled / Hidden",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (notice.active) Emerald700 else Rose600
                                    )
                                    Text(text = dateStr, fontSize = 11.sp, color = Neutral400)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notice.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Neutral900
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notice.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Neutral600
                                )

                                Divider(modifier = Modifier.padding(vertical = 10.dp), color = Neutral200)

                                // Actions (Edit, Toggle Status, Delete)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = { viewModel.toggleNoticeStatus(notice.id) }) {
                                        Text(if (notice.active) "Disable" else "Enable", color = Neutral700, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    TextButton(onClick = { openEditDialog(notice) }) {
                                        Text("Edit", color = Emerald700, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    TextButton(onClick = { viewModel.deleteNotice(notice.id) }) {
                                        Text("Delete", color = Rose600, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Create / Edit Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = if (editingNotice == null) "Create Store Notice" else "Edit Notice",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (dialogError != null) {
                        Surface(
                            color = Rose50,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Rose500)
                        ) {
                            Text(
                                text = dialogError ?: "",
                                color = Rose600,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Notice Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description / Announcement *") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = imageUrl ?: "",
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Upload/Replace Photo (Max 5 MB)", fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        dialogError = null
                        if (title.isBlank() || description.isBlank()) {
                            dialogError = "Title and description are required."
                            return@Button
                        }
                        if (editingNotice == null) {
                            val res = viewModel.createNotice(title, description, imageUrl)
                            res.onSuccess { showDialog = false }
                                .onFailure { dialogError = it.localizedMessage }
                        } else {
                            val res = viewModel.updateNotice(editingNotice!!.id, title, description, imageUrl, editingNotice!!.active)
                            res.onSuccess { showDialog = false }
                                .onFailure { dialogError = it.localizedMessage }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Emerald700)
                ) {
                    Text("Save Notice", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = Neutral500)
                }
            }
        )
    }
}
