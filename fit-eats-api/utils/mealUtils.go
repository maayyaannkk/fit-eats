package utils

import (
	"fit-eats-api/models"
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Convert dayOfWeek to actual date based on given start date
func getDateFromDayOfWeek(startDate time.Time, dayOfWeek string) time.Time {
	days := map[string]int{
		"Monday":    0,
		"Tuesday":   1,
		"Wednesday": 2,
		"Thursday":  3,
		"Friday":    4,
		"Saturday":  5,
		"Sunday":    6,
	}

	if offset, exists := days[dayOfWeek]; exists {
		return startDate.AddDate(0, 0, offset)
	}

	return startDate // Default to start date if invalid input
}

func ParseMealPlanResponse(userId, mainGoalId, weeklyGoalId primitive.ObjectID, startDate time.Time, response map[string]any) models.MealPlan {
	var mealPlan models.MealPlan
	mealPlan.ID = primitive.NewObjectID()
	mealPlan.UserId = userId
	mealPlan.MainGoalId = mainGoalId
	mealPlan.WeeklyGoalId = weeklyGoalId

	if mealPlans, ok := response["mealPlans"].([]any); ok {
		for _, mp := range mealPlans {
			if dayMealData, ok := mp.(map[string]any); ok {
				dayOfWeek, _ := dayMealData["dayOfWeek"].(string)
				date := getDateFromDayOfWeek(startDate, dayOfWeek)

				var dayMeal models.DayMeal
				dayMeal.ID = primitive.NewObjectID()
				dayMeal.Date = date

				if meals, ok := dayMealData["meals"].([]any); ok {
					for _, mealData := range meals {
						if mealMap, ok := mealData.(map[string]any); ok {
							meal := models.Meal{
								ID:          primitive.NewObjectID(),
								Name:        mealMap["name"].(string),
								Description: mealMap["description"].(string),
								Calories:    int(mealMap["calories"].(float64)),
								Carbs:       int(mealMap["carbs"].(float64)),
								Protein:     int(mealMap["protein"].(float64)),
								Fat:         int(mealMap["fat"].(float64)),
								Time:        mealMap["time"].(string),
							}

							// Parse ingredients
							if ingredients, ok := mealMap["ingredients"].([]any); ok {
								for _, ing := range ingredients {
									if ingMap, ok := ing.(map[string]any); ok {
										meal.Ingredients = append(meal.Ingredients, models.Ingredient{
											Name:     ingMap["name"].(string),
											Quantity: ingMap["quantity"].(string),
										})
									}
								}
							}

							// Parse recipe steps
							if steps, ok := mealMap["recipe_steps"].([]any); ok {
								for _, step := range steps {
									meal.RecipeSteps = append(meal.RecipeSteps, step.(string))
								}
							}

							dayMeal.Meals = append(dayMeal.Meals, meal)
						}
					}
				}
				mealPlan.DayMeals = append(mealPlan.DayMeals, dayMeal)
			}
		}
	}

	return mealPlan
}

func ParseSingleMealPlanResponse(response map[string]any) models.DayMeal {
	var dayMeal models.DayMeal

	if meals, ok := response["meals"].([]any); ok {
		for _, mealData := range meals {
			if mealMap, ok := mealData.(map[string]any); ok {
				meal := models.Meal{
					ID:          primitive.NewObjectID(),
					Name:        mealMap["name"].(string),
					Description: mealMap["description"].(string),
					Calories:    int(mealMap["calories"].(float64)),
					Carbs:       int(mealMap["carbs"].(float64)),
					Protein:     int(mealMap["protein"].(float64)),
					Fat:         int(mealMap["fat"].(float64)),
					Time:        mealMap["time"].(string),
				}

				// Parse ingredients
				if ingredients, ok := mealMap["ingredients"].([]any); ok {
					for _, ing := range ingredients {
						if ingMap, ok := ing.(map[string]any); ok {
							meal.Ingredients = append(meal.Ingredients, models.Ingredient{
								Name:     ingMap["name"].(string),
								Quantity: ingMap["quantity"].(string),
							})
						}
					}
				}

				// Parse recipe steps
				if steps, ok := mealMap["recipe_steps"].([]any); ok {
					for _, step := range steps {
						meal.RecipeSteps = append(meal.RecipeSteps, step.(string))
					}
				}

				dayMeal.Meals = append(dayMeal.Meals, meal)
			}
		}
	}
	return dayMeal
}
