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

func (r *UserGoalRepository) CreateUserGoal(ctx context.Context, user *models.Goal) error {
	_, err := r.Collection.InsertOne(ctx, user)
	return err
}

func (r *UserGoalRepository) GetUserGoalsByUserId(ctx context.Context, mongoUserId primitive.ObjectID) ([]models.Goal, error) {
	var userGoals []models.Goal

	cursor, err := r.Collection.Find(ctx, bson.M{"userId": mongoUserId})
	if err != nil {
		return nil, err
	}
	defer cursor.Close(ctx)

	if err = cursor.All(ctx, &userGoals); err != nil {
		return nil, err
	}

	return userGoals, nil
}

func (r *UserGoalRepository) UpdateUserGoal(ctx context.Context, goalId primitive.ObjectID, updateData bson.M) error {
	filter := bson.M{"_id": goalId} // Find by ID

	update := bson.M{"$set": updateData} // Update fields

	_, err := r.Collection.UpdateOne(ctx, filter, update)
	return err
}

func (r *UserGoalRepository) DeleteUserGoal(ctx context.Context, goalId primitive.ObjectID) error {
	filter := bson.M{"_id": goalId} // Find by ID
	_, err := r.Collection.DeleteOne(ctx, filter)
	return err
}
