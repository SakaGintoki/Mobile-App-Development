package com.filkom.designimplementation.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.theme.Poppins

@Composable
fun ForgotPasswordScreen(
    onSubmit: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {

        // ===== HEADER (gradient image) =====
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

        // ===== Title & subtitle di bawah header =====
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Lupa Password?",
                color = Color(0xFFB1608C), // sesuai tema
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Masukkan email yang terdaftar, dan kami akan kirimkan link untuk atur ulang passwordmu.",
                color = Color(0xFF8F8F93),
                fontFamily = Poppins,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        // ===== Form =====
        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                "Email",
                fontFamily = Poppins,
                fontSize = 12.sp,
                color = Color(0xFF6A6A6B),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Masukan email", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = { onSubmit(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5))
            ) {
                Text("Kirim", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Kembali", color = Color(0xFF6A6A6B), fontFamily = Poppins, fontSize = 12.sp)
            }
        }
    }
}
