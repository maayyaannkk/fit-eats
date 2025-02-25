package models

import "go.mongodb.org/mongo-driver/bson/primitive"

type User struct {
	ID           primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`
	Name         string             `bson:"name" json:"name" validate:"required,min=3,max=50"`
	Email        string             `bson:"email" json:"email" validate:"required,email"`
	Password     string             `bson:"password" json:"password,omitempty" validate:"required,min=6"`
	Age          string             `bson:"age" json:"age"`
	Sex          string             `bson:"sex" json:"sex"`
	Country      string             `bson:"country" json:"country"`
	RefreshToken string             `bson:"refreshToken" json:"refreshToken,omitempty"`
}
