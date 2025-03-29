package com.fiteats.app.ui.screens.meal

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.fiteats.app.models.Ingredient
import com.fiteats.app.models.Meal
import com.fiteats.app.ui.custom.LoadingDialog
import com.fiteats.app.ui.viewModel.MealDetailViewModel

@Composable
fun MealDetailsScreen(navController: NavController, meal: Meal) {
    val viewModel: MealDetailViewModel = viewModel()
    val isLoading by viewModel.isLoading.observeAsState(initial = false)
    val error by viewModel.apiError.observeAsState(initial = "")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Column(
                            Modifier
                                .weight(0.8f)
                                .align(Alignment.Bottom)
                        ) {
                            Text(text = "Meal Details")
                        }
                        Column {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(
                                        horizontal = 8.dp,
                                        vertical = 2.dp
                                    )
                            ) {
                                Text(
                                    text = meal.time,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = meal.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = meal.description,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nutrition Information",
                fontWeight = FontWeight.Bold
            )
            NutritionInfo(
                calories = meal.calories,
                protein = meal.protein,
                carbs = meal.carbs,
                fat = meal.fat,
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Ingredients", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            meal.ingredients.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = it.name,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Text(
                        text = it.quantity,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Recipe Steps", fontWeight = FontWeight.Bold)

            meal.recipeSteps.forEachIndexed { index, step ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = step,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!meal.isConsumed) {
                if (isLoading) {
                    LoadingDialog()
                } else {
                    Button(
                        onClick = { viewModel.consumeMeal(meal.id!!) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text(text = "Mark as Complete", color = Color.White)
                    }
                }
            }

            if (!error.isNullOrEmpty())
                Toast.makeText(LocalContext.current, error, LENGTH_SHORT).show()

            LaunchedEffect(error) {
                if (error == null) navController.popBackStack()
            }
        }
    }
}

@Composable
fun NutritionInfo(calories: Int, protein: Int, carbs: Int, fat: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NutritionInfoBox(
            modifier = Modifier.weight(1f),
            "KCal",
            "$calories",
            Color(0xFF29779E)
        )
        NutritionInfoBox(
            modifier = Modifier.weight(1f),
            "Protein",
            "${protein}g",
            Color(0xFF81C784)
        )
        NutritionInfoBox(
            modifier = Modifier.weight(1f),
            "Carbs",
            "${carbs}g",
            Color(0xFFE57373)
        )
        NutritionInfoBox(
            modifier = Modifier.weight(1f),
            "Fat",
            "${fat}g",
            Color(0xFFF9A825)
        )
    }
}

@Composable
fun NutritionInfoBox(modifier: Modifier, label: String, value: String, color: Color) {
    Card(modifier = modifier.padding(8.dp), shape = RoundedCornerShape(4.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value, color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = label, fontSize = 12.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun MealDetailsScreenPreview() {
    MealDetailsScreen(
        rememberNavController(),
        Meal(
            id = "1",
            name = "Grilled Chicken Salad",
            description = "A healthy mix of grilled chicken breast with fresh vegetables and light vinaigrette dressing",
            calories = 450,
            carbs = 25,
            protein = 35,
            fat = 22,
            time = "12:45 PM",
            ingredients = listOf(
                Ingredient(name = "Chicken breast", quantity = "200g"),
                Ingredient(name = "Mixed lettuce", quantity = "100g"),
                Ingredient(name = "Cherry tomatoes", quantity = "50g"),
                Ingredient(name = "Cucumber", quantity = "50g"),
                Ingredient(name = "Olive oil", quantity = "15ml")
            ),
            recipeSteps = listOf(
                "Season chicken breast with salt and pepper",
                "Grill chicken for 6-8 minutes each side until cooked through",
                "Wash and chop all vegetables",
                "Mix vegetables in a bowl, slice chicken and arrange on top",
                "Drizzle with olive oil and serve"
            ),
            isConsumed = false
        )

    )
}