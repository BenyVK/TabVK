<div align="center">

# TabVK

**All-in-one animated TAB list plugin for Minecraft servers, with built-in RankVK integration.**

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21%2B-62b47a?style=flat-square)](#requirements)
[![Version](https://img.shields.io/badge/version-1.0.2-blue?style=flat-square)](#)
[![Author](https://img.shields.io/badge/author-BenyVK-orange?style=flat-square)](https://github.com/BenyVK)

**English** | [فارسی](README.FA.md)

</div>

---

## About

TabVK is a lightweight, fully configurable TAB list plugin for Paper-based servers. It gives you an animated header and footer, a custom player-list format, live placeholders, gradients, per-world layouts and ping bars, all from a single `configvk.yaml` file.

TabVK has no rank system of its own. Prefixes, suffixes, groups and weights come from **[RankVK](https://github.com/BenyVK)**, and the tab list is sorted by group weight automatically.

## Features

- **Animated header and footer** with multiple lines, custom frames and adjustable speed
- **Gradients and colors**: legacy codes (`&a`, `&l`), hex (`&#ff00ff`) and MiniMessage (`<gradient>`, `<rainbow>`), including animated gradients through `%phase%`
- **Custom animations** (`%anim_<name>%`): titles, loading bars, rotating links
- **Rich placeholders**: ping, ping bars, TPS, online/max players, world, coordinates, health, food, level, gamemode, RAM, time and date
- **Ping levels**: colors and signal bars based on each player's ping
- **RankVK integration**: prefix, suffix, group and weight are read from RankVK, and the list is sorted by weight (higher weight, higher position)
- **Per-world header/footer**: different layouts for the Nether, the End or any world
- **Player customization**: players can set their own prefix and suffix, with length limits and a color permission
- **Nametag support**: optional prefix and suffix above player heads (scoreboard teams)
- **Rotating quotes**: fun messages in the footer through `%quote%`
- **Custom placeholders and icons**: define your own `%server_name%`, `%discord%`, `%icon_heart%` and more
- **PlaceholderAPI support** (optional): use any PAPI placeholder anywhere
- **Timezone aware**: configurable timezone, time format and date format
- **Cleaner command suggestions**: optionally hide `plugin:command` entries from the command list
- **Per-player toggle**: players can turn their header/footer on or off

## Requirements

| Requirement | Details |
|---|---|
| Server | Paper (or a Paper fork), Minecraft **1.21+** |
| Java | 21+ |
| Recommended | [RankVK](https://github.com/BenyVK) for ranks and sorting |
| Optional | PlaceholderAPI, LuckPerms |

## Installation

1. Download the latest `TabVK.jar` from the [Releases](https://github.com/BenyVK/TabVK/releases) page.
2. Put it in your server's `plugins/` folder.
3. Install **RankVK** if you want ranks, prefixes and sorting.
4. Start the server. `plugins/TabVK/configvk.yaml` is generated on first run.
5. Edit the config and run `/tabvk reload`.

## Commands

The main command is `/tabvk`, with the alias `/tvk`.

| Command | Description | Permission |
|---|---|---|
| `/tabvk help` | Show the command list | none |
| `/tabvk about` | Plugin info | none |
| `/tabvk toggle` | Toggle your own header/footer | `tabvk.toggle` |
| `/tabvk prefix <text\|reset>` | Set your own prefix | `tabvk.customize.prefix` |
| `/tabvk suffix <text\|reset>` | Set your own suffix | `tabvk.customize.suffix` |
| `/tabvk set <player> <prefix\|suffix> <text\|reset>` | Edit another player's prefix or suffix | `tabvk.admin` |
| `/tabvk reload` | Reload the config | `tabvk.admin` |

## Permissions

| Permission | Description | Default |
|---|---|---|
| `tabvk.admin` | Reload the config and edit other players | OP |
| `tabvk.toggle` | Toggle header/footer for yourself | everyone |
| `tabvk.customize.prefix` | Set your own prefix | OP |
| `tabvk.customize.suffix` | Set your own suffix | OP |
| `tabvk.color` | Use colors and gradients in a custom prefix or suffix | OP |

## Configuration

Everything lives in `plugins/TabVK/configvk.yaml`. The file is heavily commented and includes ready-to-copy sample ranks for RankVK.

```yaml
header-footer:
  enabled: true
  header:
    - "%anim_title%"
    - "%icon_arrow% &7Welcome <gradient:#55ffff:#ff55ff:%phase%><bold>%player%</bold></gradient>"
  footer:
    - "&7Ping: %ping_bars% &f%ping%ms  &8|  &7TPS: %tps_color%%tps%"
    - "&7Online: &a%online%&7/&c%max%  &8|  &7World: &e%world%"

player-list:
  enabled: true
  format: "%prefix%%name_color%%player%%suffix% &8| %ping_color%%ping%ms"
```

### Main placeholders

`%player%` `%ping%` `%ping_color%` `%ping_bars%` `%online%` `%max%` `%tps%` `%tps_color%` `%world%` `%x%` `%y%` `%z%` `%health%` `%food%` `%level%` `%gamemode%` `%ram_used%` `%ram_max%` `%time%` `%date%` `%group%` `%prefix%` `%suffix%` `%name_color%` `%quote%` `%phase%` `%anim_<name>%` `%icon_<name>%`

### RankVK quick start

```
/rank group vip create
/rank group vip setweight 50
/rank group vip setprefix &6[VIP]
/rank user <player> parent add vip
```

Changes appear in the tab list within about half a second.

## Notes

- Minecraft's default font does not render right-to-left scripts such as Persian or Arabic correctly in the tab list, so English text works best there.
- If another plugin already manages scoreboard teams, keep `nametag.enabled: false`.

## Building from source

```bash
git clone https://github.com/BenyVK/TabVK.git
cd TabVK
# build with your Paper 1.21 API setup (Gradle or Maven)
```

## Author

Made by **[BenyVK](https://github.com/BenyVK)** · [www.gharri.ir](https://www.gharri.ir)

---

<div align="center">

**English** | [فارسی](README.FA.md)

</div>
