# Chat Bot - Android AI Chat Application

A ChatGPT-style Android app built with **Java** and **MVP architecture**.

## Features

- User registration and login (local SQLite database)
- Create new AI conversations
- Chat history with sidebar drawer
- Rename and delete conversations
- Settings: profile, theme, AI model, logout, clear chats
- Beautiful Material Design 3 UI (dark theme by default)
- Powered by Alibaba Qwen AI API

## Requirements

- Android Studio (latest)
- Android SDK 35
- Min SDK 24 (Android 7.0+)
- Internet connection for AI chat
- Qwen API key from Alibaba Cloud DashScope

## Setup

1. Open Android Studio
2. **File → Open** → select this `ChatBot` folder
3. Wait for Gradle sync to finish
4. Copy `local.properties.example` to `local.properties` (if needed)
5. Add your API key to `local.properties`:

```properties
sdk.dir=C\:\\Users\\dell\\AppData\\Local\\Android\\Sdk
QWEN_API_KEY=your_api_key_here
```

6. Run on emulator or physical device

## Project Structure (MVP)

```
com.chatbot.app/
├── model/       → SQLite, API, repositories
├── contract/    → MVP view/presenter interfaces
├── presenter/   → Business logic
├── view/        → Activities
├── adapter/     → RecyclerView adapters
└── utils/       → Helpers
```

## Screens

| Screen | Description |
|--------|-------------|
| Splash | Session check, routes to Login or Main |
| Login | Email/password sign in |
| Register | Create new account |
| Main | Home with conversation drawer |
| Chat | AI messaging interface |
| Settings | Profile, theme, model, account |

## API Region

Default base URL: `https://dashscope-intl.aliyuncs.com/compatible-mode/v1/`

My key is from China (Beijing), use `QWEN_BASE_URL` in `app/build.gradle`.

## Security and my reminder

- Never commit `local.properties` or your API key
- Passwords are hashed with bcrypt before storage
- All chat data stored locally in SQLite

## Version

1.0
