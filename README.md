# discord-darts-bot
A Discord Bot that counts your score during a game of darts

## Preparation
- Go to disordapp.com/developers
- Create a new Application
- Create a new Bot in this application
- Copy your Token from the bot menu

## Build
To create a jar use the `mvn package` command.
The jar file will be located in the _target_ folder.

## Starting the bot
Run the application with `java -jar <path to your jar file> <your bot token>

## List of commands
- !gameon @opponent - play a game of 501 Double out against @opponent
- XY - you scored XY points
- remaining XY - your remaining score is XY points (alternatives: rXY, rest XY)
- check Y - you checked out with Y darts. If Y is not given, the bot will assume, you needed the least possible darts for the remaining score
- !quit - abandon the current game
