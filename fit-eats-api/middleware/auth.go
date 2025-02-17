package middleware

import (
	"net/http"
	"strings"

	"fit-eats-api/utils"

	"github.com/gin-gonic/gin"
)

func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")
		if authHeader == "" {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "No token provided"})
			c.Abort()
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")

		if !utils.IsAccessTokenValid(tokenString) {
			c.JSON(http.StatusUnauthorized, gin.H{"error": "Invalid jwt token"})
			c.Abort()
			return
		}

		c.Next()
	}
}
