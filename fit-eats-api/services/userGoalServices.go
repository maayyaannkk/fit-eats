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
	return s.UserGoalRepo.CreateMainUserGoal(ctx, userGoal)
}

func (s *UserGoalService) RegisterWeeklyGoal(ctx context.Context, mongoMainGoalId primitive.ObjectID, userGoal *models.WeeklyGoal) error {
	return s.UserGoalRepo.CreateWeeklyUserGoal(ctx, mongoMainGoalId, userGoal)
}

func (s *UserGoalService) GetUserGoals(ctx context.Context, userId primitive.ObjectID) ([]models.Goal, error) {
	user, err := s.UserGoalRepo.GetUserGoalsByUserId(ctx, userId)
	if err != nil {
		return nil, errors.New("goals not found")
	}
	return user, err
}

func (s *UserGoalService) DeleteUserMainGoal(ctx context.Context, goalId primitive.ObjectID) error {
	err := s.UserGoalRepo.DeleteMainUserGoal(ctx, goalId)
	if err != nil {
		return errors.New("goals not found")
	}
	return nil
}

func (s *UserGoalService) DeleteUserWeeklyGoal(ctx context.Context, goalId primitive.ObjectID, weeklyGoalId primitive.ObjectID) error {
	err := s.UserGoalRepo.DeleteWeeklyUserGoal(ctx, goalId, weeklyGoalId)
	if err != nil {
		return errors.New("goals not found")
	}
	return nil
}
