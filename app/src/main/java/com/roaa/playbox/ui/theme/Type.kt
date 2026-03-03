package com.roaa.playbox.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.roaa.playbox.R

val InterVariable = FontFamily(
    Font(
        resId = R.font.inter_variable,
        weight = FontWeight.Normal
    )
)

// 2️⃣ Create base typography instance
private val DefaultTypography = Typography()

// 3️⃣ Override styles
val AppTypography = DefaultTypography.copy(
    bodyLarge = DefaultTypography.bodyLarge.copy(
        fontFamily = InterVariable,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = DefaultTypography.bodyMedium.copy(
        fontFamily = InterVariable,
        fontWeight = FontWeight.SemiBold
    ),
    bodySmall = DefaultTypography.bodySmall.copy(
        fontFamily = InterVariable
    ),
    titleLarge = DefaultTypography.titleLarge.copy(
        fontFamily = InterVariable,
        fontWeight = FontWeight.SemiBold
    ),
    headlineMedium = DefaultTypography.headlineMedium.copy(
        fontFamily = InterVariable,
        fontWeight = FontWeight.Bold
    )
)