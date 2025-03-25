package com.jorgeguerra.youtubetutoriales.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jorgeguerra.youtubetutoriales.R

val miFont = FontFamily(
    Font(R.font.orbitronvariablefontwght, FontWeight.Normal),
    Font(R.font.orbitronvariablefontwght, FontWeight.Bold) // Opcional si tienes variantes
)

// Set of Material typography styles to start with

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = miFont,  // Cambiado a tu fuente personalizada
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = miFont,  // Cambiado a tu fuente personalizada
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = miFont,  // Cambiado a tu fuente personalizada
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)