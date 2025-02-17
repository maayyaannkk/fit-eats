package utils

import (
	"log"
	"time"

	"github.com/golang-jwt/jwt/v5"
	"golang.org/x/crypto/bcrypt"

	"fit-eats-api/config"
	"fit-eats-api/models"
)

func GenerateAccessJwt(user *models.User) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"email": user.Email,
		"exp":   time.Now().Add(time.Minute * 15).Unix(),
	})
	secret := config.GetConfig().JWTAccessSecret
	tokenString, err := token.SignedString([]byte(secret))
	return tokenString, err
}

func IsAccessTokenValid(tokenString string) bool {
	secret := config.GetConfig().JWTAccessSecret

	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(secret), nil
	})

	if err != nil {
		log.Printf("Error parsing token: %v", err)
		return false
	}

	if !token.Valid {
		log.Printf("Error parsing token: invalid")
		return false
	}
	return true
}

func GenerateRefreshJwt(user *models.User) (string, error) {
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, jwt.MapClaims{
		"email": user.Email,
		"exp":   time.Now().Add(time.Hour * 24 * 7).Unix(), //1 week validity
	})
	secret := config.GetConfig().JWTRefreshSecret
	tokenString, err := token.SignedString([]byte(secret))
	return tokenString, err
}

func IsRefreshTokenValid(tokenString string) bool {
	secret := config.GetConfig().JWTRefreshSecret

	token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
		return []byte(secret), nil
	})

	if err != nil {
		log.Printf("Error parsing token: %v", err)
		return false
	}

	if !token.Valid {
		log.Printf("Error parsing token: invalid")
		return false
	}
	return true
}

func IsPasswordCorrect(hashedPassword string, plainTextPassword string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hashedPassword), []byte(plainTextPassword))
	return err == nil
}

func GeneratePasswordHashFromPlainText(plainTextPassword string) (string, error) {
	hashedPassword, err := bcrypt.GenerateFromPassword([]byte(plainTextPassword), bcrypt.DefaultCost)
	return string(hashedPassword), err
}
