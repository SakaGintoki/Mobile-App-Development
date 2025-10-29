package com.filkom.designimplementation.ui.littleai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
// import androidx.compose.material.icons.filled.Place
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.filkom.designimplementation.BotDock
import com.filkom.designimplementation.data.ChatMessage
import com.filkom.designimplementation.feature.chat.ChatViewModel
import com.filkom.designimplementation.R
import kotlinx.coroutines.launch

// PALETTE
private val Pink = Color(0xFFFF70A3)
private val PinkSoft = Color(0xFFFF79AC)
private val PageBg = Color(0xFFFFF1F6)
private val InputBg = Color(0xFFFFFFFF)

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

    // posisi avatar yang bisa diubah-ubah
    var dock by rememberSaveable(stateSaver = BotDock.saver()) {
        mutableStateOf<BotDock>(BotDock.TopBar)
    }

    LaunchedEffect(messages.size) {
        scope.launch { listState.animateScrollToItem(maxOf(messages.size - 1, 0)) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (dock is BotDock.TopBar) {
                            BotAvatar(size = 28.dp, onClick = { dock = BotDock.next(dock) })
                        }
                        Text("Little AI", color = PinkSoft, fontWeight = FontWeight.SemiBold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PinkSoft)
                    }
                },
                actions = {
                    // Hapus ikon lokasi; sisakan menu jika diinginkan
                    IconButton(onClick = { /* TODO menu */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = PinkSoft)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            BottomBar(
                value = input,
                onValueChange = { input = it },
                onSend = { vm.send(input).also { input = "" } },
                dock = dock,
                onAvatarClick = { dock = BotDock.next(dock) }
            )
        },
        containerColor = PageBg
    ) { inner ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            // Watermark tengah
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_littlesteps_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .alpha(0.12f),
                    contentScale = ContentScale.Fit
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "LittleSteps",
                    color = PinkSoft.copy(alpha = 0.3f),
                    fontWeight = FontWeight.Bold
                )
            }

            // Daftar pesan
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                state = listState
            ) {
                if (dock is BotDock.HeaderLeft) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                        ) {
                            BotAvatar(size = 40.dp, onClick = { dock = BotDock.next(dock) })
                            Text("Halo, aku Little AI. Ceritakan kebutuhanmu ya 💗", color = PinkSoft)
                        }
                    }
                }

                items(messages, key = { it.id }) { m ->
                    MessageBubble(m)
                    Spacer(Modifier.height(8.dp))
                }
                item { Spacer(Modifier.height(96.dp)) }
            }

            // Avatar melayang
            AnimatedVisibility(
                visible = dock is BotDock.FloatingEnd,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(Modifier.fillMaxSize()) {
                    BotAvatar(
                        size = 48.dp,
                        onClick = { dock = BotDock.next(dock) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
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
            .clip(CircleShape)
            .background(Pink)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Ganti mic dengan logo supaya tidak terlihat ikon mic di AppBar
        Image(
            painter = painterResource(R.drawable.ic_littlesteps_logo),
            contentDescription = "Bot",
            modifier = Modifier.fillMaxSize(0.7f),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun MessageBubble(m: ChatMessage) {
    val isUser = m.fromUser
    val bg = if (isUser) Pink else Color.White
    val fg = if (isUser) Color.White else Color(0xFF333333)
    val shape = if (isUser)
        RoundedCornerShape(topStart = 20.dp, topEnd = 4.dp, bottomEnd = 20.dp, bottomStart = 20.dp)
    else
        RoundedCornerShape(topStart = 4.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp)

    // Bersihkan **bold** dan markdown dasar untuk pesan AI saja
    val displayText = if (isUser) m.text else m.text.stripBasicMarkdown()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(bg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(displayText, color = fg)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    dock: BotDock,
    onAvatarClick: () -> Unit
) {
    Surface(
        color = Pink,
        shadowElevation = 12.dp,
        tonalElevation = 0.dp,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 14.dp,
                    end = 14.dp,
                    top = 10.dp,
                    bottom = WindowInsets.navigationBars
                        .asPaddingValues()
                        .calculateBottomPadding()
                        .coerceAtLeast(12.dp)
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            CircleIcon(Icons.Outlined.CameraAlt) {}
            CircleIcon(Icons.Outlined.Image) {}

            if (dock is BotDock.InputLeading) {
                BotAvatar(size = 32.dp, onClick = onAvatarClick)
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Masukkan text") },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(InputBg),
                trailingIcon = {
                    // Hapus blok ini jika ingin menghilangkan mic di kolom input
                    IconButton(onClick = { /* voice */ }) {
                        Icon(Icons.Outlined.Mic, contentDescription = "Mic", tint = Pink)
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputBg,
                    unfocusedContainerColor = InputBg,
                    disabledContainerColor = InputBg,
                    cursorColor = Pink,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            IconButton(
                onClick = onSend,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "Send", tint = Pink)
            }
        }
    }
}

@Composable
private fun CircleIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Color.White)
    ) {
        Icon(icon, contentDescription = null, tint = Pink)
    }
}

/* ===== Helpers ===== */

/** Bersihkan **bold**, _italic_, `code`, [link](url), serta LaTeX escaped \( \) dan \[ \] */
private fun String.stripBasicMarkdown(): String = this
    // **bold** dan __bold__
    .replace(Regex("""\*\*(.*?)\*\*"""), "$1")
    .replace(Regex("""__(.*?)__"""), "$1")
    // *italic* dan _italic_
    .replace(Regex("""\*(.*?)\*"""), "$1")
    .replace(Regex("""_(.*?)_"""), "$1")
    // `code`
    .replace(Regex("""`([^`]+)`"""), "$1")
    // [text](url)
    .replace(Regex("""\[(.*?)]\((.*?)\)"""), "$1")
    // LaTeX inline \( \) dan block \[ \]
    .replace("""\\""", "\\") // normalisasi escape
    .replace("""\(""", "")
    .replace("""\)""", "")
    .replace("""\[""", "")
    .replace("""\]""", "")
