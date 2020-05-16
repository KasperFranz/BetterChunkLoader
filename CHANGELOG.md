# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/ ), and following the format from [keep a CHANGELOG](http://keepachangelog.com/ )

## [Unreleased][unreleased]
### Added
 - Added Bstats for visibility of how many are using the version.
 - Added the option to use metadata for the amount of chunk loaders a player can have.
 
### Changed

### Fixed
 - Fixed an NPE when using purge and the world is empty (#27)

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
