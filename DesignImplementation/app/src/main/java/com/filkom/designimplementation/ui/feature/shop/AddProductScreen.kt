package com.filkom.designimplementation.ui.feature.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.shop.AddProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    viewModel: AddProductViewModel = viewModel(),
    onBack: () -> Unit = {}
) {
    LaunchedEffect(viewModel.isSuccess) {
        if (viewModel.isSuccess) {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Tambah Produk", fontFamily = Poppins, fontWeight = FontWeight.Bold, color = Pink)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Pink)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()), // Agar bisa discroll jika form panjang
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontFamily = Poppins
                )
            }

            Text("Informasi Produk", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Poppins)

            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Nama Produk") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.subtitle,
                onValueChange = { viewModel.subtitle = it },
                label = { Text("Subtitle (cth: 30 Kapsul)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("Harga", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Poppins)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = viewModel.price,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.price = it },
                    label = { Text("Harga Jual (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = viewModel.originalPrice,
                    onValueChange = { if (it.all { char -> char.isDigit() }) viewModel.originalPrice = it },
                    label = { Text("Harga Coret (Opsional)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = viewModel.category,
                onValueChange = { viewModel.category = it },
                label = { Text("Kategori (cth: Suplemen, Obat)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.description,
                onValueChange = { viewModel.description = it },
                label = { Text("Deskripsi Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Text("Foto Produk (URL)", fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = Poppins)
            Text("Copy link gambar dari Google/Internet lalu paste disini.", fontSize = 12.sp, color = Color.Gray)

            OutlinedTextField(
                value = viewModel.image1,
                onValueChange = { viewModel.image1 = it },
                label = { Text("URL Foto Utama (Wajib)") },
                placeholder = { Text("https://...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.image2,
                onValueChange = { viewModel.image2 = it },
                label = { Text("URL Foto 2 (Opsional)") },
                placeholder = { Text("https://...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = viewModel.image3,
                onValueChange = { viewModel.image3 = it },
                label = { Text("URL Foto 3 (Opsional)") },
                placeholder = { Text("https://...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { viewModel.saveProduct() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Pink),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Simpan Produk", fontFamily = Poppins, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}