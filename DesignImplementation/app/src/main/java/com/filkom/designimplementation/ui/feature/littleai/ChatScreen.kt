package com.filkom.designimplementation.ui.feature.littleai

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filkom.designimplementation.BotDock
import com.filkom.designimplementation.R
import com.filkom.designimplementation.model.data.ai.ChatMessage
import com.filkom.designimplementation.ui.theme.* // Pastikan import warna sesuai
import com.filkom.designimplementation.viewmodel.feature.chat.ChatViewModel
import kotlinx.coroutines.launch
import androidx.compose.ui.text.TextStyle
// --- DUMMY COLORS (Hapus jika sudah ada di ui/theme/Color.kt) ---
// val Pink = Color(0xFFFF4081)
// val Primary200 = Color(0xFFEEEEEE)
// val Primary400 = Color(0xFFBDBDBD)
// val Primary500 = Color(0xFF9E9E9E)
// ----------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    vm: ChatViewModel,
    onBack: () -> Unit = {}
) {
    val messages by vm.messages.collectAsStateWithLifecycle()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Logic untuk scroll ke bawah saat pesan bertambah atau keyboard muncul
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var dock by rememberSaveable(stateSaver = BotDock.saver()) {
        mutableStateOf<BotDock>(BotDock.TopBar)
    }

    Scaffold(
        containerColor = Primary200,
        topBar = {
            ChatTopBar(dock = dock, onBack = onBack, onAvatarClick = { dock = BotDock.next(dock) })
        },
        bottomBar = {
            ChatBottomBar(
                value = input,
                onValueChange = { input = it },
                onSend = {
                    if (input.isNotBlank()) {
                        vm.send(input)
                        input = ""
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (messages.isEmpty()) {
                EmptyStateWatermark()
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Header Pesan Pembuka
                if (dock is BotDock.HeaderLeft) {
                    item {
                        BotGreetingItem(onClick = { dock = BotDock.next(dock) })
                    }
                }

                items(messages, key = { it.id }) { m ->
                    MessageBubble(m)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(dock: BotDock, onBack: () -> Unit, onAvatarClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Little AI", color = Primary500, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Primary500)
            }
        },
        actions = {
            IconButton(onClick = { /* TODO menu */ }) {
                Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Primary500)
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Primary200.copy(alpha = 0.95f) // Sedikit transparan
        )
    )
}

@Composable
fun EmptyStateWatermark() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_littlesteps_logo),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .alpha(0.1f),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Mulai percakapan dengan Little AI",
            color = Primary400,
            fontSize = 14.sp
        )
    }
}

@Composable
fun BotGreetingItem(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        BotAvatar(size = 40.dp, onClick = onClick)
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Text(
                text = "Halo! Aku Little AI.\nCeritakan kebutuhanmu ya 💗",
                modifier = Modifier.padding(12.dp),
                color = Color(0xFF333333),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun MessageBubble(m: ChatMessage) {
    val isUser = m.fromUser
    val bg = if (isUser) Pink else Color.White
    val contentColor = if (isUser) Color.White else Color(0xFF333333)

    // Shape bubble lebih modern
    val shape = if (isUser)
        RoundedCornerShape(topStart = 18.dp, topEnd = 4.dp, bottomEnd = 18.dp, bottomStart = 18.dp)
    else
        RoundedCornerShape(topStart = 4.dp, topEnd = 18.dp, bottomEnd = 18.dp, bottomStart = 18.dp)

    val displayText = if (isUser) m.text else m.text.stripBasicMarkdown()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Opsi: Tampilkan avatar kecil di sebelah bubble bot jika diinginkan
            // BotAvatar(size = 24.dp, onClick = {})
            // Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = shape,
            color = bg,
            shadowElevation = 2.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = displayText,
                color = contentColor,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                lineHeight = 20.sp
            )
        }
    }
}
@Composable
private fun ChatBottomBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        color = Pink,
        shadowElevation = 8.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(top = 16.dp, bottom = 16.dp, start = 12.dp, end = 12.dp),

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            IconAction(Icons.Outlined.CameraAlt)
            IconAction(Icons.Outlined.Image)

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                maxLines = 4,
                textStyle = TextStyle(fontSize = 14.sp, color = Color.Black),
                modifier = Modifier
                    .weight(1f)
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text("Ketik pesan...", fontSize = 14.sp, color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )
            IconButton(
                onClick = onSend,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun IconAction(icon: ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = Color.White,
        modifier = Modifier
            .size(26.dp)
            .clickable { /* Handle click */ }
    )
}
@Composable
private fun BotAvatar(
    size: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .shadow(2.dp, CircleShape)
            .clip(CircleShape)
            .background(Color.White) // Background putih agar logo jelas
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Border ring (opsional)
        Box(Modifier.matchParentSize().background(Pink.copy(alpha = 0.1f)))

        Image(
            painter = painterResource(R.drawable.ic_littlesteps_logo_notext),
            contentDescription = "Bot",
            modifier = Modifier.fillMaxSize(0.8f),
            contentScale = ContentScale.Fit
        )
    }
}

// Utility untuk Markdown sederhana
private fun String.stripBasicMarkdown(): String = this
    .replace(Regex("""\*\*(.*?)\*\*"""), "$1") // Bold
    .replace(Regex("""__(.*?)__"""), "$1")     // Bold
    .replace(Regex("""\*(.*?)\*"""), "$1")     // Italic
    .replace(Regex("""_(.*?)_"""), "$1")       // Italic
    .replace(Regex("""`([^`]+)`"""), "$1")     // Code
    .replace(Regex("""\[(.*?)]\((.*?)\)"""), "$1") // Link
    // Hapus karakter escape yang tidak perlu
    .replace("""\""", "")