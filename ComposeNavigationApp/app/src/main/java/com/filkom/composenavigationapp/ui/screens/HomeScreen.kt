package com.filkom.composenavigationapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.filkom.composenavigationapp.ui.navigation.Routes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {

    val items = remember {

        listOf(

            "A01" to "Judul Item 1",

            "A02" to "Judul Item 2",

            "A03" to "Judul Item 3",

            "A04" to "Judul Item 4",

            "A05" to "Judul Item 5"

        )

    }


    LazyColumn(

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(12.dp),

        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {

        items(items, key = { it.first }) { (id, title) ->

            Card(

                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),

                modifier = Modifier

                    .fillMaxWidth()

                    .clickable {

                        navController.navigate("${Routes.DETAIL}/$id") }

            ) {

                Column(Modifier.padding(16.dp)) {

                    Text(title, style = MaterialTheme.typography.titleMedium)

                    Spacer(Modifier.height(4.dp))

                    Text("ID: $id", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                }

            }

        }

    }

}
