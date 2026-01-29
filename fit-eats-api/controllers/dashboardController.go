package controllers

import (
	"fit-eats-api/config"
	"fit-eats-api/models"
	"fit-eats-api/repositories"
	"net/http"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type DashboardController struct {
	UserRepository     *repositories.UserRepository
	UserGoalRepository *repositories.UserGoalRepository
	MealRepository     *repositories.MealRepository
}

func NewDashboardController(userRepository *repositories.UserRepository, userGoalRepository *repositories.UserGoalRepository, mealRepository *repositories.MealRepository) *DashboardController {
	return &DashboardController{UserRepository: userRepository, UserGoalRepository: userGoalRepository, MealRepository: mealRepository}
}

func (c *DashboardController) GetDashboard(ctx *gin.Context) {
	userId, error := ctx.GetQuery("userId")
	if !error {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}
	mongoUserId, error1 := primitive.ObjectIDFromHex(userId)
	if error1 != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserRepository.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get user info"})
		return
	}

	mainGoal, err := c.UserGoalRepository.GetUserActiveGoalByUserId(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get active goal"})
		return
	}

	weeklyGoal := mainGoal.WeeklyGoals[0]

	dayMeal, err := c.MealRepository.GetSingleDayMealByDate(timedContext, mongoUserId)
	if err != nil || dayMeal == nil || len(dayMeal.Meals) == 0 {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get active meal plan"})
		return
	}

	totalCalories := 0
	totalProtein := 0
	totalCarbs := 0
	totalFats := 0
	consumedCalories := 0
	consumedProtein := 0
	consumedCarbs := 0
	consumedFats := 0
	for _, meal := range dayMeal.Meals {
		totalCalories += meal.Calories
		totalProtein += meal.Protein
		totalCarbs += meal.Carbs
		totalFats += meal.Fat
		if meal.IsConsumed {
			consumedCalories += meal.Calories
			consumedProtein += meal.Protein
			consumedCarbs += meal.Carbs
			consumedFats += meal.Fat
		}
	}

	dashboardResponse := models.DashboardResponse{
		UserInfo: models.UserInfoSection{
			Name:     user.Name,
			Greeting: "Practice makes perfect",
		},
		ProgressSummary: models.ProgressSummary{
			WeightInKg: models.MetricProgress{
				Current: weeklyGoal.CurrentWeightInKg,
				Last:    weeklyGoal.CurrentWeightInKg,
				Goal:    mainGoal.TargetWeightInKg,
				Start:   mainGoal.StartWeightInKg,
			},
			BodyFatPercentage: models.MetricProgress{
				Current: weeklyGoal.CurrentFatPercentage,
				Last:    weeklyGoal.CurrentFatPercentage,
				Goal:    mainGoal.TargetFatPercentage,
				Start:   mainGoal.StartFatPercentage,
			},
		},
		CalorieOverview: models.CalorieOverview{
			Total: models.CalorieData{
				Consumed: float64(consumedCalories),
				Goal:     float64(totalCalories),
			},
			Macros: models.MacroData{
				Protein: models.MacroItem{
					Consumed: float64(consumedProtein),
					Goal:     float64(totalProtein),
					Unit:     "g",
				},
				Carbs: models.MacroItem{
					Consumed: float64(consumedCarbs),
					Goal:     float64(totalCarbs),
					Unit:     "g",
				},
				Fats: models.MacroItem{
					Consumed: float64(consumedFats),
					Goal:     float64(totalFats),
					Unit:     "g",
				},
			},
		},
		TodayMeals: dayMeal.Meals,
	}

	ctx.JSON(http.StatusOK, dashboardResponse)
}
