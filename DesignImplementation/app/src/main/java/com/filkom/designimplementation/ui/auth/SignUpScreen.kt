package com.filkom.designimplementation.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filkom.designimplementation.R
import com.filkom.designimplementation.ui.components.SocialCircleButton
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.auth.LoginState
import com.filkom.designimplementation.viewmodel.auth.LoginViewModel
import com.filkom.designimplementation.viewmodel.auth.SignUpViewModel
import com.filkom.designimplementation.viewmodel.auth.SignUpState

@Composable
fun SignUpScreen(
    viewModelGoogle: LoginViewModel = viewModel(),
    viewModel: SignUpViewModel = viewModel(),
    onSuccess: (String, String?, String?) -> Unit,
    onFailed: (String) -> Unit,
    onToLogin: () -> Unit = {},

    onGoogle: () -> Unit = {},
    onFacebook: () -> Unit = {}
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.signUpState.collectAsState()
    val googleState by viewModelGoogle.loginState.collectAsState()

    LaunchedEffect(state) {
        when (state) {
            is SignUpState.Success -> {
                val user = (state as SignUpState.Success).user
                onSuccess(user.id, user.email, user.name) // Panggil Callback Sukses
            }
            is SignUpState.Failed -> {
                val errorMsg = (state as SignUpState.Failed).message
                onFailed(errorMsg) // Panggil Callback Gagal
            }
            else -> {}
        }
    }

    LaunchedEffect(googleState) {
        when (googleState) {
            is LoginState.Success -> {
                val user = (googleState as LoginState.Success).user
                onSuccess(user.id, user.email, user.name) // Panggil Callback Sukses
            }
            is LoginState.Failed -> {
                val errorMsg = (googleState as LoginState.Failed).message
                onFailed(errorMsg) // Panggil Callback Gagal
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
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

        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Buat Akun Kamu!",
                color = Color(0xFFB1608C),
                fontFamily = Poppins,
                fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Daftar untuk akses penuh ke fitur menarik dan pengalaman terbaik dari aplikasi kami.",
                color = Color(0xFF8F8F93),
                fontFamily = Poppins,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(Modifier.height(16.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            FieldLabel("Nama Lengkap")
            OutlinedTextField(
                value = name, onValueChange = { name = it },
                placeholder = { Text("Masukan nama", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            FieldLabel("Email")
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                placeholder = { Text("Masukan email", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(14.dp))

            FieldLabel("Password")
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                placeholder = { Text("Masukan password", fontFamily = Poppins) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = {
                    viewModel.signUpUser(name, email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF987C5))
            ) {
                if (state is SignUpState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign Up", fontFamily = Poppins, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }

            if (state is SignUpState.Failed) {
                Text(
                    text = (state as SignUpState.Failed).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                )
            }

            Spacer(Modifier.height(22.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.weight(1f))
                Text(
                    "atau Login dengan",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = Color(0xFF8F8F93),
                    fontFamily = Poppins,
                    fontSize = 12.sp
                )
                Spacer(Modifier.weight(1f))
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SocialCircleButton(R.drawable.ic_facebook, "Facebook") {
                    onFacebook()
                }
                SocialCircleButton(R.drawable.ic_google, "Google") {
                    val webClientId = context.getString(R.string.default_web_client_id)
                    viewModelGoogle.signInWithGoogle(context, webClientId)
                }
            }

            if (googleState is LoginState.Loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), color = Color(0xFFF987C5))
            }
            if (googleState is LoginState.Failed) {
                Text(
                    text = (googleState as LoginState.Failed).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp)
                )
            }

            Spacer(Modifier.height(22.dp))

            TextButton(
                onClick = onToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Sudah punya akun? ",
                    color = Color(0xFF6A6A6B),
                    fontFamily = Poppins,
                    fontSize = 12.sp
                )
                Text(
                    text = "Login",
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
private fun FieldLabel(text: String) {
    Text(text,
        fontFamily = Poppins,
        fontSize = 12.sp,
        color = Color(0xFF6A6A6B),
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(6.dp))
}