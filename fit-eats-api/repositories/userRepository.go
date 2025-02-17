package repositories

import (
	"context"
	"fit-eats-api/models"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

type UserRepository struct {
	Collection *mongo.Collection
}

func NewUserRepository(db *mongo.Database) *UserRepository {
	return &UserRepository{
		Collection: db.Collection("users"),
	}
}

func (r *UserRepository) CreateUser(ctx context.Context, user *models.User) error {
	_, err := r.Collection.InsertOne(ctx, user)
	return err
}

func (r *UserRepository) GetUserByEmail(ctx context.Context, email string) (*models.User, error) {
	var user models.User
	err := r.Collection.FindOne(ctx, bson.M{"email": email}).Decode(&user)
	return &user, err
}

func (r *UserRepository) UpdateUser(ctx context.Context, userID primitive.ObjectID, updateData bson.M) error {
	filter := bson.M{"_id": userID} // Find user by ID

	update := bson.M{"$set": updateData} // Update fields

	_, err := r.Collection.UpdateOne(ctx, filter, update)
	return err
}
