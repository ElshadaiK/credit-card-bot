# Credit Card Suggestion Telegram Bot

A Telegram bot to help users track and optimize their credit card usage by suggesting the best card to use based on due dates and closing statements. Users can add, view, and adjust their credit card configurations directly within the bot.

## Features

- Add and configure multiple credit cards.
- Get optimized card suggestions based on due dates.
- Easy navigation with inline keyboards for seamless user interaction.
- Dynamic UI adjustments based on user actions.
- Secure environment variable handling for sensitive information.

## Requirements

- Java 11 or higher
- Maven 3.x
- A [Telegram Bot Token](https://core.telegram.org/bots#6-botfather)

## Local Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/elshadaik/credit-card-bot.git
   cd credit-card-bot
   ```

2. Create a `.env` file for local development:
   ```plaintext
   TELEGRAM_BOT_TOKEN=<your-bot-token>
   TELEGRAM_BOT_USERNAME=<your-bot-username>
   ```

3. Install dependencies:
   ```bash
   mvn clean install
   ```

4. Run the bot locally:
   ```bash
   mvn exec:java -Dexec.mainClass="creditcardbot.Bot"
   ```
## Key Files and Directories

- `src/main/java/creditcardbot/`
    - `Bot.java`: The main class that handles user interactions.
    - `CreditCard.java`: Represents a credit card with configurable properties.
    - `KeyboardUtils.java`: Utility class for generating dynamic inline keyboards.
    - `MessageUtils.java`: Utility class for sending messages and keyboards.

- `.env`: (For local development only) Stores your bot's credentials securely.

## Commands

- **/start**: Start the bot and view the main menu.
- **/menu**: Navigate back to the main menu.
- **Add a Card**: Add a new credit card from a predefined list.
- **View and Adjust Cards**: View your configured cards and adjust due/closing dates.
- **Get a Suggestion**: Get a recommendation for the best card to use based on your configurations.

## Future Enhancements

- Add user authentication for enhanced security.
- Integrate with external APIs to pull real-time credit card details.
- Allow users to add custom cards manually.

## Contributing

1. Fork the repository.
2. Create a new branch:
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Commit your changes:
   ```bash
   git commit -m "feat(scope): describe your feature"
   ```
4. Push your branch and open a pull request.

## License

This project is licensed under the MIT License. 