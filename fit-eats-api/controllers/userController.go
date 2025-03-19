package controllers

import (
	"fit-eats-api/config"
	"fit-eats-api/models"
	"fit-eats-api/repositories"
	"fit-eats-api/utils"

	"net/http"

	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
)

type UserController struct {
	UserRepository *repositories.UserRepository
}

func NewUserController(repository *repositories.UserRepository) *UserController {
	return &UserController{UserRepository: repository}
}

func (c *UserController) Register(ctx *gin.Context) {
	var user models.User
	if err := ctx.ShouldBindJSON(&user); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	// Validate input
	errors := utils.ValidateStruct(user)
	if errors != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"errors": errors})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	hashedPassword, err1 := utils.GeneratePasswordHashFromPlainText(user.Password)
	if err1 != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not generate user password"})
		return
	}
	user.Password = hashedPassword

	// Register user
	err := c.UserRepository.CreateUser(timedContext, &user)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not register user"})
		return
	}

	ctx.JSON(http.StatusCreated, gin.H{"message": "User registered successfully"})
}

func (c *UserController) Login(ctx *gin.Context) {
	email := ctx.PostForm("email")
	password := ctx.PostForm("password")
	if email == "" || password == "" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "missing body params"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserRepository.GetUserByEmail(timedContext, email)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "user not found. Please register to continue"})
		return
	}

	if !utils.IsPasswordCorrect(user.Password, password) {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "invalid Credentials"})
		return
	}

	accessToken, err := utils.GenerateAccessJwt(user)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "unable to generate access token"})
		return
	}

	refreshToken, err := utils.GenerateRefreshJwt(user)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "unable to generate refresh token"})
		return
	}

	err = c.UserRepository.UpdateUser(ctx, user.ID, bson.M{"refreshToken": refreshToken})
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "unable to save refresh token"})
		return
	}

	user, err = c.UserRepository.GetUserProfileById(ctx, user.ID)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "unable to get user token"})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"accessToken": accessToken, "refreshToken": refreshToken, "user": user})
}

func (c *UserController) RequestAccessToken(ctx *gin.Context) {
	emailId := ctx.PostForm("emailId")
	refreshToken := ctx.PostForm("refreshToken")
	if refreshToken == "" || emailId == "" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "missing body params"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserRepository.GetUserByEmail(timedContext, emailId)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "user not found"})
		return
	}

	if !utils.IsRefreshTokenValid(refreshToken) {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "refresh token invalid"})
		return
	}

	if user.RefreshToken != refreshToken {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "invalid credentials"})
		return
	}

	token, err := utils.GenerateAccessJwt(user)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "unable to generate access toke"})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"accessToken": token, "refreshToken": refreshToken})
}

func (c *UserController) LogoutUser(ctx *gin.Context) {
	emailId := ctx.PostForm("emailId")
	if emailId == "" {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "missing body params"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	user, err := c.UserRepository.GetUserByEmail(timedContext, emailId)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "user not found"})
		return
	}
	err = c.UserRepository.UpdateUser(timedContext, user.ID, bson.M{"refreshToken": ""})
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": "could not revoke"})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"message": "Logout user"})
}

func (c *UserController) UpdateUser(ctx *gin.Context) {
	var user models.User
	if err := ctx.ShouldBindJSON(&user); err != nil {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

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
	if user.DietPreferences != nil {
		update["dietPreferences"] = user.DietPreferences
	}

	if len(update) == 0 {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not update user"})
		return // Nothing to update
	}

	// Register user
	err := c.UserRepository.UpdateUser(timedContext, user.ID, update)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not update user"})
		return
	}

	ctx.JSON(http.StatusCreated, gin.H{"message": "User updated successfully"})
}

func (c *UserController) GetUser(ctx *gin.Context) {
	emailId, error := ctx.GetQuery("emailId")
	if !error {
		ctx.JSON(http.StatusBadRequest, gin.H{"error": "Invalid request format"})
		return
	}

	timedContext, cancel := config.GetTimedContext()
	defer cancel()

	// Get user
	user, err := c.UserRepository.GetUserProfileByEmailId(timedContext, emailId)
	if err != nil {
		ctx.JSON(http.StatusInternalServerError, gin.H{"error": "Could not get user"})
		return
	}

	ctx.JSON(http.StatusCreated, gin.H{"user": user})
}
