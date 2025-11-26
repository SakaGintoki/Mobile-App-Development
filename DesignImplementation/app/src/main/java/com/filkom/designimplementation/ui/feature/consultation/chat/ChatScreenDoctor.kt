package com.filkom.designimplementation.ui.feature.consultation.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.filkom.designimplementation.R
// Import ONLY ChatMessage from the AI package (shared model)
import com.filkom.designimplementation.model.data.consultation.ChatMessageDoctor
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.consultation.ConsultationViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenDoctor(
    doctorName: String,
    doctorId: String,
    doctorImage: String,
    doctorSpecialization: String,
    viewModel: ConsultationViewModel,
    onBack: () -> Unit
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        viewModel.loadChatSession(doctorId, currentUserId)
    }

    val messages by viewModel.chatMessages.collectAsState()
    val isSessionActive by viewModel.isSessionActive.collectAsState()

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                name = doctorName,
                image = doctorImage,
                specialization = doctorSpecialization,
                status = if(isSessionActive) "Online" else "Offline",
                onBack = onBack
            )
        },
        bottomBar = {
            if (isSessionActive) {
                ChatInputBar(
                    value = messageText,
                    onValueChange = { messageText = it },
//                    onSend = {
//                        if (messageText.isNotBlank()) {
//                            viewModel.sendChatMessage(doctorId, currentUserId, messageText)
//                            messageText = ""
//                        }
//                    }
                    onSend = {
                        if (messageText.isNotBlank()) {
                            viewModel.sendChatMessage(
                                doctorId = doctorId,
                                userId = currentUserId,
                                text = messageText,
                                doctorName = doctorName,
                                doctorSpecialization = doctorSpecialization
                            )
                            messageText = ""
                        }
                    }
                )
            } else {
                SessionExpiredBanner()
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)).padding(innerPadding)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { chat ->
                    ChatBubbleDoctor(chat)
                }
            }
        }
    }
}

@Composable
fun SessionExpiredBanner() {
    Surface(
        color = Color(0xFFE0E0E0),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                "Sesi konsultasi telah berakhir.",
                fontFamily = Poppins,
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(
    name: String,
    image: String,
    specialization: String,
    status: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = image,
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.ic_launcher_background),
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        fontFamily = Poppins,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = specialization,
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            color = Color.Gray
                        )

                        Spacer(Modifier.width(4.dp))
                        Box(Modifier.size(3.dp).background(Color.LightGray, CircleShape))
                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = status,
                            fontFamily = Poppins,
                            fontSize = 10.sp,
                            color = if(status == "Online") Pink else Color.Gray
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        },
        actions = {
            IconButton(onClick = {}) { Icon(Icons.Filled.Videocam, null, tint = Pink) }
            IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, null) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun ChatBubbleDoctor(chat: ChatMessageDoctor) { // Change: Use ChatMessage here
    val bubbleColor = if (chat.fromUser) Pink else Color.White
    val textColor = if (chat.fromUser) Color.White else Color.Black

    // Correct logic for alignment in a Box
    val alignment = if (chat.fromUser) Alignment.CenterEnd else Alignment.CenterStart

    val timeString = remember(chat.timestamp) {
        SimpleDateFormat("HH:mm", Locale("id", "ID")).format(chat.timestamp)
    }

    val shape = if (chat.fromUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    } else {
        RoundedCornerShape(topStart = 0.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .align(if (chat.fromUser) Alignment.End else Alignment.Start) // Use horizontal alignment for Column children
                .background(bubbleColor, shape)
                .widthIn(max = 280.dp)
                .padding(12.dp)
        ) {
            Column {
                Text(chat.text, color = textColor, fontSize = 14.sp, fontFamily = Poppins)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = timeString,
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(shadowElevation = 8.dp, color = Color.White) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.AttachFile, null, tint = Color.Gray)
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Tulis pesan...", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color(0xFFF0F0F0),
                    unfocusedContainerColor = Color(0xFFF0F0F0)
                ),
                maxLines = 3
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = onSend,
                modifier = Modifier.background(Pink, CircleShape).size(48.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}