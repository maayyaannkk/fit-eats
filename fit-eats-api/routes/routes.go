package routes

import (
	"fit-eats-api/controllers"
	"fit-eats-api/middleware"

	"github.com/gin-gonic/gin"
)

func SetupUserRoutes(router *gin.Engine, userController *controllers.UserController) {
	api := router.Group("/api")
	{
		api.POST("/register", userController.Register)
		api.POST("/login", userController.Login)
		api.POST("/requestAccessToken", userController.RequestAccessToken)

		protected := api.Group("/")
		protected.Use(middleware.AuthMiddleware()) // Apply JWT auth middleware
		{
			protected.PUT("/profile", userController.UpdateUser)
			protected.GET("/profile", userController.GetUser)
			protected.POST("/logout", userController.LogoutUser)
		}
	}
}

func SetupUserGoalRoutes(router *gin.Engine, userGoalController *controllers.UserGoalController) {
	protected := router.Group("/api")
	protected.Use(middleware.AuthMiddleware()) // Apply JWT auth middleware
	{
		protected.GET("/getIdealWeight", userGoalController.GetIdealWeightRange)
		protected.GET("/getGoalDuration", userGoalController.GetGoalDuration)
		protected.GET("/getTdee", userGoalController.GetTdee)
		protected.GET("/getMacros", userGoalController.GetMacros)

		protected.POST("/registerMainGoal", userGoalController.RegisterUserGoal)
		protected.POST("/registerWeeklyGoal", userGoalController.RegisterWeeklyUserGoal)

		protected.GET("/getGoals", userGoalController.GetUserGoals)
		protected.GET("/getActiveGoal", userGoalController.GetActiveUserGoal)
		protected.DELETE("/deleteMainGoal", userGoalController.DeleteUserMainGoal)
		protected.DELETE("/deleteWeeklyGoal", userGoalController.DeleteUserWeeklyGoal)
	}
}

func SetupMealRoutes(router *gin.Engine, mealController *controllers.MealController) {
	protected := router.Group("/api")
	protected.Use(middleware.AuthMiddleware()) // Apply JWT auth middleware
	{
		protected.GET("/getMealPlan", mealController.GetWeeklyMealPlan)
		protected.POST("/createMealPlan", mealController.CreateWeeklyMealPlan)
		protected.POST("/customizeMealPlan", mealController.CustomizeDayMealPlan)
	}
}
