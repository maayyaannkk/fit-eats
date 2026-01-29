package repositories

import (
	"context"
	"fit-eats-api/models"
	"fmt"
	"time"

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

func (r *MealRepository) GetSingleDayMealByDate(ctx context.Context, userId primitive.ObjectID) (*models.DayMeal, error) {

	loc, err1 := time.LoadLocation("Asia/Kolkata")
	if err1 != nil {
		panic(err1)
	}

	nowIST := time.Now().In(loc)

	startOfDay := time.Date(
		nowIST.Year(),
		nowIST.Month(),
		nowIST.Day(),
		0, 0, 0, 0,
		loc,
	)
	endOfDay := startOfDay.Add(24 * time.Hour)

	fmt.Println(startOfDay.String())
	fmt.Println(endOfDay.String())

	filter := bson.M{
		"userId": userId,
		"dayMeals.date": bson.M{
			"$gte": startOfDay,
			"$lt":  endOfDay,
		},
	}

	projection := bson.M{
		"dayMeals": bson.M{
			"$filter": bson.M{
				"input": "$dayMeals",
				"as":    "dm",
				"cond": bson.M{
					"$and": []bson.M{
						{"$gte": []any{"$$dm.date", startOfDay}},
						{"$lt": []any{"$$dm.date", endOfDay}},
					},
				},
			},
		},
	}

	var result struct {
		DayMeals []models.DayMeal `bson:"dayMeals"`
	}

	err := r.Collection.
		FindOne(ctx, filter, options.FindOne().SetProjection(projection)).
		Decode(&result)

	if err == mongo.ErrNoDocuments || len(result.DayMeals) == 0 {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}

	return &result.DayMeals[0], nil
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
