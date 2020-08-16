# BetterChunkLoader
[![Codacy Badge][CodacyImg]][codacyLink]
[![Discord][discordImg]][discordLink]

This Sponge plugin requires the [BCLForge mod][BCLForgeMod]. we use this secondary mod to do chunkloading reliable.

You can find the latest version under [releases][ReleaseLink].

## Requirements 
 - [BCLForge mod][BCLForgeMod]
 - A MySQL Database 
 
## Support
You can receive support for using this plugin on my [Discord Chat][discordLink].


## Known issues
the database is NOT compatible with the 1.7 version of this mod since this is making the table readable instead of being bytes - Sorry


If you find any issues when using this plugin, please create an issue.


## Commands
The base command is betterchunkloader (with the alias of bcl), this gives the general usage and what commands are available.

| Sub command        | parameters           | Permission  | Description |
| ------------- |:-------------: | -----| ----- |
| `balance` |   | `betterchunkloader.balance.own` | Get your balance of chunkloaders.
| `balance` | [User]  | `betterchunkloader.balance.others` | Get your balance of  another player
| `info` |  | `betterchunkloader.info`  | get general information about chunk loaders on the server.
| `list` |  | `betterchunkloader.list.own` | get a list of your chunk loaders with the coordinates.
| `list` | [User] | `betterchunkloader.list.others` | get a list of the users chunk loaders with the coordinates.
| `chunks` | [ add \| set \| remove ] [User] [Type] [Amount] | `betterchunkloader.chunks` | change a players amount of the different chunk loaders (personal or world)
| `delete` | [User] | `betterchunkloader.delete`  | Remove the specified players chunk loaders.
| `purge` |  | `betterchunkloader.purge` | Remove Chunk loaders in not existing worlds (eg. after removing a world)
| `reload` |  | `betterchunkloader.reload` | Reloads the configuration (implemented in v.3.8.RC3) 

## metadata
As part of version 3.8.0, you can now set up metadata on the user for a few defaults.

| meta name        | description |
| ------------- | ----- |
| bcl.world | The number of world chunk loaders a user can have. |
| bcl.personal | The number of personal chunk loaders a user can have. |

**The user currently has to log out and log in for this to take effect, this will also not work if the user has a set/added value (I am working on a
 solution for this)**
 

## Permissions
The up to date version of permission can be found at [permissions][permissions] file.


## FAQ 
### what is the difference between a personal loader and a world loader? 
A personal loader is only active when the player is online while a world loader is active even when the player is offline) (how long after their last login can be defined in the config. 




[discordLink]: https://discord.gg/MD6qGAd
[discordImg]: https://img.shields.io/badge/Support-Discord-7289DA.svg
[CodacyImg]: https://api.codacy.com/project/badge/Grade/3fb6acd7449047798d24928bc94ca347
[CodacyLink]: https://www.codacy.com/app/KasperFranz/BetterChunkLoader?utm_source=github.com&utm_medium=referral&utm_content=KasperFranz/BetterChunkLoader&utm_campaign=badger
[bclForgeMod]: https://github.com/KasperFranz/BCLForgeLib
[ReleaseLink]: https://github.com/KasperFranz/BetterChunkLoader/releases
[permissions]: /src/main/java/guru/franz/mc/bcl/utils/Permission.java