package repositories

import (
	"context"
	"fit-eats-api/models"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

type UserGoalRepository struct {
	Collection *mongo.Collection
}

func NewUserGoalRepository(db *mongo.Database) *UserGoalRepository {
	return &UserGoalRepository{
		Collection: db.Collection("userGoals"),
	}
}

func (r *UserGoalRepository) CreateMainUserGoal(ctx context.Context, mainGoal *models.Goal) error {
	_, err := r.Collection.InsertOne(ctx, mainGoal)
	return err
}

func (r *UserGoalRepository) CreateWeeklyUserGoal(ctx context.Context, mainGoalId primitive.ObjectID, weeklyGoal *models.WeeklyGoal) error {
	weeklyGoal.ID = primitive.NewObjectID() // Generate a new ID for the weekly goal

	// Ensure 'weeklyGoals' is an array before pushing a new item
	filter := bson.M{"_id": mainGoalId, "$or": []bson.M{{"weeklyGoals": bson.M{"$exists": false}}, {"weeklyGoals": nil}}}
	initUpdate := bson.M{"$set": bson.M{"weeklyGoals": bson.A{}}}

	_, _ = r.Collection.UpdateOne(ctx, filter, initUpdate) // Set only if 'weeklyGoals' does not exist

	// Now push the new weekly goal into the array
	update := bson.M{"$push": bson.M{"weeklyGoals": weeklyGoal}}

	result, err := r.Collection.UpdateOne(ctx, bson.M{"_id": mainGoalId}, update)
	if err != nil {
		return err
	}

	if result.ModifiedCount == 0 {
		return mongo.ErrNoDocuments // No document found to update
	}

	return nil
}

func (r *UserGoalRepository) GetUserGoalByUserId(ctx context.Context, mongoUserId primitive.ObjectID) (*models.Goal, error) {
	var userGoal models.Goal
	err := r.Collection.FindOne(ctx, bson.M{"userId": mongoUserId}).Decode(&userGoal)

	if err != nil {
		return nil, err
	}

	return &userGoal, nil
}

func (r *UserGoalRepository) DeleteMainUserGoal(ctx context.Context, goalId primitive.ObjectID) error {
	filter := bson.M{"_id": goalId} // Find by ID
	_, err := r.Collection.DeleteOne(ctx, filter)
	return err
}

func (r *UserGoalRepository) DeleteWeeklyUserGoal(ctx context.Context, mainGoalId primitive.ObjectID, weeklyGoalId primitive.ObjectID) error {
	filter := bson.M{"_id": mainGoalId, "weeklyGoals._id": weeklyGoalId} // Find by ID
	_, err := r.Collection.DeleteOne(ctx, filter)
	return err
}
