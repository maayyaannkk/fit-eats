package models

import "go.mongodb.org/mongo-driver/bson/primitive"

type User struct {
	ID              primitive.ObjectID `bson:"_id,omitempty" json:"id,omitempty"`
	Name            string             `bson:"name" json:"name" validate:"required,min=3,max=50"`
	Email           string             `bson:"email" json:"email" validate:"required,email"`
	Password        string             `bson:"password" json:"password,omitempty" validate:"required,min=6"`
	HeightInCm      float64            `bson:"heightInCm" json:"heightInCm,omitempty"`
	Age             string             `bson:"age" json:"age,omitempty"`
	Sex             string             `bson:"sex" json:"sex,omitempty"`
	Country         string             `bson:"country" json:"country,omitempty"`
	DietPreferences []string           `bson:"dietPreferences" json:"dietPreferences,omitempty"`
	RefreshToken    string             `bson:"refreshToken" json:"refreshToken,omitempty"`
}
