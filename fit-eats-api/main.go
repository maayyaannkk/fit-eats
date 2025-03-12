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

/*
Controller: Receives HTTP requests, validates input, calls the service layer to perform actions, and returns HTTP responses. It's the entry point for the API.
Service: Contains the business logic of the application.
Repository: Deals directly with data persistence (e.g., MongoDB). It abstracts away the database implementation details.

Data Flow:

+----------+     +-----------+     +----------+     +------------+
| HTTP     | --> | Controller| --> | Service  | --> | Repository | --> MongoDB
| Request  |     |           |     |          |     |            |
+----------+     +-----------+     +----------+     +------------+
                                                      ^
                                                      |
+----------+     +-----------+     +----------+     +------------+
| HTTP     | <-- | Controller| <-- | Service  | <-- | Repository | <-- MongoDB
| Response |     |           |     |          |     |            |
+----------+     +-----------+     +----------+     +------------+

*/

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

	// Initialize repositories, services, and controllers
	userGoalRepo := repositories.NewUserGoalRepository(db)
	userGoalService := services.NewUserGoalService(userGoalRepo)
	userGoalController := controllers.NewUserGoalController(userGoalService, userService)

	// Set up Gin router
	router := gin.Default()

	// Define API routes
	routes.SetupUserRoutes(router, userController)
	routes.SetupUserGoalRoutes(router, userGoalController)

	// Start the server
	fmt.Println("Server is running on port " + cfg.Port)
	if err := router.Run(":" + cfg.Port); err != nil {
		log.Fatal("Server failed to start:", err)
	}
}
