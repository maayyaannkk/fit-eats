//TODO create API handler for recipes, creation, changes though prompt. take output as daily, and weekly after user confirmation.
//TODO create prompt to get recipe details from llm.

// TODO integate with some AI API, to fetch and save recipes.

package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type GoalType string

const (
	Weekly  GoalType = "Fat loss"
	Monthly GoalType = "Muscle gain"
)

type Goal struct {
	ID     primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`
	UserId primitive.ObjectID `bson:"userId" json:"userId"`

	StartWeightInKg    float64 `bson:"startWeightInKg" json:"startWeightInKg"`
	StartFatPercentage float64 `bson:"startFatPercentage" json:"startFatPercentage"`

	TargetWeightInKg    float64 `bson:"targetWeightInKg" json:"targetWeightInKg"`
	TargetFatPercentage float64 `bson:"targetFatPercentage" json:"targetFatPercentage"`

	GoalStartDate time.Time `bson:"goalStartDate" json:"goalStartDate"`
	GoalEndDate   time.Time `bson:"goalEndDate" json:"goalEndDate"`

	GoalType           GoalType `bson:"goalType" json:"goalType"`
	WeeklyWeightChange float64  `bson:"weeklyWeightChange" json:"weeklyWeightChange"`

	WeeklyGoals []WeeklyGoal `bson:"weeklyGoals" json:"weeklyGoals"`
}

type WeeklyGoal struct {
	ID primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`

	StartDate time.Time `bson:"startDate" json:"startDate"`
	EndDate   time.Time `bson:"endDate" json:"endDate"`

	CurrentWeightInKg    float64 `bson:"currentWeightInKg" json:"currentWeightInKg"`
	CurrentFatPercentage float64 `bson:"currentFatPercentage" json:"currentFatPercentage"`

	DailyMaintenanceCalories float64 `bson:"dailyMaintenanceCalories" json:"dailyMaintenanceCalories"`
	TargetDailyCalories      float64 `bson:"targetDailyCalories" json:"targetDailyCalories"`

	TargetDailyMacrosProtein float64 `bson:"targetDailyMacrosProtein" json:"targetDailyMacrosProtein"`
	TargetDailyMacrosCarbs   float64 `bson:"targetDailyMacrosCarbs" json:"targetDailyMacrosCarbs"`
	TargetDailyMacrosFats    float64 `bson:"targetDailyMacrosFats" json:"targetDailyMacrosFats"`

	WorkoutRoutine string `bson:"workoutRoutine" json:"workoutRoutine"`
}
