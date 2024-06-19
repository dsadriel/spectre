<center>
  
![Spectre](/.github/logo.png)

</center>

----

## Spectre is a Minecraft plugin that allows the player to control the visibility of nearby players.

Built for: `1.20.6`
Tested on: 1.20, 1.19.4, 1.18.2

> [!CAUTION]
> This plugin is still in development and may contain bugs. Use at your own risk. The performance impact of this plugin when used with a large number of players is still unknown.

> [!IMPORTANT]
> This plugin is dependent on [PacketEvents v2.3](https://github.com/retrooper/packetevents/releases/tag/v2.3.0) to intercept packets and modify the player's visibility.


## Features
- Three visibility modes: **Vanish**, **Ghost**, and **Invisible**
- Three armor visibility modes: **Visible**, **Hidden**, and **Boots**
- Custom hiding radius per player
- Toggle the visibility of nearby players
- Bypass the visibility restrictions
- Admin commands to set the visibility mode, armor visibility, and hiding radius for other players
- Permissions for each command and feature to control access
- Customizable messages


### Demo
<details>

> Visibility mode: **Ghost**, Armor: **Boots**

![](/.github/1.webp)

> Visibility mode: **Ghost**, Armor: **Visible**

![](/.github/2.webp)

> Visibility mode: **Ghost**, Armor: **Hidden**

![](/.github/3.webp)

> Visibility mode: **Invisible**, Armor: **Boots**

![](/.github/4.webp)

</details>


## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/spectre [help]` | Display the help message | `spectre.use` |
| `/spectre set mode <vanish\|ghost\|invisible>` | Change the visibility mode | `spectre.mode` |
| `/spectre set armor <visible\|hidden\|boots>` | Change the visibility of armor | `spectre.armor` |
| `/spectre set radius <radius>` | Set the hiding radius | `spectre.radius` |
| `/spectre set <mode\|armor\|radius> <value> [player]` | Set a value for yourself or another player | `spectre.admin` |
| `/spectre version` | Display the plugin version and check for updates | `spectre.admin` |
| `/spectre info [player]` | Display information about the players configuration | `spectre.admin` |



## Permissions

| Permission | Description |
| --- | --- |
| `spectre.use` | Allows the player to use spectre command |
| `spectre.toggle` | Allows the player to toggle the visibility of nearby players |
| `spectre.mode` | Allows the player to change the visibility mode |
| `spectre.armor` | Allows the player to change the visibility of armor |
| `spectre.radius` | Allows the player to change the hiding radius |
| `spectre.bypass` | Allows the player to bypass the visibility restrictions |
| `spectre.admin` | Allows the to use admin commands |

## Installation
To install the plugin, download the latest release from the [releases page](https://github.com/dsadriel/spectre/releases) and place it in the `plugins` folder of your server. Also, make sure to install [PacketEvents](https://github.com/retrooper/packetevents/releases) in the `plugins` folder.

## Configuration

All messages can be customized in the `config.yml` file.  The default configuration can be found [here](/src/main/resources/config.yml).

## bStats
This plugin uses bStats to collect anonymous data about the server to help the development process. You can disable this feature in the `plugins/bStats/config.yml` file or by setting `enabled` to `false` in the `config.yml` file.