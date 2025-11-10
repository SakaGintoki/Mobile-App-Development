package com.filkom.designimplementation.ui.start

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.theme.Poppins

@Composable
fun StartScreen(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onFacebookClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Header foto + fade putih ke bawah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .offset(y = 30.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.start_bg),
                contentDescription = "Mother and Child",
                alpha = 0.95f,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            val colorStops = arrayOf(
                0.0f to Color.Transparent,
                0.25f to Color.White.copy(alpha = 0.3f),
                0.55f to Color.White.copy(alpha = 0.65f),
                0.8f to Color.White.copy(alpha = 0.9f),
                1f to Color.White
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(brush = Brush.verticalGradient(colorStops = colorStops))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = 200.dp)
                .align(Alignment.CenterEnd)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) { append("Tumbuh ") }
                    withStyle(SpanStyle(color = Color(0xFFEB4C94), fontWeight = FontWeight.Bold)) { append("Bersama ") }
                    withStyle(SpanStyle(color = Color(0xFFEB4C94), fontWeight = FontWeight.Bold)) { append("Bahagia ") }
                    withStyle(SpanStyle(color = Color.Black, fontWeight = FontWeight.Bold)) { append("Selamanya") }
                },
                fontSize = 30.sp,
                fontFamily = Poppins
            )

            Spacer(Modifier.height(8.dp))
            Text(
                text = "LittleSteps tersedia untuk membantu orang tua dan buah hati.",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(24.dp))

            // Tombol Login / Sign Up
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onLoginClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) { Text("Login", fontSize = 16.sp) }

                OutlinedButton(
                    onClick = onSignUpClick,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) { Text("Sign Up", fontSize = 16.sp, color = Color(0xFFF987C5)) }
            }

            Spacer(Modifier.height(32.dp))

            // Divider dengan teks
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.weight(1f))
                Text(text = "atau Login dengan", color = Color.Gray, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 8.dp))
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp, modifier = Modifier.weight(1f))
            }

            Spacer(Modifier.height(32.dp))

            // Sosmed: Facebook & Google
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                // Facebook
                Button(
                    onClick = onFacebookClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(0.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.size(56.dp)
                ) {
                    Image(painter = painterResource(R.drawable.ic_facebook), contentDescription = "Facebook Login", modifier = Modifier.size(24.dp))
                }
                // Google
                Button(
                    onClick = onGoogleClick,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(0.dp),
                    border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
                    modifier = Modifier.size(56.dp)
                ) {
                    Image(painter = painterResource(R.drawable.ic_google), contentDescription = "Google Login", modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}
