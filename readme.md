<center>
  
![Spectre](/.github/logo.png)

</center>

----

> Spectre is a Minecraft plugin that allows the player to control the visibility of nearby players.

## Features
- User can toggle the visibility of nearby players
- Three visibility modes: Vanish, Ghost, Invisible
- Three armor visibility modes: Visible, Hidden, Boots
- Customizable visibility distance
- Permissions support

## Requirements
This plugin is dependent on [PacketEvents](https://github.com/retrooper/packetevents) v2.3 to intercept packets and modify the player's visibility.


## Commands
> WARNING: Permissions are not yet implemented, all commands are available to all players.

| Command | Description | Permission |
| --- | --- | --- |
| `/spectre help` | Display the help message | `spectre.help` |
| `/spectre reload` | Reload the configuration | `spectre.reload` |
| `/spectre <enable\|disable>` | Toggle the visibility of nearby players | `spectre.toggle` |
| `/spectre mode <vanish\|ghost\|invisible>` | Change the visibility mode | `spectre.mode.<vanish\|ghost\|invisible>` |
| `/spectre armor <visible\|hidden\|boots>` | Change the visibility of armor | `spectre.armor.<visible\|hidden\|boots>` |
| `/spectre distance <distance>` | Change the visibility distance | `spectre.set_distance` |

## Configuration

All messages can be customized in the `config.yml` file. On the BOOTS mode players with no boots will be rendered with boots specified in the configuration.
