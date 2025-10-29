package com.filkom.designimplementation.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.typography.Poppins

@Composable
fun LoginScreen(
    onForgotPassword: () -> Unit = {},
    onLogin: (String, String) -> Unit = { _, _ -> },
    onFacebook: () -> Unit = {},
    onGoogle: () -> Unit = {},
    onToSignUp: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        // ================= HEADER (gambar gradasi) =================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.header),
                contentDescription = "Header Gradient",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // ================= TEKS DI BAWAH HEADER =================
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Selamat Datang!",
                color = Color(0xFFB1608C),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Masuk ke akunmu dan mulai jelajahi semua fitur dari aplikasi kami!",
                color = Color(0xFF8F8F93),
                fontFamily = Poppins,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        // ================= FORM =================
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Email
            Text("Email",
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Color(0xFF6A6A6B),
                fontWeight = FontWeight.Bold
                )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Masukan email", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            // Password
            Text("Password", fontFamily = Poppins, fontSize = 12.sp, color = Color(0xFF6A6A6B))
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Masukan password", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(6.dp))
            TextButton(
                onClick = onForgotPassword,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Lupa password?", color = Color(0xFFF987C5), fontFamily = Poppins, fontSize = 12.sp)
            }

            Spacer(Modifier.height(10.dp))

            // Tombol Login
            Button(
                onClick = { onLogin(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5))
            ) {
                Text("Login", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(22.dp))

            // Divider + teks tengah
            Row(verticalAlignment = Alignment.CenterVertically) {
                Divider(Modifier.weight(1f))
                Text(
                    "atau Login dengan",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = Color(0xFF8F8F93),
                    fontFamily = Poppins,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Divider(Modifier.weight(1f))
            }

            Spacer(Modifier.height(18.dp))

            // Sosial: Facebook & Google
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialCircleButton(R.drawable.ic_facebook, "Facebook", onFacebook)
                SocialCircleButton(R.drawable.ic_google, "Google", onGoogle)
            }

            Spacer(Modifier.height(22.dp))

            TextButton(
                onClick = onToSignUp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    color = Color(0xFF6A6A6B),
                    fontFamily = Poppins,
                    fontSize = 12.sp
                )
                Text(
                    text = "SignUp",
                    color = Color(0xFFF987C5),
                    fontFamily = Poppins,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun SocialCircleButton(iconRes: Int, contentDesc: String, onClick: () -> Unit) {
    OutlinedIconButton(
        onClick = onClick,
        shape = CircleShape,
        colors = IconButtonDefaults.outlinedIconButtonColors(),
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDesc,
            tint = Color.Unspecified,
            modifier = Modifier.size(22.dp)
        )
    }
}
