package controllers

import (
	"encoding/json"
	"fit-eats-api/config"
	"fit-eats-api/repositories"
	"fit-eats-api/utils"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/generative-ai-go/genai"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type MealController struct {
	UserRepository     *repositories.UserRepository
	UserGoalRepository *repositories.UserGoalRepository
	UserMealRepository *repositories.MealRepository
}

func NewMealController(userRepository *repositories.UserRepository, userGoalRepository *repositories.UserGoalRepository, userMealRepository *repositories.MealRepository) *MealController {
	return &MealController{UserRepository: userRepository, UserGoalRepository: userGoalRepository, UserMealRepository: userMealRepository}
}

func (c *MealController) GetWeeklyMealPlan(ctx *gin.Context) {
	requiredFields := []string{"userId", "mainGoalId", "weeklyGoalId"}
	values := make(map[string]string)

	for _, field := range requiredFields {
		value, ok := ctx.GetQuery(field)
		if !ok {
			ctx.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("Invalid request format: missing %s", field)})
			return
		}
		values[field] = value
	}

	mongoUserIdStr := values["userId"]
	mainGoalIdStr := values["mainGoalId"]
	weeklyGoalIdStr := values["weeklyGoalId"]

	mongoUserId, err := primitive.ObjectIDFromHex(mongoUserIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid userId format: must be a valid ObjectId"})
		return
	}
	mongoMainGoalId, err := primitive.ObjectIDFromHex(mainGoalIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid mainGoalId format: must be a valid ObjectId"})
		return
	}
	mongoWeeklyGoalId, err := primitive.ObjectIDFromHex(weeklyGoalIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid weeklyGoalId format: must be a valid ObjectId"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	mealPlan, err1 := c.UserMealRepository.GetWeeklyMealPlan(timedContext, mongoUserId, mongoMainGoalId, mongoWeeklyGoalId)
	if err1 != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Meal Plan is not yet created"})
		return
	}
	ctx.JSON(http.StatusOK, mealPlan)
}

func (c *MealController) CreateWeeklyMealPlan(ctx *gin.Context) {
	requiredFields := []string{"userId", "mainGoalId", "weeklyGoalId"}
	values := make(map[string]string)

	for _, field := range requiredFields {
		value, ok := ctx.GetQuery(field)
		if !ok {
			ctx.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("Invalid request format: missing %s", field)})
			return
		}
		values[field] = value
	}

	mongoUserIdStr := values["userId"]
	mainGoalIdStr := values["mainGoalId"]
	weeklyGoalIdStr := values["weeklyGoalId"]

	mongoUserId, err := primitive.ObjectIDFromHex(mongoUserIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid userId format: must be a valid ObjectId"})
		return
	}
	mongoMainGoalId, err := primitive.ObjectIDFromHex(mainGoalIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid mainGoalId format: must be a valid ObjectId"})
		return
	}
	mongoWeeklyGoalId, err := primitive.ObjectIDFromHex(weeklyGoalIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid weeklyGoalId format: must be a valid ObjectId"})
		return
	}

	//120 seconds for llm to respond
	timedContext, cancel := config.GetTimedContext(120)
	defer cancel()

	user, err := c.UserRepository.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "User not found"})
		return
	}
	if !user.IsProfileComplete() {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Profile incomplete"})
		return
	}

	goal, err := c.UserGoalRepository.GetUserWeeklyGoal(timedContext, mongoMainGoalId, mongoWeeklyGoalId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Goal not found"})
		return
	}

	isAlreadyCreated := c.UserMealRepository.IsWeeklyMealPlanCreated(timedContext, mongoUserId, mongoWeeklyGoalId)
	if isAlreadyCreated {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Meal Plan is already created"})
		return
	}

	prompt := config.GetWeeklyMealPrompt(*user, float32(goal.WeeklyGoals[0].CurrentWeightInKg), float32(goal.WeeklyGoals[0].CurrentFatPercentage),
		float32(goal.TargetWeightInKg), float32(goal.TargetFatPercentage), int32(goal.WeeklyGoals[0].TargetDailyCalories),
		int32(goal.WeeklyGoals[0].TargetDailyMacrosFats), int32(goal.WeeklyGoals[0].TargetDailyMacrosCarbs), int32(goal.WeeklyGoals[0].TargetDailyMacrosProtein), string(goal.GoalType))

	resp, err := config.GetMealModel().GenerateContent(timedContext, genai.Text(prompt))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate content: " + err.Error()})
		return
	}

	if len(resp.Candidates) == 0 || len(resp.Candidates[0].Content.Parts) == 0 {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "No content generated by the model"})
		return
	}

	content, ok := resp.Candidates[0].Content.Parts[0].(genai.Text)
	if !ok {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Unexpected content format from the model"})
		return
	}

	var result map[string]any
	if err := json.Unmarshal([]byte(content), &result); err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to unmarshal response to JSON: " + err.Error()})
		return
	}

	mealPlan := utils.ParseMealPlanResponse(mongoUserId, mongoMainGoalId, mongoWeeklyGoalId, goal.WeeklyGoals[0].StartDate, result)
	err = c.UserMealRepository.CreateWeeklyMealPlan(timedContext, &mealPlan)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate content: " + err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, mealPlan)
}

