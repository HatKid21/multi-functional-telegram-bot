
Multifunctional bot with AI assistent and reminder system.

# Features

- Support Gemini AI

- Local database SQLite for reminder storing

- Long-polling mode for telegram bot

# Requirements

* Java 21 or above

* Telegram Bot Token ([you can get it from @BotFather](https://t.me/BotFather))

* Gemini API key ([get it](https://aistudio.google.com/apikey))

## Evironment variables settings

- `GEMINI_AI_API_KEY` - Your Gemini API key

- `TELEGRAM_BOT_API_KEY` - Your Telegram bot token

- `KEY_ALIAS` - Keystore alias (default: `mykey`)

- `KEY_PASSWORD` - Key password

- `KEYSTORE_PASSWORD` - Keystore password


## Technologies used

- **Java 21** - Main programming language

- **TelegramBots Library** - Working with Telegram API

- **Gemini API** (fork) - Google Gemini AI integration

- **SQLite JDBC** - Local database

## Project dependencies

Main dependencies are shown in `build.gradle.kts`.


##  License

Project uses [gemini-api](https://github.com/michael-ameri/gemini-api) library under Apache 2.0 License.