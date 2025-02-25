//TODO create new handlers for goal events (add new goal, list goals, edit goal, start end date).

//TODO create schema for recipe.
//TODO create API handler for recipes, creation, changes though prompt. take output as daily, and weekly after user confirmation.
//TODO create prompt to get recipe details from llm.

// TODO integate with some AI API, to fetch and save recipes.

package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Custom type for GoalDuration
type GoalDurationType string

// Constants for GoalDuration
const (
	Weekly  GoalDurationType = "weekly"
	Monthly GoalDurationType = "monthly"
	Overall GoalDurationType = "overall"
)

type Goal struct {
	ID     primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`
	UserId primitive.ObjectID `bson:"userId" json:"userId"`

	GoalDuration         GoalDurationType `bson:"goalDuration" json:"goalDuration"`
	CurrentWeightInKg    float64          `bson:"currentWeightInKg" json:"currentWeightInKg"`
	CurrentFatPercentage float64          `bson:"currentFatPercentage" json:"currentFatPercentage"`

	GoalStartDate time.Time `bson:"goalStartDate" json:"goalStartDate"`
	GoalEndDate   time.Time `bson:"goalEndDate" json:"goalEndDate"`

	DailyMaintenanceCalories float64 `bson:"dailyMaintenanceCalories" json:"dailyMaintenanceCalories"`

	TargetDailyCalories      float64 `bson:"targetDailyCalories" json:"targetDailyCalories"`
	TargetDailyMacrosProtein float64 `bson:"targetDailyMacrosProtein" json:"targetDailyMacrosProtein"`
	TargetDailyMacrosCarbs   float64 `bson:"targetDailyMacrosCarbs" json:"targetDailyMacrosCarbs"`
	TargetDailyMacrosFats    float64 `bson:"targetDailyMacrosFats" json:"targetDailyMacrosFats"`

	WorkoutRoutine string `bson:"workoutRoutine" json:"workoutRoutine"`
}
