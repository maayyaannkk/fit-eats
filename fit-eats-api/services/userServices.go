package services

import (
	"context"
	"errors"

	"fit-eats-api/models"
	"fit-eats-api/repositories"
	"fit-eats-api/utils"

	"go.mongodb.org/mongo-driver/bson"
)

type UserService struct {
	UserRepo *repositories.UserRepository
}

func NewUserService(repo *repositories.UserRepository) *UserService {
	return &UserService{UserRepo: repo}
}

func (s *UserService) RegisterUser(ctx context.Context, user *models.User) error {
	hashedPassword, err := utils.GeneratePasswordHashFromPlainText(user.Password)
	if err != nil {
		return err
	}
	user.Password = hashedPassword
	return s.UserRepo.CreateUser(ctx, user)
}

func (s *UserService) Login(ctx context.Context, email, password string) (string, string, error) {
	user, err := s.UserRepo.GetUserByEmail(ctx, email)
	if err != nil {
		return "", "", errors.New("user not found. Please register to continue")
	}

	if !utils.IsPasswordCorrect(user.Password, password) {
		return "", "", errors.New("invalid credentials")
	}

	tokenString, err := utils.GenerateAccessJwt(user)
	if err != nil {
		return "", "", errors.New("unable to generate access token")
	}
	refreshString, err := utils.GenerateRefreshJwt(user)
	if err != nil {
		return "", "", errors.New("unable to generate refresh token")
	}
	err = s.UserRepo.UpdateUser(ctx, user.ID, bson.M{"refreshToken": refreshString})
	if err != nil {
		return "", "", errors.New("unable to save refresh token")
	}
	return tokenString, refreshString, err
}

func (s *UserService) RequestAccessToken(ctx context.Context, email string, refreshToken string) (string, error) {
	user, err := s.UserRepo.GetUserByEmail(ctx, email)
	if err != nil {
		return "", errors.New("user not found")
	}

	if !utils.IsRefreshTokenValid(refreshToken) {
		return "", errors.New("refresh token invalid")
	}

	if user.RefreshToken != refreshToken {
		return "", errors.New("invalid credentials")
	}

	tokenString, err := utils.GenerateAccessJwt(user)
	if err != nil {
		return "", errors.New("unable to generate access token")
	}
	return tokenString, err
}

func (s *UserService) RevokeAccessToken(ctx context.Context, email string) error {
	user, err := s.UserRepo.GetUserByEmail(ctx, email)
	if err != nil {
		return errors.New("user not found")
	}
	err = s.UserRepo.UpdateUser(ctx, user.ID, bson.M{"refreshToken": ""})
	if err != nil {
		return errors.New("could not revoke")
	}
	return err
}
