# SpringTelegramAI

SpringTelegramAI is a Spring Boot application that uses Telegram Bot API to interact with ChatGPT .

![interaction](/docs/interaction.png)
![deploy to ecs](/docs/deploy-to-ecs.png)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

What things you need to install the software and how to install them.

- Java 17
- Maven
- Spring Boot

### Environment Variables

The following environment variables are required to run the project:

- `DB_URL`: The URL of your database.
- `DB_USERNAME`: The username for your database.
- `DB_PASSWORD`: The password for your database.
- `BOT_TOKEN`: The token of your Telegram bot.
- `GPT_TOKEN`: The token of your ChatGPT model.
- `JWT_SECRET`: The secret for your JWT tokens.
- `JWT_LIFETIME`: The lifetime for your JWT tokens.
- `JWT_ISSUER`: The issuer for your JWT tokens.
- `CORS_ALLOWED_ORIGIN`: The allowed origin for your CORS requests (front-end application).

You can set these variables in your IDE or in your system's environment variables.

### Running the Project

To run the project, navigate to the project directory and execute the following command:

```bash
mvn spring-boot:run
```

### Improvements can be done
- [ ] implement custom AuthenticationProvider like EmailAuthenticationProvider
- [ ] Decouple monolith with microservices (Telegram service, Auth service, Admin service)
- [ ] Use Oauth2 Authorization Server with Authorization Client
- [ ] Use WebHook instead of polling
- [x] Use Markdown
- [x] Use transactions in TelegramGPTBot
- [ ] Add error handling to WebClient
- [ ] Add error handling to @TelegramAdvice
- [x] Fix regen messages - only new messages can do it!
- [ ] Fix removeInlineKeyboardFromLastAssistantMessage - handle MessageNotSentException when there is no inline keyboard in message

## Usage

This code is for personal use and learning purposes only. Unauthorized or commercial use is not allowed without express and prior written consent of the author. Please respect the rights of the author.

## License

This project is licensed under the MIT License - see the [LICENSE](/license.txt) file for details.


