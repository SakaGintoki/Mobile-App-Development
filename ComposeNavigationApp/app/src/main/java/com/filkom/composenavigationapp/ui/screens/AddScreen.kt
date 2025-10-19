package com.filkom.composenavigationapp.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AddScreen(navController: NavController) {

    Column(

        Modifier

            .fillMaxSize()

            .padding(20.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center

    ) {

        Text("Add Screen (contoh form tambah data)", style = MaterialTheme.typography.titleLarge)



        Spacer(Modifier.height(16.dp))



        Button(onClick = { navController.popBackStack() }) {

            Text("Selesai (Kembali)")

        }

    }

}
