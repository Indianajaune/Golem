# Golem
A Minecraft Discord bot.

## How to run

Import the project as a maven project into your IDE of choice. Once imported, either build it as a jar or run it from your IDE. 
When running the bot, the first argument of the exec must be your token.

When running as a jar: `java -jar builtjar.jar TOKENHERE`

When running from an IDE:

IntelliJ:

![IntelliJ Example](http://i.imgur.com/qkjwvie.png)

Eclipse:

![Eclipse Example](http://i.imgur.com/v0mLql6.png)

## Functionalities

```
/player <playername> : display some informations about a minecraft player whose username is <playername>
/query <address> <port> : display some informations about a minecraft server <address> must be either an IP or a domain of a Minecraft server and <port> must be the query port
/wiki <search> : return an URL to the minecraft wiki with <search>
/join : join vocal channel
/play <song> : search on the internet <song> and plays it, can either be an URL or keywords
/leave : leave the vocal channel 
/skip : pass into the next music in the queue or either stop if there is nothing in the queue.
```

**WIP :**

Fixing and improving current features
-have to fix URL parsing with wiki feature (second argument is not used because of the native discord4j method used)
-have to make return message for /help
-have to display username history of players in the embed


**TODO :**

-RCON server management (randomly crypting RCON password for evident potential security issues)
-mojang server status display
