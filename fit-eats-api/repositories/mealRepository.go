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

func (r *MealRepository) UpdateSingleDayMeal(ctx context.Context, mealPlanId primitive.ObjectID,
	dayMealId primitive.ObjectID, meals []models.Meal) error {
	filter := bson.M{"_id": mealPlanId, "dayMeals._id": dayMealId} // Find by ID

	update := bson.M{
		"$set": bson.M{"dayMeals.$.meals": meals}, // Updating only the meals array
	}

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

func (r *MealRepository) GetSingleDayMeal(ctx context.Context, mainGoalId primitive.ObjectID, weeklyGoalId primitive.ObjectID, dayMealId primitive.ObjectID) (*models.DayMeal, error) {
	var mealPlan models.DayMeal

	err := r.Collection.FindOne(ctx, bson.M{"mainGoalId": mainGoalId, "weeklyGoalId": weeklyGoalId, "dayMeals._id": dayMealId}).Decode(&mealPlan)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, nil
		}
		return nil, err
	}

	return &mealPlan, nil
}

func (r *MealRepository) GetMealPlanMeta(ctx context.Context, mealPlanId primitive.ObjectID) (*models.MealPlan, error) {
	var mealPlan models.MealPlan

	err := r.Collection.FindOne(ctx, bson.M{"_id": mealPlanId}, options.FindOne().SetProjection(bson.M{"dayMeals": 0})).Decode(&mealPlan)
	if err != nil {
		if err == mongo.ErrNoDocuments {
			return nil, nil
		}
		return nil, err
	}

	return &mealPlan, nil
}

func (r *MealRepository) ConsumeSingleMeal(ctx context.Context, mealId primitive.ObjectID) error {
	filter := bson.M{"dayMeals.meals._id": mealId}

	update := bson.M{"$set": bson.M{"dayMeals.$[].meals.$[meal].isConsumed": true}}

	options := options.Update().SetArrayFilters(options.ArrayFilters{
		Filters: []interface{}{
			bson.M{"meal._id": mealId},
		},
	})

	_, err := r.Collection.UpdateOne(ctx, filter, update, options)
	return err
}
