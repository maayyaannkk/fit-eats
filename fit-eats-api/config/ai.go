package config

import (
	"fit-eats-api/models"
	"sync"

	"fmt"
	"log"

	"github.com/google/generative-ai-go/genai"
	"google.golang.org/api/option"
)

var weightRangeModel *genai.GenerativeModel
var goalDurationModel *genai.GenerativeModel
var tdeeModel *genai.GenerativeModel
var macroModel *genai.GenerativeModel
var mealModel *genai.GenerativeModel
var singleMealModel *genai.GenerativeModel

var loadOnceWeightRange sync.Once
var loadOnceGoalDuration sync.Once
var loadOnceTdee sync.Once
var loadOnceMacro sync.Once
var loadOnceMeal sync.Once
var loadOnceSingleMeal sync.Once

func getBaseModel() genai.GenerativeModel {
	ctx, cancel := GetTimedContext()
	defer cancel()

	client, err := genai.NewClient(ctx, option.WithAPIKey(GetConfig().GeminiApiKey))
	if err != nil {
		log.Fatalf("Error creating client: %v", err)
	}

	baseModel := client.GenerativeModel("gemini-2.5-flash-lite")
	baseModel.SetTemperature(0)
	baseModel.SetTopK(40)
	baseModel.SetTopP(0.95)
	baseModel.SetMaxOutputTokens(8192)
	baseModel.ResponseMIMEType = "application/json"
	baseModel.SystemInstruction = &genai.Content{
		Parts: []genai.Part{
			genai.Text("You are a seasoned highly qualified fitness and nutrition specialist with over 15 years of experience in the industry." +
				"Your expertise lies in creating and helping your clients achieve fitness goals," +
				"while also providing detailed caloric and macronutrient breakdowns." +
				"your response is always in a valid json nothing else"),
		},
	}
	return *baseModel
}

func GetWeightRangeModel() *genai.GenerativeModel {
	if weightRangeModel == nil {
		loadOnceWeightRange.Do(func() {
			temp := getBaseModel()
			temp.ResponseSchema = &genai.Schema{
				Type:     genai.TypeObject,
				Enum:     []string{},
				Required: []string{"idealWeightRange"},
				Properties: map[string]*genai.Schema{
					"idealWeightRange": {
						Type:     genai.TypeObject,
						Enum:     []string{},
						Required: []string{"lowerBound", "upperBound"},
						Properties: map[string]*genai.Schema{
							"lowerBound": {
								Type:     genai.TypeObject,
								Enum:     []string{},
								Required: []string{"weight_in_kg", "fat_percentage", "description"},
								Properties: map[string]*genai.Schema{
									"weight_in_kg": {
										Type: genai.TypeNumber,
									},
									"fat_percentage": {
										Type: genai.TypeNumber,
									},
									"description": {
										Type: genai.TypeString,
									},
								},
							},
							"upperBound": {
								Type:     genai.TypeObject,
								Enum:     []string{},
								Required: []string{"weight_in_kg", "fat_percentage", "description"},
								Properties: map[string]*genai.Schema{
									"weight_in_kg": {
										Type: genai.TypeNumber,
									},
									"fat_percentage": {
										Type: genai.TypeNumber,
									},
									"description": {
										Type: genai.TypeString,
									},
								},
							},
						},
					},
				},
			}
			weightRangeModel = &temp
		})
	}
	return weightRangeModel
}

