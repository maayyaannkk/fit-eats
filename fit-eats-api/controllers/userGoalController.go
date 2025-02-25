package controllers

import (
	"fit-eats-api/config"
	"fit-eats-api/models"
	"fit-eats-api/services"
	"fit-eats-api/utils"
	"net/http"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type UserGoalController struct {
	UserGoalService *services.UserGoalService
}

func NewUserGoalController(service *services.UserGoalService) *UserGoalController {
	return &UserGoalController{UserGoalService: service}
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

func (c *UserGoalController) DeleteUserGoal(ctx *gin.Context) {
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
	err := c.UserGoalService.DeleteUserGoals(timedContext, mongoGoalId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get user"})
		return
	}

	ctx.JSON(http.StatusMovedPermanently, gin.H{"success": true})
}
