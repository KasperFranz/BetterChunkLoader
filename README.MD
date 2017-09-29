# BetterChunkLoader
[![Build Status](http://ci.communitybuilt.net/job/BetterChunkLoader/badge/icon)](http://ci.communitybuilt.net/job/BetterChunkLoader/)
[![Discord](https://img.shields.io/badge/Support-Discord-7289DA.svg)](https://discord.gg/MD6qGAd)

This Sponge plugin also requires the [BCLForge mod](https://github.com/KasperFranz/BCLForgeLib) which is used to save the chunks and make them loaded.

## Support
You can recieve support for using this plugin on my [Discord Chat](https://discord.gg/MD6qGAd).


## Known issues
the database is NOT compatible with the 1.7 version of this mod, since this is making the table readable instead of being bytes - Sorry


If you have any issues with it please make an issue.

The latest builds can be found on our [CI server](http://ci.communitybuilt.net/job/BCLForgeLib/) which is provided by Community Built


## Commands
The base command is betterchunkloader (with the alias of bcl), this gives the general usage and what commands is avaliable.

| Subcommand        | paramaters           | Permission  | Description |
| ------------- |:-------------:| -----:| ----- |
| `balance` |   | `betterchunkloader.balance.own` | Get your balance of chunkloaders.
| `balance` | [User]  | `betterchunkloader.balance.others` | Get your balance of  another player
| `info` |  | `betterchunkloader.info`  | get general information about chunk loaders on the server.
| `list` |  | `betterchunkloader.list.own` | get a list of your chunk loaders with the coordinates.
| `list` | [User] | `betterchunkloader.list.others` | get a list of the users chunk loaders with the coordinates.
| `chunks` | [ add \| set \| remove ] [User] [Type] [Amount] | `betterchunkloader.chunks` | change a players amount of the different chunk loaders (personal or world)
| `delete` | [User] | `betterchunkloader.delete`  | Remove the specified players chunk loaders.
| `purge` |  | `betterchunkloader.purge` | Remove Chunk loaders in not existing worlds (eg. after removing a world)


## Permissions
An up to date version of the permissions can be found in [/src/main/java/net/kaikk/mc/bcl/utils/BCLPermission.java](src/main/java/net/kaikk/mc/bcl/utils/BCLPermission.java)
