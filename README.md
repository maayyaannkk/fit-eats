# FitEats
FitEats is a smart meal planner that helps you achieve fitness goals like weight loss or muscle gain. It creates personalised meal plans based on target calories and macros, offering AI-powered suggestions and budget-friendly options to help stay on track with easy meal prep and nutrition tracking.

## Screenshots
<img src="https://github.com/maayyaannkk/fit-eats/blob/main/FitEats-collage.png" width="100%" />

## Walkthrough
<div align="center">
  <video src="https://github.com/user-attachments/assets/0aca84d6-db3e-4e2e-97a3-01171744699b" width="80%" controls>
  </video>
</div>

## Architecture (High Level)

```text
┌─────────────────────────┐   HTTPS (JWT)  ┌─────────────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│      Android App        │ ─────────────▶ │        Go API           │ ──▶ │     MongoDB      │ ◀── │   Gemini 2.5     │
│  (Jetpack Compose)      │                │      (Gin, Stateless)   │     │                  │     │      Flash       │
│                         │                │                         │     │ - Users          │     │                  │
│  - UI / User Flow       │                │  - Auth & validation    │     │ - Goals          │     │ - Meal generation│
│  - ViewModels (MVVM)    │                │  - Business logic       │     │ - Meal plans     │     │ - Portions/macros│
│  - JWT stored locally   │                │  - AI orchestration     │     │                  │     │                  │
└─────────────────────────┘                └─────────────────────────┘     └──────────────────┘     └──────────────────┘
```

## Tech Stack

| Mobile                                   | Backend                                      | Database                          | AI                                      |
|------------------------------------------|----------------------------------------------|-----------------------------------|-----------------------------------------|
| Android                                  | Go                                           | MongoDB                           | Gemini 2.5 Flash                        |
| Jetpack Compose                          | Gin                                          | Persistent user data              | Cost-efficient                          |
| MVVM (ViewModels)                        | Stateless REST API                           |                                   | Low-latency                             |
|                                          | JWT Authentication                           |                                   |                                         |

## Running Locally
- Clone the repository
- Add a .env file with required API keys
- Start MongoDB
- Run the Go API
- Run the Android app 
