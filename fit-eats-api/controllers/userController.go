package controllers

import (
	"fit-eats-api/config"
	"fit-eats-api/models"
	"fit-eats-api/services"
	"fit-eats-api/utils"

	"net/http"

	"github.com/gin-gonic/gin"
)

type UserController struct {
	UserService *services.UserService
}

func NewUserController(service *services.UserService) *UserController {
	return &UserController{UserService: service}
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

	// Register user
	err := c.UserService.RegisterUser(timedContext, &user)
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

	user, token, refreshToken, err := c.UserService.Login(timedContext, email, password)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"accessToken": token, "refreshToken": refreshToken, "user": user})
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

	token, err := c.UserService.RequestAccessToken(timedContext, emailId, refreshToken)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
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

	err := c.UserService.RevokeAccessToken(timedContext, emailId)
	if err != nil {
		ctx.JSON(http.StatusUnauthorized, gin.H{"error": err.Error()})
		return
	}

	ctx.JSON(http.StatusOK, gin.H{"message": "Logout user"})
}
