package main

import (
	"fmt"
	"log"

	"fit-eats-api/config"
	"fit-eats-api/controllers"
	"fit-eats-api/repositories"
	"fit-eats-api/routes"
	"fit-eats-api/services"

	"github.com/gin-gonic/gin"
)

func main() {
	// Load configuration
	cfg := config.GetConfig()

	// Connect to MongoDB using config
	client := config.ConnectDB(cfg)
	db := client.Database(cfg.Database)
	fmt.Println("Connected to MongoDB:", cfg.Database)

	// Initialize repositories, services, and controllers
	userRepo := repositories.NewUserRepository(db)
	userService := services.NewUserService(userRepo)
	userController := controllers.NewUserController(userService)

	// Set up Gin router
	router := gin.Default()

	// Define API routes
	routes.SetupRoutes(router, userController)

	// Start the server
	fmt.Println("Server is running on port " + cfg.Port)
	if err := router.Run(":" + cfg.Port); err != nil {
		log.Fatal("Server failed to start:", err)
	}
}
