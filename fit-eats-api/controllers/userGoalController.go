package controllers

import (
	"encoding/json"
	"fit-eats-api/config"
	"fit-eats-api/models"
	"fit-eats-api/services"
	"fit-eats-api/utils"
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/google/generative-ai-go/genai"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type UserGoalController struct {
	UserService     *services.UserService
	UserGoalService *services.UserGoalService
}

func NewUserGoalController(service *services.UserGoalService, userService *services.UserService) *UserGoalController {
	return &UserGoalController{UserGoalService: service, UserService: userService}
}

func (c *UserGoalController) GetUserGoals(ctx *gin.Context) {
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

	// Register user
	goals, err := c.UserGoalService.GetUserGoals(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get user"})
		return
	}

	ctx.JSON(http.StatusCreated, gin.H{"userGoals": goals})
}

func (c *UserGoalController) RegisterUserGoal(ctx *gin.Context) {
	var userGoal models.Goal
	if err := ctx.ShouldBindJSON(&userGoal); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	errors := utils.ValidateStruct(userGoal)
	if errors != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"errors": errors})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	// Register user
	err := c.UserGoalService.RegisterUserGoal(timedContext, &userGoal)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not register goal"})
		return
	}

	ctx.JSON(http.StatusCreated, gin.H{"message": "Goal registered successfully"})
}

func (c *UserGoalController) RegisterWeeklyUserGoal(ctx *gin.Context) {
	var userGoal models.WeeklyGoal
	if err := ctx.ShouldBindJSON(&userGoal); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	errors := utils.ValidateStruct(userGoal)
	if errors != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"errors": errors})
		return
	}

	mainGoalIdStr := ctx.Query("mainGoalId")
	if mainGoalIdStr == "" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "mainGoalId is required"})
		return
	}
	mainGoalIdMongo, err1 := primitive.ObjectIDFromHex(mainGoalIdStr)
	if err1 != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "mainGoalId is invalid"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	// Register user
	err := c.UserGoalService.RegisterWeeklyGoal(timedContext, mainGoalIdMongo, &userGoal)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not register goal"})
		return
	}

	ctx.JSON(http.StatusCreated, gin.H{"message": "Goal registered successfully"})
}

func (c *UserGoalController) DeleteUserMainGoal(ctx *gin.Context) {
	goalId, error := ctx.GetQuery("goalId")
	if !error {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}
	mongoGoalId, error1 := primitive.ObjectIDFromHex(goalId)
	if error1 != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	// Register user
	err := c.UserGoalService.DeleteUserMainGoal(timedContext, mongoGoalId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get goal"})
		return
	}

	ctx.JSON(http.StatusMovedPermanently, gin.H{"success": true})
}
func (c *UserGoalController) DeleteUserWeeklyGoal(ctx *gin.Context) {
	goalId, error := ctx.GetQuery("goalId")
	if !error {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}
	mongoGoalId, error1 := primitive.ObjectIDFromHex(goalId)
	if error1 != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	weeklyGoalId, error := ctx.GetQuery("weeklyGoalId")
	if !error {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}
	mongoWeeklyGoalId, error1 := primitive.ObjectIDFromHex(weeklyGoalId)
	if error1 != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	// Register user
	err := c.UserGoalService.DeleteUserWeeklyGoal(timedContext, mongoGoalId, mongoWeeklyGoalId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get goal"})
		return
	}

	ctx.JSON(http.StatusMovedPermanently, gin.H{"success": true})
}
func (c *UserGoalController) GetIdealWeightRange(ctx *gin.Context) {
	mongoUserIdStr, ok := ctx.GetQuery("userId")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing userId"})
		return
	}
	mongoUserId, err := primitive.ObjectIDFromHex(mongoUserIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid userId format: must be a valid ObjectId"})
		return
	}
	currentWeightInKgStr, ok := ctx.GetQuery("currentWeightInKg")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentWeightInKg"})
		return
	}

	currentWeightInKg, err := strconv.ParseFloat(currentWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	currentBodyFatPercentageStr, ok := ctx.GetQuery("currentBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	currentBodyFatPercentage, err := strconv.ParseFloat(currentBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	if currentBodyFatPercentage < 10 || currentBodyFatPercentage > 80 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentBodyFatPercentage must be between 10 and 80"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserService.UserRepo.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "User not found"})
		return
	}
	if !user.IsProfileComplete() {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Profile incomplete"})
		return
	}

	prompt := config.GetWeightRangePrompt(*user, float32(currentWeightInKg), float32(currentBodyFatPercentage))

	resp, err := config.GetWeightRangeModel().GenerateContent(timedContext, genai.Text(prompt))
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

	jsonData, err := json.Marshal(content)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to marshal response to JSON: " + err.Error()})
		return
	}

	ctx.Data(http.StatusOK, "application/json", jsonData)
}

