package com.fiteats.app.ui.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MealScreen() {
    Text(text = "Meal Screen", modifier = Modifier.fillMaxSize(), textAlign = TextAlign.Center)
}

@Preview
@Composable
fun MealScreenPreview() {
    MealScreen()
}