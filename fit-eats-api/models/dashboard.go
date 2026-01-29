package models

type DashboardResponse struct {
	UserInfo        UserInfoSection `json:"user"`
	ProgressSummary ProgressSummary `json:"progressSummary"`
	CalorieOverview CalorieOverview `json:"calorieOverview"`
	TodayMeals      []Meal          `json:"todayMeals"`
}

type UserInfoSection struct {
	Name         string `json:"name"`
	Greeting     string `json:"greeting"` // e.g., "Persistence is the bridge between failure and success."
	ProfileImage string `json:"profile_image,omitempty"`
}

type ProgressSummary struct {
	WeightInKg        MetricProgress `json:"weightInKg"`
	BodyFatPercentage MetricProgress `json:"bodyFatPercentage"`
}

type MetricProgress struct {
	Current float64 `json:"current"` // current value
	Last    float64 `json:"last"`    // last week's value
	Goal    float64 `json:"goal"`
	Start   float64 `json:"start"`
}

type CalorieOverview struct {
	Total  CalorieData `json:"total"`
	Macros MacroData   `json:"macros"`
}

type CalorieData struct {
	Consumed  float64 `json:"consumed"`  // e.g., 1650
	Goal      float64 `json:"goal"`      // e.g., 2000
}

type MacroData struct {
	Protein MacroItem `json:"protein"`
	Carbs   MacroItem `json:"carbs"`
	Fats    MacroItem `json:"fats"`
}

type MacroItem struct {
	Consumed float64 `json:"consumed"`
	Goal     float64 `json:"goal"`
	Unit     string  `json:"unit"` // e.g., "g"
}
