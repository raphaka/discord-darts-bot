# discord-darts-bot
A Discord Bot that counts your score during a game of darts

## Preparation
- Go to disordapp.com/developers
- Create a new Application
- Create a new Bot in this application
- Invite your bot to your server. https://discordapp.com/developers/docs/topics/oauth2#bots
- Copy your Token from the bot menu

## Build
To create a jar use the `mvn package` command.
The jar file will be located in the _target_ folder.

## Starting the bot
Run the application with `java -jar <path to your jar file> <your bot token>`

## List of commands
- !gameon @opponent - play a single game of 501 Double out against @opponent (alias: !go)
- !bestof <legs> @opponent - play a match with multiple legs against @opponent (aliases: !bo, !match, !m)
- XY - you scored XY points
- remaining XY - your remaining score is XY points (aliases: r XY, rest XY)
- check Y - you checked out with Y darts (alias: c Y)
- !quit - abandon the current match
