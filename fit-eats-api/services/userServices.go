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

func (s *UserService) UpdateUser(ctx context.Context, user *models.User) error {
	update := bson.M{}

	if user.Name != "" {
		update["name"] = user.Name
	}
	if user.Age != "" {
		update["age"] = user.Age
	}
	if user.Sex != "" {
		update["sex"] = user.Sex
	}
	if user.HeightInCm != 0 {
		update["heightInCm"] = user.HeightInCm
	}
	if user.Country != "" {
		update["country"] = user.Country
	}

	if len(update) == 0 {
		return nil // Nothing to update
	}

	return s.UserRepo.UpdateUser(ctx, user.ID, update)
}

func (s *UserService) Login(ctx context.Context, email, password string) (*models.User, string, string, error) {
	user, err := s.UserRepo.GetUserByEmail(ctx, email)
	if err != nil {
		return nil, "", "", errors.New("user not found. Please register to continue")
	}

	if !utils.IsPasswordCorrect(user.Password, password) {
		return nil, "", "", errors.New("invalid credentials")
	}

	tokenString, err := utils.GenerateAccessJwt(user)
	if err != nil {
		return nil, "", "", errors.New("unable to generate access token")
	}

	refreshString, err := utils.GenerateRefreshJwt(user)
	if err != nil {
		return nil, "", "", errors.New("unable to generate refresh token")
	}

	err = s.UserRepo.UpdateUser(ctx, user.ID, bson.M{"refreshToken": refreshString})
	if err != nil {
		return nil, "", "", errors.New("unable to save refresh token")
	}

	user, err = s.UserRepo.GetUserProfileById(ctx, user.ID)
	if err != nil {
		return nil, "", "", errors.New("unable to get user token")
	}
	return user, tokenString, refreshString, err
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

func (s *UserService) GetUser(ctx context.Context, email string) (*models.User, error) {
	user, err := s.UserRepo.GetUserProfileByEmailId(ctx, email)
	if err != nil {
		return nil, errors.New("user not found")
	}
	return user, err
}
