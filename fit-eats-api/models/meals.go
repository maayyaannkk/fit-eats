package models

import "go.mongodb.org/mongo-driver/bson/primitive"

type MealPlan struct {
	ID primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`

	UserId       primitive.ObjectID `bson:"userId" json:"userId"`
	MainGoalId   primitive.ObjectID `bson:"mainGoalId" json:"mainGoalId"`
	WeeklyGoalId primitive.ObjectID `bson:"weeklyGoalId" json:"weeklyGoalId"`

	Meals []Meal `bson:"meals" json:"meals"`
}

type Meal struct {
	ID          primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`
	Name        string             `bson:"name" json:"name"`
	Description string             `bson:"description" json:"description"`
	Calories    int                `bson:"calories" json:"calories"`
	Carbs       int                `bson:"carbs" json:"carbs"`
	Protein     int                `bson:"protein" json:"protein"`
	Fat         int                `bson:"fat" json:"fat"`
	Time        string             `bson:"time" json:"time"`
	Ingredients []Ingredient       `bson:"ingredients" json:"ingredients"`
	RecipeSteps []string           `bson:"recipe_steps" json:"recipe_steps"`
}

type Ingredient struct {
	Name     string `bson:"name" json:"name"`
	Quantity string `bson:"quantity" json:"quantity"`
}