func GetGoalDurationModel() *genai.GenerativeModel {
	if goalDurationModel == nil {
		loadOnceGoalDuration.Do(func() {
			temp := getBaseModel()
			temp.ResponseSchema = &genai.Schema{
				Type: genai.TypeObject,
				Properties: map[string]*genai.Schema{
					"type": {
						Type: genai.TypeString,
						Enum: []string{
							"Fat loss",
							"Muscle gain",
						},
					},
					"pace_options": {
						Type: genai.TypeObject,
						Properties: map[string]*genai.Schema{
							"slow": {
								Type: genai.TypeObject,
								Properties: map[string]*genai.Schema{
									"weekly_weight_change_kg": {
										Type: genai.TypeNumber,
									},
									"duration_weeks": {
										Type: genai.TypeInteger,
									},
									"notes": {
										Type: genai.TypeString,
									},
								},
								Required: []string{
									"weekly_weight_change_kg",
									"duration_weeks",
									"notes",
								},
							},
							"medium": {
								Type: genai.TypeObject,
								Properties: map[string]*genai.Schema{
									"weekly_weight_change_kg": {
										Type: genai.TypeNumber,
									},
									"duration_weeks": {
										Type: genai.TypeInteger,
									},
									"notes": {
										Type: genai.TypeString,
									},
								},
								Required: []string{
									"weekly_weight_change_kg",
									"duration_weeks",
									"notes",
								},
							},
							"fast": {
								Type: genai.TypeObject,
								Properties: map[string]*genai.Schema{
									"weekly_weight_change_kg": {
										Type: genai.TypeNumber,
									},
									"duration_weeks": {
										Type: genai.TypeInteger,
									},
									"notes": {
										Type: genai.TypeString,
									},
								},
								Required: []string{
									"weekly_weight_change_kg",
									"duration_weeks",
									"notes",
								},
							},
						},
						Required: []string{
							"slow",
							"medium",
							"fast",
						},
					},
				},
				Required: []string{
					"type",
					"pace_options",
				},
			}
			goalDurationModel = &temp
		})
	}
	return goalDurationModel
}

func GetTdeeModel() *genai.GenerativeModel {
	if tdeeModel == nil {
		loadOnceTdee.Do(func() {
			temp := getBaseModel()
			temp.ResponseSchema = &genai.Schema{
				Type: genai.TypeObject,
				Required: []string{
					"bmr",
					"tdee",
				},
				Properties: map[string]*genai.Schema{
					"bmr": {
						Type: genai.TypeInteger,
					},
					"tdee": {
						Type: genai.TypeArray,
						Items: &genai.Schema{
							Type:     genai.TypeObject,
							Required: []string{"description", "tdee", "lifestyle"},
							Properties: map[string]*genai.Schema{
								"description": {
									Type: genai.TypeString,
								},
								"tdee": {
									Type: genai.TypeInteger,
								},
								"lifestyle": {
									Type: genai.TypeString,
									Enum: []string{"Sedentary", "Light", "Moderate", "Very Active", "Extra Active"},
								},
							},
						},
					},
				},
			}
			tdeeModel = &temp
		})
	}
	return tdeeModel
}

func GetMacroModel() *genai.GenerativeModel {
	if macroModel == nil {
		loadOnceMacro.Do(func() {
			temp := getBaseModel()
			temp.ResponseSchema = &genai.Schema{
				Type: genai.TypeObject,
				Properties: map[string]*genai.Schema{
					"weekly_weight_loss_kg": {
						Type: genai.TypeNumber,
					},
					"daily_calorie_deficit": {
						Type: genai.TypeInteger,
					},
					"daily_calorie_intake": {
						Type: genai.TypeInteger,
					},
					"macronutrient_split": {
						Type: genai.TypeObject,
						Properties: map[string]*genai.Schema{
							"protein": {
								Type: genai.TypeObject,
								Properties: map[string]*genai.Schema{
									"total_grams": {
										Type: genai.TypeInteger,
									},
									"calories": {
										Type: genai.TypeInteger,
									},
								},
								Required: []string{
									"total_grams",
									"calories",
								},
							},
							"fat": {
								Type: genai.TypeObject,
								Properties: map[string]*genai.Schema{
									"total_grams": {
										Type: genai.TypeInteger,
									},
									"calories": {
										Type: genai.TypeInteger,
									},
								},
								Required: []string{
									"total_grams",
									"calories",
								},
							},
							"carbohydrates": {
								Type: genai.TypeObject,
								Properties: map[string]*genai.Schema{
									"total_grams": {
										Type: genai.TypeInteger,
									},
									"calories": {
										Type: genai.TypeInteger,
									},
								},
								Required: []string{
									"total_grams",
									"calories",
								},
							},
						},
						Required: []string{
							"protein",
							"fat",
							"carbohydrates",
						},
					},
				},
				Required: []string{
					"weekly_weight_loss_kg",
					"daily_calorie_deficit",
					"daily_calorie_intake",
					"macronutrient_split",
				},
			}
			macroModel = &temp
		})
	}
	return macroModel
}

