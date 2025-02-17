package routes

import (
	"fit-eats-api/controllers"
	"fit-eats-api/middleware"

	"github.com/gin-gonic/gin"
)

func SetupRoutes(router *gin.Engine, userController *controllers.UserController) {
	api := router.Group("/api")
	{
		api.POST("/register", userController.Register)
		api.POST("/login", userController.Login)
		api.POST("/requestAccessToken", userController.RequestAccessToken)

		protected := api.Group("/")
		protected.Use(middleware.AuthMiddleware()) // Apply JWT auth middleware
		{
			protected.GET("/profile", func(ctx *gin.Context) {
				ctx.JSON(200, gin.H{"message": "Welcome to your profile!"})
			})
			protected.POST("/logout", userController.LogoutUser)
		}
	}
}
