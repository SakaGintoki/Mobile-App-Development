package com.filkom.designimplementation.ui.feature.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.filkom.designimplementation.ui.components.formatRupiah
import com.filkom.designimplementation.ui.theme.Pink
import com.filkom.designimplementation.ui.theme.Poppins
import com.filkom.designimplementation.viewmodel.feature.checkout.CheckoutViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    viewModel: CheckoutViewModel,
    onBack: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val totalPayment = viewModel.getTotalPayment()

    LaunchedEffect(viewModel.transactionState) {
        if (viewModel.transactionState == "success") {
            onSuccess()
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pilih Metode Bayar", fontFamily = Poppins, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 16.dp, color = Color.White) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text("Total", fontFamily = Poppins, color = Color.Gray)
                        Text(formatRupiah(totalPayment), fontFamily = Poppins, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.processPayment() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Pink),
                        shape = RoundedCornerShape(25.dp),
                        enabled = viewModel.selectedPaymentMethod != null && viewModel.transactionState != "loading"
                    ) {
                        if (viewModel.transactionState == "loading") {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Bayar", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Saldo Aplikasi (Otomatis Potong)", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                PaymentOptionCard(
                    name = "Saldo App",
                    isSelected = viewModel.selectedPaymentMethod == "Saldo App",
                    onSelect = {
                        viewModel.selectedPaymentMethod = "Saldo App"
                        viewModel.paymentType = "internal" // Tandai ini internal
                    }
                )
            }

            // 2. ATM / Transfer
            item {
                Text("ATM/Transfer (Virtual Account)", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                val banks = listOf("BCA", "BNI", "BRI", "Mandiri")
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray)) {
                    Column {
                        banks.forEach { bank ->
                            PaymentOptionItem(
                                name = bank,
                                isSelected = viewModel.selectedPaymentMethod == bank,
                                onSelect = {
                                    viewModel.selectedPaymentMethod = bank
                                    viewModel.paymentType = "external" // Tandai ini eksternal
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }

            // 3. E-Money
            item {
                Text("E-Money", fontFamily = Poppins, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                val wallets = listOf("Gopay", "Dana", "ShopeePay", "OVO")
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray)) {
                    Column {
                        wallets.forEach { wallet ->
                            PaymentOptionItem(
                                name = wallet,
                                isSelected = viewModel.selectedPaymentMethod == wallet,
                                onSelect = {
                                    viewModel.selectedPaymentMethod = wallet
                                    viewModel.paymentType = "external" // Tandai ini eksternal
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                        }
                    }
                }
            }
            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun PaymentOptionCard(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSelect() }
            .border(1.dp, if (isSelected) Pink else Color.LightGray, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFF0F5) else Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, modifier = Modifier.weight(1f), fontFamily = Poppins)
            RadioButton(selected = isSelected, onClick = onSelect, colors = RadioButtonDefaults.colors(selectedColor = Pink))
        }
    }
}

@Composable
fun PaymentOptionItem(name: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable (
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bisa tambahkan Icon Bank disini jika ada resource-nya
        Text(name, modifier = Modifier.weight(1f), fontFamily = Poppins, fontWeight = FontWeight.Medium)

        // Checkbox style seperti di gambar (Kotak)
        Box(
            modifier = Modifier
                .size(20.dp)
                .border(1.5.dp, if(isSelected) Pink else Color.Gray, RoundedCornerShape(4.dp))
                .background(if(isSelected) Pink else Color.White, RoundedCornerShape(4.dp))
        )
    }
}