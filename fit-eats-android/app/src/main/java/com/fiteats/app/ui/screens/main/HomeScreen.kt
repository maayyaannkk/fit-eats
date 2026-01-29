package com.fiteats.app.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.fiteats.app.models.CalorieOverview
import com.fiteats.app.models.DashboardModel
import com.fiteats.app.models.MacroItem
import com.fiteats.app.models.Macros
import com.fiteats.app.models.Meal
import com.fiteats.app.models.ProgressItem
import com.fiteats.app.models.ProgressSummary
import com.fiteats.app.models.User
import com.fiteats.app.models.progressPercentage
import com.fiteats.app.ui.viewModel.DashboardViewModel

@Composable
fun HomeScreen(onMealClick: (Meal) -> Unit) {
    val viewModel: DashboardViewModel = viewModel()

    val dashboard by viewModel.dashboardData.collectAsState(initial = null)
    val isLoading by viewModel.isLoading.collectAsState(initial = false)
    val apiError by viewModel.apiError.collectAsState(initial = null)

    LaunchedEffect(Unit) {
        viewModel.getDashboard()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                windowInsets = WindowInsets(0)
            )
        }
    ) { paddingValue ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValue),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            apiError != null -> {
                Text(
                    text = apiError ?: "Unknown error",
                    modifier = Modifier.padding(16.dp)
                )
            }

            dashboard != null -> {
                DashboardScreen(
                    data = dashboard!!,
                    modifier = Modifier.padding(paddingValue),
                    onMealClick
                )
            }
        }

    }
}

@Composable
fun DashboardScreen(
    data: DashboardModel,
    modifier: Modifier = Modifier,
    onMealClick: (Meal) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DashboardHeader(data.user) }
        item { ProgressCards(data.progressSummary) }
        item { CalorieOverviewCard(data.calorieOverview) }
        item { MealsHeader() }
        items(data.todayMeals) {
            MealCard(it, onMealClick)
        }
    }
}

@Composable
fun DashboardHeader(user: User) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    text = "Hello, ${user.name}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = user.greeting.uppercase(),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ProgressCards(summary: ProgressSummary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ProgressCard(
            title = "Weight (kg)",
            progressItem = summary.weightInKg,
            modifier = Modifier.weight(1f)
        )

        ProgressCard(
            title = "Body Fat (%)",
            progressItem = summary.bodyFatPercentage,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ProgressCard(
    title: String,
    progressItem: ProgressItem,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .height(110.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium)

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${progressItem.current.toInt()}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "/ ${progressItem.goal.toInt()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            LinearProgressIndicator(
                progress = { progressItem.progressPercentage() / 100 },
                modifier = Modifier.fillMaxWidth(),
                color = ProgressIndicatorDefaults.linearColor,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}

@Composable
fun CalorieOverviewCard(data: CalorieOverview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calorie Overview",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { (data.total.consumed * 1f) / data.total.goal },
                    modifier = Modifier.fillMaxSize(),
                    color = ProgressIndicatorDefaults.circularColor,
                    strokeWidth = 10.dp,
                    trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                    strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = data.total.consumed.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "of ${data.total.goal} kcal",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${data.total.goal - data.total.consumed} kcal remaining",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            MacroProgressRow(data.macros)
        }
    }
}

@Composable
fun MacroProgressRow(macros: Macros) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        MacroItemView("Protein", macros.protein, modifier = Modifier.weight(1f))
        MacroItemView("Carb", macros.carbs, modifier = Modifier.weight(1f))
        MacroItemView("Fats", macros.fats, modifier = Modifier.weight(1f))
    }
}

@Composable
fun MacroItemView(label: String, item: MacroItem, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(
            text = "${item.consumed}/${item.goal}${item.unit}",
            style = MaterialTheme.typography.bodySmall
        )
        LinearProgressIndicator(
            progress = { (item.consumed * 1f) / item.goal },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            color = ProgressIndicatorDefaults.linearColor,
            trackColor = ProgressIndicatorDefaults.linearTrackColor,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        )
    }
}

@Composable
fun MealsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Today’s Meals",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun MealCard(meal: Meal, onMealClick: (Meal) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = { onMealClick.invoke(meal) })
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(meal.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(strokeWidth = 2.dp)
                    }
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("N/A", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = "${meal.time} • ${meal.calories} kcal",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (meal.isConsumed)
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            else
                Icon(Icons.Default.ChevronRight, contentDescription = null)

        }
    }
}