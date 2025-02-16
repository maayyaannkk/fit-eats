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
	MongoURI  string
	Database  string
	JWTSecret string
	Port      string
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
			MongoURI:  os.Getenv("MONGO_URI"),
			Database:  os.Getenv("DB_NAME"),
			JWTSecret: os.Getenv("JWT_SECRET"),
			Port:      os.Getenv("PORT"),
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
