package com.filkom.designimplementation.typography

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.filkom.designimplementation.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_italic, FontWeight.Light),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_thin, FontWeight.Thin),
)