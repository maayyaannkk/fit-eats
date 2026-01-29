package main

import (
	"fmt"
	"log"

	"fit-eats-api/config"
	"fit-eats-api/controllers"
	"fit-eats-api/repositories"
	"fit-eats-api/routes"

	"github.com/gin-gonic/gin"
)

/*
Controller: Receives HTTP requests, validates input, calls the service layer to perform actions, and returns HTTP responses. It's the entry point for the API.
Service: Contains the business logic of the application.
Repository: Deals directly with data persistence (e.g., MongoDB). It abstracts away the database implementation details.

Data Flow:

+----------+     +-----------+     +------------+
| HTTP     | --> | Controller| --> | Repository | --> MongoDB
| Request  |     |           |     |            |
+----------+     +-----------+     +------------+
                                         ^
                                         |
+----------+     +-----------+     +------------+
| HTTP     | <-- | Controller| <-- | Repository | <-- MongoDB
| Response |     |           |     |            |
+----------+     +-----------+     +------------+

*/

func main() {
	// Load configuration
	cfg := config.GetConfig()

	// Connect to MongoDB using config
	client := config.ConnectDB(cfg)
	db := client.Database(cfg.Database)
	fmt.Println("Connected to MongoDB:", cfg.Database)

	// Initialize repositories, and controllers
	userRepo := repositories.NewUserRepository(db)
	userController := controllers.NewUserController(userRepo)

	// Initialize repositories, and controllers
	userGoalRepo := repositories.NewUserGoalRepository(db)
	userGoalController := controllers.NewUserGoalController(userRepo, userGoalRepo)

	// Initialize repositories, and controllers
	mealRepo := repositories.NewMealRepository(db)
	mealController := controllers.NewMealController(userRepo, userGoalRepo, mealRepo)

	dashboardController := controllers.NewDashboardController(userRepo, userGoalRepo, mealRepo)

	// Set up Gin router
	router := gin.Default()

	// Define API routes
	routes.SetupUserRoutes(router, userController)
	routes.SetupUserGoalRoutes(router, userGoalController)
	routes.SetupMealRoutes(router, mealController)
	routes.SetupDashboardRoutes(router, dashboardController)

	// Start the server
	fmt.Println("Server is running on port " + cfg.Port)
	if err := router.Run(":" + cfg.Port); err != nil {
		log.Fatal("Server failed to start:", err)
	}
}
