package repositories

import (
	"context"
	"fit-eats-api/models"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type MealRepository struct {
	Collection *mongo.Collection
}

func NewMealRepository(db *mongo.Database) *MealRepository {
	return &MealRepository{
		Collection: db.Collection("meals"),
	}
}

func (r *MealRepository) CreateWeeklyMealPlan(ctx context.Context, mealPlan *models.MealPlan) error {
	_, err := r.Collection.InsertOne(ctx, mealPlan)
	return err
}

// TODO recheck this
func (r *MealRepository) UpdateSingleDayMeal(ctx context.Context, weeklyGoalId primitive.ObjectID, mealId primitive.ObjectID, updateData bson.M) error {
	filter := bson.M{"weeklyGoalId": weeklyGoalId, "dayMeals.meals._id": mealId} // Find by ID

	update := bson.M{"$set": updateData} // Update fields

	_, err := r.Collection.UpdateOne(ctx, filter, update)
	return err
}

func (r *MealRepository) IsWeeklyMealPlanCreated(ctx context.Context, userId primitive.ObjectID, weeklyGoalId primitive.ObjectID) bool {
	var mealPlan models.MealPlan
	err := r.Collection.FindOne(ctx, bson.M{"userId": userId, "weeklyGoalId": weeklyGoalId}, options.FindOne().SetProjection(bson.M{"dayMeals": 0})).Decode(&mealPlan)

	return err == nil
}

func (r *MealRepository) GetWeeklyMealPlan(ctx context.Context, userId primitive.ObjectID, mainGoalId primitive.ObjectID, weeklyGoalId primitive.ObjectID) (*models.MealPlan, error) {
	var mealPlan models.MealPlan

	err := r.Collection.FindOne(ctx, bson.M{"userId": userId, "mainGoalId": mainGoalId, "weeklyGoalId": weeklyGoalId}).Decode(&mealPlan)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, nil
		}
		return nil, err
	}

	return &mealPlan, nil
}
