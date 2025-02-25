package services

import (
	"context"
	"errors"

	"fit-eats-api/models"
	"fit-eats-api/repositories"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type UserGoalService struct {
	UserGoalRepo *repositories.UserGoalRepository
}

func NewUserGoalService(repo *repositories.UserGoalRepository) *UserGoalService {
	return &UserGoalService{UserGoalRepo: repo}
}

func (s *UserGoalService) RegisterUserGoal(ctx context.Context, userGoal *models.Goal) error {
	return s.UserGoalRepo.CreateUserGoal(ctx, userGoal)
}

func (s *UserGoalService) GetUserGoals(ctx context.Context, userId primitive.ObjectID) ([]models.Goal, error) {
	user, err := s.UserGoalRepo.GetUserGoalsByUserId(ctx, userId)
	if err != nil {
		return nil, errors.New("goals not found")
	}
	return user, err
}

func (s *UserGoalService) DeleteUserGoals(ctx context.Context, goalId primitive.ObjectID) error {
	err := s.UserGoalRepo.DeleteUserGoal(ctx, goalId)
	if err != nil {
		return errors.New("goals not found")
	}
}
