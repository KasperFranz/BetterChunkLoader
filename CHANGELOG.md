# Changelog

All notable changes to this project will be documented in this file. This
project adheres to [Semantic Versioning](http://semver.org/ ), and following the
format from [keep a CHANGELOG](http://keepachangelog.com/ )

## [Unreleased][unreleased]

### Added

### Changed

- Updated the log message when the player deleted their own chunkloader

### Fixed

- Fixed a problem where a chunkloader would not be deleted if more than 1 block
  were deleted in the same event #55

## 2020-12-23 3.8.2

### Added

- Show an error message on BCL commands if the plugin is not ready.
  (because of wrong config or datastore)

- Added H2 as an option for datastore #25

### Changed

- We throw more readable errors when there is a config exception.
- Cleanup of internals in the plugin (namespaces)
- Removed internal references of admin loader (it was removed a long time ago)

### Fixed

- Issue with creating chunk loaders
- URL Encode database password (Thank you, @clienthax)

## 2020-10-26 3.8.1

### Added

- Sending a message to the player if another player is deleting their
  chunkloader

- Allowing the player to Delete their own/others Chunk Loaders through the list
  command (permission betterchunkloader.list.delete.others/
  betterchunkloader.list.delete.own) #6

- Allowing players to delete all their own chunkloaders (using /bcl delete) #36

### Changed

- Cleaned up the messages sent to the player and logged when we are
  deleting/changing/creating chunk loaders.

- Using the DB Connection provided through Sponge #33 (moves DB connection to
  another thread)

- Added security by using SQL Prepared statements.

### Fixed

- Fixed the log messages around edit/delete chunkloaders #37
- Fixed a crash with bstats #44
- Removed bstats #44

## 2020-09-30 3.8.0

### Added

- Added bStats for visibility of how many are using the version.

- Added the option to use metadata for the amount of chunk loaders a player can
  have.

- Added more exceptions around the MySQL database connection start.

- Added a reload command to reload the configuration!

### Changed

- Moved to a new config setup, it should help make the code cleaner :)
- All amounts passed to /bcl chunks should now always be a positive integer.

### Fixed

- Fixed a null pointer exception when using the purge command, and the world is
  empty (#27)

- If the world is not loaded on startup we are now loading in the world when we
  get the sponge event! (Fixes #35)

## 2018-10-07 3.7.0

### Changed

- added some more clean message to the error when there is a mysql error.

### Fixed

- It was not possible to remove chunk loaders

## 2018-04-07 3.6.0

### Fixed

- Now the default blaze rod is also doing what it is supposed to.

## 2017-10-27 3.5.0

### Added

- added an option to teleport to a chunkloader (if you have the permission)

- Added message when not able to load config. (#20)

- Added option to use another item instead of a blaze rod to init the chunk
  loaders.

### Changed

- Upgrade to use Sponge API 7
- the info command is now only showing active currently loaded chunks.

### Fixed

- Fixed the spam when shutting down the server, since it tried to unload chunks
  we didn't load.

- An Illegal Exception if the data in the database isn't a valid UUID, then we
  now just say so instead of not loading the plugin.

## 2017-06-12 3.4.0

### Changed

- changed permission to read your own balance from `betterchunkloader.balance`
  to `betterchunkloader.balance.own`

### Fixed

- Fixed an issue where the insert was done to the wrong part
  (world add as personal and personal as world)
  only if using the add while nothing was in the DB

- Fixed a permission issue on the balance command.
  (You were able to see everyone's balance without the right permission)

- Updated the readme with the subcommands

## 2017-04-21 3.3.0

### Changed

- Using the gameStoppingEvent instead of the GameStopped event to be sure that
  we can save the DB properly.

### Fixed

- setting a players personal/world is not going out from the base config option
  instead of 0.