func (c *UserGoalController) GetGoalDuration(ctx *gin.Context) {
	mongoUserIdStr, ok := ctx.GetQuery("userId")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing userId"})
		return
	}
	mongoUserId, err := primitive.ObjectIDFromHex(mongoUserIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid userId format: must be a valid ObjectId"})
		return
	}
	currentWeightInKgStr, ok := ctx.GetQuery("currentWeightInKg")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentWeightInKg"})
		return
	}
	goalWeightInKgStr, ok := ctx.GetQuery("goalWeightInKgStr")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing goalWeightInKgStr"})
		return
	}

	currentWeightInKg, err := strconv.ParseFloat(currentWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	goalWeightInKg, err := strconv.ParseFloat(goalWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid goalWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	currentBodyFatPercentageStr, ok := ctx.GetQuery("currentBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	currentBodyFatPercentage, err := strconv.ParseFloat(currentBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	goalBodyFatPercentageStr, ok := ctx.GetQuery("goalBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	goalBodyFatPercentage, err := strconv.ParseFloat(goalBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	if currentBodyFatPercentage < 10 || currentBodyFatPercentage > 80 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentBodyFatPercentage must be between 10 and 80"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserService.UserRepo.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "User not found"})
		return
	}
	if !user.IsProfileComplete() {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Profile incomplete"})
		return
	}

	prompt := config.GetGoalDurationPrompt(*user, float32(currentWeightInKg), float32(currentBodyFatPercentage), float32(goalWeightInKg), float32(goalBodyFatPercentage))

	resp, err := config.GetGoalDurationModel().GenerateContent(timedContext, genai.Text(prompt))
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

	jsonData, err := json.Marshal(content)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to marshal response to JSON: " + err.Error()})
		return
	}

	ctx.Data(http.StatusOK, "application/json", jsonData)
}

func (c *UserGoalController) GetTdee(ctx *gin.Context) {
	mongoUserIdStr, ok := ctx.GetQuery("userId")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing userId"})
		return
	}
	mongoUserId, err := primitive.ObjectIDFromHex(mongoUserIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid userId format: must be a valid ObjectId"})
		return
	}
	currentWeightInKgStr, ok := ctx.GetQuery("currentWeightInKg")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentWeightInKg"})
		return
	}
	goalWeightInKgStr, ok := ctx.GetQuery("goalWeightInKgStr")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing goalWeightInKgStr"})
		return
	}
	goalType, ok := ctx.GetQuery("goalType")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing goalType"})
		return
	}

	currentWeightInKg, err := strconv.ParseFloat(currentWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	goalWeightInKg, err := strconv.ParseFloat(goalWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid goalWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	currentBodyFatPercentageStr, ok := ctx.GetQuery("currentBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	currentBodyFatPercentage, err := strconv.ParseFloat(currentBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	goalBodyFatPercentageStr, ok := ctx.GetQuery("goalBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	goalBodyFatPercentage, err := strconv.ParseFloat(goalBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	if currentBodyFatPercentage < 10 || currentBodyFatPercentage > 80 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentBodyFatPercentage must be between 10 and 80"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserService.UserRepo.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "User not found"})
		return
	}
	if !user.IsProfileComplete() {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Profile incomplete"})
		return
	}

	prompt := config.GetTdeePrompt(*user, float32(currentWeightInKg), float32(currentBodyFatPercentage), float32(goalWeightInKg), float32(goalBodyFatPercentage), goalType)

	resp, err := config.GetTdeeModel().GenerateContent(timedContext, genai.Text(prompt))
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

	jsonData, err := json.Marshal(content)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to marshal response to JSON: " + err.Error()})
		return
	}

	ctx.Data(http.StatusOK, "application/json", jsonData)
}

func (c *UserGoalController) GetMacros(ctx *gin.Context) {
	mongoUserIdStr, ok := ctx.GetQuery("userId")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing userId"})
		return
	}
	mongoUserId, err := primitive.ObjectIDFromHex(mongoUserIdStr)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid userId format: must be a valid ObjectId"})
		return
	}
	currentWeightInKgStr, ok := ctx.GetQuery("currentWeightInKg")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentWeightInKg"})
		return
	}
	goalWeightInKgStr, ok := ctx.GetQuery("goalWeightInKgStr")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing goalWeightInKgStr"})
		return
	}
	goalType, ok := ctx.GetQuery("goalType")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing goalType"})
		return
	}

	currentWeightInKg, err := strconv.ParseFloat(currentWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	goalWeightInKg, err := strconv.ParseFloat(goalWeightInKgStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid goalWeightInKg format: must be a number"})
		return
	}

	if currentWeightInKg < 30 || currentWeightInKg > 250 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentWeightInKg must be between 30 and 250"})
		return
	}

	currentBodyFatPercentageStr, ok := ctx.GetQuery("currentBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	currentBodyFatPercentage, err := strconv.ParseFloat(currentBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	goalBodyFatPercentageStr, ok := ctx.GetQuery("goalBodyFatPercentage")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBodyFatPercentage"})
		return
	}

	goalBodyFatPercentage, err := strconv.ParseFloat(goalBodyFatPercentageStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBodyFatPercentage format: must be a number"})
		return
	}

	if currentBodyFatPercentage < 10 || currentBodyFatPercentage > 80 {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "currentBodyFatPercentage must be between 10 and 80"})
		return
	}

	currentBmrStr, ok := ctx.GetQuery("currentBmr")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentBmr"})
		return
	}

	currentBmr, err := strconv.ParseInt(currentBmrStr, 10, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentBmr format: must be an integer"})
		return
	}

	currentTdeeStr, ok := ctx.GetQuery("currentTdee")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing currentTdee"})
		return
	}

	currentTdee, err := strconv.ParseInt(currentTdeeStr, 10, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid currentTdee format: must be an integer"})
		return
	}

	weightChangeStr, ok := ctx.GetQuery("weightChange")
	if !ok {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format: missing weightChange"})
		return
	}

	weightChange, err := strconv.ParseFloat(weightChangeStr, 32)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid weightChange format: must be a number"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserService.UserRepo.GetUserProfileById(timedContext, mongoUserId)
	if err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "User not found"})
		return
	}
	if !user.IsProfileComplete() {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Profile incomplete"})
		return
	}

	prompt := config.GetDailyMacroPrompt(*user, float32(currentWeightInKg), float32(currentBodyFatPercentage),
		float32(goalWeightInKg), float32(goalBodyFatPercentage), goalType,
		int32(currentBmr), int32(currentTdee), float32(weightChange))

	resp, err := config.GetMacroModel().GenerateContent(timedContext, genai.Text(prompt))
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

	jsonData, err := json.Marshal(content)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Failed to marshal response to JSON: " + err.Error()})
		return
	}

	ctx.Data(http.StatusOK, "application/json", jsonData)
}