func (c *MealController) CustomizeDayMealPlan(ctx *gin.Context) {
	requiredFields := []string{"mealPlanId", "dayMealId", "userPrompt"}
	values := make(map[string]string)

	for _, field := range requiredFields {
		value, ok := ctx.GetQuery(field)
		if !ok {
			ctx.JSON(http.StatusBadRequest, gin.H{"error": fmt.Sprintf("Invalid request format: missing %s", field)})
			return
		}
		values[field] = value
	}

	mealPlanIdStr := values["mealPlanId"]
	dayMealIdStr := values["dayMealId"]
	userPrompt := values["userPrompt"]

	mongoMealPlanId, err := primitive.ObjectIDFromHex(mealPlanIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid mealPlanId format: must be a valid ObjectId"})
		return
	}
	mongodayMealId, err := primitive.ObjectIDFromHex(dayMealIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid dayMealId format: must be a valid ObjectId"})
		return
	}

	// 120 seconds for llm to respond
	timedContext, cancel := config.GetTimedContext(120)
	defer cancel()

	mealPlan, err := c.UserMealRepository.GetMealPlanMeta(timedContext, mongoMealPlanId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Meal plan not found"})
		return
	}

	mongoUserId := mealPlan.UserId
	mongoMainGoalId := mealPlan.MainGoalId
	mongoWeeklyGoalId := mealPlan.WeeklyGoalId

	user, err := c.UserRepository.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "User not found"})
		return
	}
	if !user.IsProfileComplete() {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Profile incomplete"})
		return
	}

	goal, err := c.UserGoalRepository.GetUserWeeklyGoal(timedContext, mongoMainGoalId, mongoWeeklyGoalId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Goal not found"})
		return
	}

	dayMeal, err := c.UserMealRepository.GetSingleDayMeal(timedContext, mongoMainGoalId, mongoWeeklyGoalId, mongodayMealId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Goal not found"})
		return
	}

	jsonBytes, err := json.Marshal(dayMeal)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Day Meal plan not found"})
		return
	}

	prompt := config.GetSingleMealEditPrompt(*user, string(jsonBytes), userPrompt, float32(goal.WeeklyGoals[0].CurrentWeightInKg), float32(goal.WeeklyGoals[0].CurrentFatPercentage),
		float32(goal.TargetWeightInKg), float32(goal.TargetFatPercentage), int32(goal.WeeklyGoals[0].TargetDailyCalories),
		int32(goal.WeeklyGoals[0].TargetDailyMacrosFats), int32(goal.WeeklyGoals[0].TargetDailyMacrosCarbs), int32(goal.WeeklyGoals[0].TargetDailyMacrosProtein), string(goal.GoalType))

	resp, err := config.GetSingleMealModel().GenerateContent(timedContext, genai.Text(prompt))
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate content: " + err.Error()})
		return
	}

	if len(resp.Candidates) == 0 || len(resp.Candidates[0].Content.Parts) == 0 {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "No content generated by the model"})
		return
	}

	content, ok := resp.Candidates[0].Content.Parts[0].(genai.Text)
	if !ok {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Unexpected content format from the model"})
		return
	}

	var result map[string]any
	if err := json.Unmarshal([]byte(content), &result); err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to unmarshal response to JSON: " + err.Error()})
		return
	}

	dayMealNew := utils.ParseSingleMealPlanResponse(result)
	err = c.UserMealRepository.UpdateSingleDayMeal(timedContext, mongoMealPlanId, mongodayMealId, dayMealNew.Meals)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to generate content: " + err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"mealPlanId": mealPlan.ID, "mainGoalId": mealPlan.MainGoalId, "weeklyGoalId": mealPlan.WeeklyGoalId})
}
