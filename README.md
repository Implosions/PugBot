# PugBot
PugBot is a Discord bot used to facilitate and organize pickup games.

## Features
- Create, update, and remove different queues for users to add to
  - Queues hold a specified number of players that will fire when the limit is reached, notifying all signups
  - Users can set notifications for when a queue reaches a desired playercount
  - Pick teams within the discord application
  - Bot has the ability to randomly select one user to captain, and then select another player to captain with a similar rating as them based on relative pick order
  - Substitute players in and out of games
  - Configurable individual queue settings
    - Able to set a minimum number of games played to have the ability to captain
	- Able to choose whether or not to randomize captains
	- Able to assign a category id if a voice channel is created for that queue
	
- Admin functions
  - Give users admin priviledges
  - Ban users from interacting with the bot

- Create custom commands that output a preset message

- Configurable settings
  - Set a channel to focus pug related functions
  - Disconnect timer
  - AFK timer
  - Generate voice channels for teams on game start
  - Post picked teams to discord

## Dependencies
- [JDA](https://github.com/DV8FromTheWorld/JDA) (and its dependencies)
- [SQLite JDBC driver](https://github.com/xerial/sqlite-jdbc)

## Acknowledgements
PugBot is based on the IRC pug bot [Xenia](https://github.com/xzanth/pugbot) by [Xzanth](https://github.com/xzanth)