func GetMealModel() *genai.GenerativeModel {
	if mealModel == nil {
		loadOnceMeal.Do(func() {
			temp := getBaseModel()
			temp.ResponseSchema = &genai.Schema{
				Type:     genai.TypeObject,
				Required: []string{"mealPlans"},
				Properties: map[string]*genai.Schema{
					"mealPlans": {
						Type: genai.TypeArray,
						Items: &genai.Schema{
							Type:     genai.TypeObject,
							Required: []string{"dayOfWeek", "meals"},
							Properties: map[string]*genai.Schema{
								"dayOfWeek": {
									Type: genai.TypeString,
									Enum: []string{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"},
								},
								"meals": {
									Type: genai.TypeArray,
									Items: &genai.Schema{
										Type:     genai.TypeObject,
										Required: []string{"time", "name", "description", "ingredients", "recipe_steps", "calories", "protein", "fat", "carbs"},
										Properties: map[string]*genai.Schema{
											"time": {
												Type: genai.TypeString,
											},
											"name": {
												Type: genai.TypeString,
											},
											"description": {
												Type: genai.TypeString,
											},
											"ingredients": {
												Type: genai.TypeArray,
												Items: &genai.Schema{
													Type:     genai.TypeObject,
													Required: []string{"name", "quantity"},
													Properties: map[string]*genai.Schema{
														"name": {
															Type: genai.TypeString,
														},
														"quantity": {
															Type: genai.TypeString,
														},
													},
												},
											},
											"recipe_steps": {
												Type: genai.TypeArray,
												Items: &genai.Schema{
													Type: genai.TypeString,
												},
											},
											"calories": {
												Type: genai.TypeInteger,
											},
											"protein": {
												Type: genai.TypeInteger,
											},
											"fat": {
												Type: genai.TypeInteger,
											},
											"carbs": {
												Type: genai.TypeInteger,
											},
										},
									},
								},
							},
						},
					},
				},
			}
			mealModel = &temp
		})
	}
	return mealModel
}

func GetSingleMealModel() *genai.GenerativeModel {
	if singleMealModel == nil {
		loadOnceSingleMeal.Do(func() {
			temp := getBaseModel()
			temp.ResponseSchema = &genai.Schema{
				Type:     genai.TypeObject,
				Required: []string{"meals"},
				Properties: map[string]*genai.Schema{
					"meals": {
						Type: genai.TypeArray,
						Items: &genai.Schema{
							Type:     genai.TypeObject,
							Required: []string{"time", "name", "description", "ingredients", "recipe_steps", "calories", "protein", "fat", "carbs"},
							Properties: map[string]*genai.Schema{
								"time": {
									Type: genai.TypeString,
								},
								"name": {
									Type: genai.TypeString,
								},
								"description": {
									Type: genai.TypeString,
								},
								"ingredients": {
									Type: genai.TypeArray,
									Items: &genai.Schema{
										Type:     genai.TypeObject,
										Required: []string{"name", "quantity"},
										Properties: map[string]*genai.Schema{
											"name": {
												Type: genai.TypeString,
											},
											"quantity": {
												Type: genai.TypeString,
											},
										},
									},
								},
								"recipe_steps": {
									Type: genai.TypeArray,
									Items: &genai.Schema{
										Type: genai.TypeString,
									},
								},
								"calories": {
									Type: genai.TypeInteger,
								},
								"protein": {
									Type: genai.TypeInteger,
								},
								"fat": {
									Type: genai.TypeInteger,
								},
								"carbs": {
									Type: genai.TypeInteger,
								},
							},
						},
					},
				},
			}
			singleMealModel = &temp
		})
	}
	return singleMealModel
}

func GetWeightRangePrompt(user models.User, currentWeightInKg float32, currentBodyFatPercentage float32) string {
	bodyFatString := ""
	if currentBodyFatPercentage != 0 {
		bodyFatString = fmt.Sprintf("with approx %.1f%% body fat", currentBodyFatPercentage)
	}

	return fmt.Sprintf("I am %.1f kg %s, %s year old %s, and %.1f cm in height."+
		" What should be my ideal weight range in kg with respective body fat percentage."+
		" Add maximum 1 line for description.",
		currentWeightInKg, bodyFatString, user.Age, user.Sex, user.HeightInCm)
}

func GetGoalDurationPrompt(user models.User, currentWeightInKg float32, currentBodyFatPercentage float32, goalWeightInKg float32, goalBodyFatPercentage float32) string {
	bodyFatString := ""
	if currentBodyFatPercentage != 0 {
		bodyFatString = fmt.Sprintf("with approx %.1f%% body fat", currentBodyFatPercentage)
	}

	return fmt.Sprintf("I am %.1f kg %s, %s year old %s, and %.1f cm in height."+
		" My goal is to get to target weight as %.1f kg and %.1f%% body fat."+
		" I want 3 pace options to get to my target namely slow paced, medium paced and fast paced."+
		" For each option I want also want to know duration in weeks to achieve the target, weekly weight change i.e. loss or gain in kg.",
		currentWeightInKg, bodyFatString, user.Age, user.Sex, user.HeightInCm, goalWeightInKg, goalBodyFatPercentage)
}

func GetTdeePrompt(user models.User, currentWeightInKg float32, currentBodyFatPercentage float32, goalWeightInKg float32, goalBodyFatPercentage float32, goalType string) string {
	bodyFatString := ""
	if currentBodyFatPercentage != 0 {
		bodyFatString = fmt.Sprintf("with approx %.1f%% body fat", currentBodyFatPercentage)
	}

	return fmt.Sprintf("I am %.1f kg %s, %s year old %s, and %.1f cm in height."+
		" My goal is %s, with target weight as %.1f kg and %.1f%% body fat."+
		" I want to know my maintenance calories and tdee. bmr should be average of Mifflin-St Jeor and Harris-Benedict. "+
		" Description should include job, lifestyle and exercise.",
		currentWeightInKg, bodyFatString, user.Age, user.Sex, user.HeightInCm, goalType, goalWeightInKg, goalBodyFatPercentage)
}

func GetDailyMacroPrompt(user models.User, currentWeightInKg float32, currentBodyFatPercentage float32,
	goalWeightInKg float32, goalBodyFatPercentage float32, goalType string,
	currentBmr int32, currentTdee int32, weightChange float32) string {
	bodyFatString := ""
	if currentBodyFatPercentage != 0 {
		bodyFatString = fmt.Sprintf("with approx %.1f%% body fat", currentBodyFatPercentage)
	}

	return fmt.Sprintf("I am %.1f kg %s, %s year old %s, and %.1f cm in height."+
		" My goal is %s, with target weight as %.1f kg and %.1f%% body fat."+
		" With my Current bmr of %d calories and tdee of %d calories, I want to know my daily calorie intake to achieve a weight change of %.2f kg per week. "+
		" Make sure to include daily calories and macros in response.",
		currentWeightInKg, bodyFatString, user.Age, user.Sex, user.HeightInCm, goalType, goalWeightInKg, goalBodyFatPercentage, currentBmr, currentTdee, weightChange)
}

// TODO add a user prompt for preferences
func GetWeeklyMealPrompt(user models.User, prompt string,
	currentWeightInKg float32, currentBodyFatPercentage float32,
	goalWeightInKg float32, goalBodyFatPercentage float32,
	maxCalories int32, maxFat int32, maxCarb int32,
	maxProtein int32, goalType string) string {
	bodyFatString := ""
	if currentBodyFatPercentage != 0 {
		bodyFatString = fmt.Sprintf("with approx %.1f%% body fat", currentBodyFatPercentage)
	}

	return fmt.Sprintf("I am %.1f kg %s, %s year old %s, and %.1f cm in height."+
		" My goal is %s, with target weight as %.1f kg and %.1f%% body fat."+
		" For the next week I will be on a %d calorie per day diet with %d grams protein %d grams fat and %d grams carbs."+
		" I am from %s and prefer %s diet."+
		" Include meals that are easily available in my country, and keep my dietary preference in line with this."+
		" Suggest a meal plan for a the whole week including time frames for each meal."+
		" Make sure to include calories and macros."+
		" Time should always be in am/pm format for eg. 6:30 pm. "+
		" Make sure the ingredients are generic and not specific to a brand or country, also make sure to include raw ingredients rather than processed or store bought finished products."+
		" for eg. ingredient should not include 'chicken tikka masala' instead break it down into raw ingredients and include in recipe steps."+
		" I will also attach a prompt with any special requests."+
		" Make sure to only include items from the prompt that are relevant to meal plan and exclude anything else."+
		" prompt: %s",
		currentWeightInKg, bodyFatString, user.Age, user.Sex, user.HeightInCm, goalType, goalWeightInKg, goalBodyFatPercentage, maxCalories, maxProtein, maxFat, maxCarb, user.Country, user.DietPreference, prompt)
}

func GetSingleMealEditPrompt(user models.User, mealsAsJsonString string, prompt string,
	currentWeightInKg float32, currentBodyFatPercentage float32,
	goalWeightInKg float32, goalBodyFatPercentage float32,
	maxCalories int32, maxFat int32, maxCarb int32,
	maxProtein int32, goalType string) string {
	bodyFatString := ""
	if currentBodyFatPercentage != 0 {
		bodyFatString = fmt.Sprintf("with approx %.1f%% body fat", currentBodyFatPercentage)
	}

	return fmt.Sprintf("I am %.1f kg %s, %s year old %s, and %.1f cm in height."+
		" My goal is %s, with target weight as %.1f kg and %.1f%% body fat."+
		" For the next week I will be on a %d calorie per day diet with %d grams protein %d grams fat and %d grams carbs."+
		" I am from %s and prefer %s diet."+
		" Include meals that are easily available in my country, and keep my dietary preference in line with this."+
		" Suggest changes to a single day meal plan. I will attach the meal plan and also a prompt with the requested changes."+
		" Make sure to only include items from the prompt that are relevant to meal plan and exclude anything else."+
		" Make sure to include calories and macros."+
		" Time should always be in am/pm format for eg. 6:30 pm. "+
		" If you don't find anything relevant in the prompt send the same meal back."+
		" Make sure the ingredients are generic and not specific to a brand or country, also make sure to include raw ingredients rather than processed or store bought finished products."+
		" for eg. ingredient should not include 'chicken tikka masala' instead break it down into raw ingredients and include in recipe steps."+
		" Meals: %s."+
		" Prompt: %s.",
		currentWeightInKg, bodyFatString, user.Age, user.Sex, user.HeightInCm, goalType, goalWeightInKg, goalBodyFatPercentage, maxCalories, maxProtein, maxFat, maxCarb, user.Country, user.DietPreference, mealsAsJsonString, prompt)
}
