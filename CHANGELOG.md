# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/ ), and following the format from [keep a CHANGELOG](http://keepachangelog.com/ )

## [Unreleased][unreleased]
### Added
 - Sending a message to the user if another player is deleting their chunkloader
 - Allowing players to delete all of their own chunkloaders (using /bcl delete) #36
 
### Changed
 - Cleaned up the messages sent to the user and logged when we are deleting/changing/creating chunk loaders.
### Fixed
 - Fixed the log messages around edit/delete chunkloaders #37


## 2020-09-30 3.8.0
### Added
 - Added bStats for visibility of how many are using the version.
 - Added the option to use metadata for the amount of chunk loaders a player can have.
 - Added more exceptions around the MySQL database connection start.
 - Added an reload command to reload the configuration!
 
### Changed
 - Moved to a new config setup, it should help make the code cleaner :)
 - All amounts passed to /bcl chunks should now always be a positive integer.

### Fixed
 - Fixed a null pointer exception when using the purge command, and the world is empty (#27)
 - If the world is not loaded on startup we are now loading in the world when we get the sponge event! (Fixes #35)
 
## 2018-10-07 3.7.0
### Changed
 - added some more clean message to the error when there is a mysql error.

### Fixed
 - It was not possible to remove chunk loaders


## 2018-04-07 3.6.0
### Fixed
 - Now the default blazerod is also doing what it is supposed to.


## 2017-10-27 3.5.0
### Added
 - added an option to teleport to a chunkloader (if you have the permission)
 - Added message when not able to load config. (#20)
 - Added option to use another item instead of a blaze rod to init the chunk loaders.

### Changed
 -  Upgrade to use Sponge API 7
 -  the info command is now only showing active currently loaded chunks.

### Fixed
 - Fixed the spam when shutting down the server, since it tried to unload chunks we didn't load.
 - A Illegal Exception if the data in the database isn't a valid UUID, then we now just say so instead of not loading the plugin.


## 2017-06-12 3.4.0
### Changed
 - changed permission to read your own balance from `betterchunkloader.balance` to `betterchunkloader.balance.own`

### Fixed
 - fixed an issue where the insert was done to the wrong part (world add as personal and personal as world, if using the add while nothing was in the DB)
-fixed a permission issue (you was able to see everyone's balance without the right permission and updated the readme with the subcommands


## 2017-04-21 3.3.0
### Changed
 - Using the gameStoppingEvent instead of the GameStopped event to be sure that we can unforce properly.
 
### Fixed
- setting a players personal/world is not going out from the base config option instead of 0.
