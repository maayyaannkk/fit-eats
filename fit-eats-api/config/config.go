package config

import (
	"context"
	"log"
	"os"
	"sync"
	"time"

	"github.com/joho/godotenv"
)

type Config struct {
	MongoURI         string
	Database         string
	JWTAccessSecret  string
	JWTRefreshSecret string
	Port             string
	GeminiApiKey     string
	SerperApiKey     string
}

var projectConfig *Config
var loadOnce sync.Once

func GetConfig() *Config {
	loadOnce.Do(func() {
		err := godotenv.Load()
		if err != nil {
			log.Fatal("Error loading .env file")
		}

		config := Config{
			MongoURI:         os.Getenv("MONGO_URI"),
			Database:         os.Getenv("DB_NAME"),
			JWTAccessSecret:  os.Getenv("JWT_ACCESS_SECRET"),
			JWTRefreshSecret: os.Getenv("JWT_REFRESH_SECRET"),
			Port:             os.Getenv("PORT"),
			GeminiApiKey:     os.Getenv("GEMINI_API_KEY"),
			SerperApiKey:     os.Getenv("SERPER_API_KEY"),
		}

		projectConfig = &config
	})

	return projectConfig
}

func GetTimedContext(timeout ...int) (context.Context, context.CancelFunc) {
	if len(timeout) == 0 {
		return context.WithTimeout(context.Background(), 5*time.Second)
	}
	return context.WithTimeout(context.Background(), time.Duration(timeout[0])*time.Second)
}
