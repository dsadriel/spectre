<center>
  
![Spectre](/.github/logo.png)

</center>

----

> Spectre is a Minecraft plugin that allows the player to control the visibility of nearby players.

> [!CAUTION]
> This plugin is still in development and may contain bugs. Use at your own risk. The performance impact of this plugin when used with a large number of players is still unknown.

## Features
- User can toggle the visibility of nearby players
- Three visibility modes: Vanish, Ghost, Invisible
- Three armor visibility modes: Visible, Hidden, Boots
- Customizable visibility distance
- Permissions support

> [!IMPORTANT]
> This plugin is dependent on [PacketEvents v2.3](https://github.com/retrooper/packetevents/releases/tag/v2.3.0) to intercept packets and modify the player's visibility.


## Commands


| Command | Description | Permission |
| --- | --- | --- |
| `/spectre [help]` | Display the help message | `spectre.use` |
| `/spectre <enable\|disable>` | Toggle the visibility of nearby players | `spectre.toggle` |
| `/spectre mode <vanish\|ghost\|invisible>` | Change the visibility mode | `spectre.mode.<vanish\|ghost\|invisible>` |
| `/spectre armor <visible\|hidden\|boots>` | Change the visibility of armor | `spectre.armor.<visible\|hidden\|boots>` |

## Permissions

| Permission | Description |
| --- | --- |
| `spectre.use` | Allows the player to use spectre command |
| `spectre.toggle` | Allows the player to toggle the visibility of nearby players |
| `spectre.mode.<vanish\|ghost\|invisible>` | Allows the player to change the visibility mode |
| `spectre.armor.<visible\|hidden\|boots>` | Allows the player to change the visibility of armor |
| `spectre.bypass` | Allows the player to bypass the visibility restrictions |
| `spectre.config` | Allows the player to reload the configuration and change settings |

## Configuration

All messages can be customized in the `config.yml` file. 

-----

## Backlog

- [ ] Implement visibility distance
- [ ] Add command to reload the configuration
- [ ] Add command to change the default boots
- [ ] Add admin command to change the visibility of other players