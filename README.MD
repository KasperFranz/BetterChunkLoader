# BetterChunkLoader

[![Codacy Badge][codacy_img]][codacy_link]
[![Discord][discord_img]][discordLink]

BetterChunkLoaders is a Sponge Forge plugin that adds two different types of
chunk loaders for your users.

## How To

### Types Of Chunk Loaders.

- **Online Only** – This will load chunks when the player is online (Iron block)
- **Always Online** – This will load chunks even when the player is offline up
  to the number of hours specified in the config (Diamond block).

### Create a chunk loader (user).

1. Add an iron or diamond block in the block you want to have as your chunk
   loaders' centre.
2. Click the block with a blaze road (the item can change this in the config)
   and an interface where you can select how many chunks around it you want to
   load.

You can find the latest version under [releases][release_link].

## Requirements

- [BCLForge mod][bcl_forge_mod] (Used so we can chunkload reliable)
- SpongeForge

## Support

You can receive support for using this plugin on my [Discord Chat][discordLink].

If you find any issues when using this plugin, please create a problem.

## Commands

The base command is betterchunkloader (with the alias of bcl), this gives the
general usage and what commands are available.

| Sub-command        | parameters           | Permission  | Description |
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

As part of version 3.8.0, you can now set up metadata on the user for a few
defaults.

| meta name        | description |
| ------------- | ----- |
| bcl.world | The number of world chunk loaders a user can have. |
| bcl.personal | The number of personal chunk loaders a user can have. |

**The user currently has to log out and log in for this to take effect, this
will also not work if the user has a set/added value (I am working on a solution
for this)**

## DataSources

As part of version 3.8.2, I have introduced H2 (which is now the default). I
would still recommend you to use MySQL where possible.

## Permissions

You can find the up-to-date version of permission in
the [permissions][permissions] file.

## FAQ

### what is the difference between a personal loader and a world loader?

A personal loader is only active when the player is online.

A world loader is active even when the player is offline.

The length of the world loader is loading after the player was last seen is
defined in the config.


[discordLink]: https://discord.gg/MD6qGAd

[discord_img]: https://img.shields.io/badge/Support-Discord-7289DA.svg

[codacy_img]: https://app.codacy.com/project/badge/Grade/810a7bcaa5364dfe972195da262d028b

[codacy_link]: https://www.codacy.com/app/KasperFranz/BetterChunkLoader?utm_source=github.com&utm_medium=referral&utm_content=KasperFranz/BetterChunkLoader&utm_campaign=badger

[bcl_forge_mod]: https://github.com/KasperFranz/BCLForgeLib

[release_link]: https://github.com/KasperFranz/BetterChunkLoader/releases

[permissions]: /src/main/java/guru/franz/mc/bcl/utils/Permission.java
