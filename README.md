# SpectatorHUD - Minecraft Fabric Mod for 1.18.2

A Fabric mod that shows health bar, hotbar, and inventory of spectated players in spectator mode for Minecraft 1.18.2.

## Features

- **Health Bar**: Shows the spectated player's health (including absorption hearts)
- **Hotbar**: Displays the spectated player's hotbar with all items
- **Food/Hunger Bar**: Shows the spectated player's hunger level
- **Experience Bar**: Displays the spectated player's XP progress and level
- **Armor Bar**: Shows the spectated player's armor value
- **Air Bar**: Shows breath meter when the spectated player is underwater
- **Inventory View**: When opening inventory while spectating, shows the spectated player's full inventory

## Requirements

- Minecraft 1.18.2
- Fabric Loader 0.14.10 or higher
- Fabric API 0.59.1+1.18.2
- Java 17 or higher

## Installation

1. Install Fabric Loader for Minecraft 1.18.2
2. Download Fabric API for 1.18.2 from [Modrinth](https://modrinth.com/mod/fabric-api) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
3. Place both Fabric API and this mod's JAR file in the `mods` folder
4. Launch Minecraft with the Fabric profile

## Building from Source

### Prerequisites

- JDK 17 or newer
- Gradle 7.4+ (or use the provided Gradle wrapper)

### Build Steps

#### Linux/macOS:
```bash
cd spectatorhud-mod
./gradlew build
```

#### Windows:
```cmd
cd spectatorhud-mod
gradlew.bat build
```

The compiled JAR file will be in `build/libs/spectatorhud-1.0.0.jar`.

### If Gradle wrapper doesn't work:

1. Install Gradle 7.4 or newer
2. Run:
```bash
gradle build
```

## Usage

1. Enter spectator mode (default key: F3+N in Creative, or `/gamemode spectator`)
2. Left-click on a player to spectate them (first-person view)
3. The spectated player's HUD elements will now be displayed:
   - Health bar at the bottom-left
   - Food bar at the bottom-right
   - Hotbar at the bottom center
   - Experience bar below the hotbar
   - Armor bar above the health bar
   - Air bar when underwater

4. Press E (or your inventory key) to view the spectated player's full inventory

## Configuration

The mod has the following configurable options (default: all enabled):
- Show Health Bar
- Show Hotbar
- Show Food Bar
- Show Experience Bar
- Show Inventory
- Show Armor Bar
- Show Air Bar

## Technical Details

### Mixins Used

1. **InGameHudMixin**: Intercepts the in-game HUD rendering to add spectated player's status bars
2. **GuiInventoryMixin**: Modifies the inventory screen to show spectated player's inventory

### Project Structure

```
spectatorhud-mod/
├── build.gradle          # Gradle build configuration
├── gradle.properties     # Mod version and dependencies
├── settings.gradle       # Gradle settings
├── gradlew               # Gradle wrapper (Linux/macOS)
├── gradlew.bat           # Gradle wrapper (Windows)
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── spectatorhud/
│       │           ├── client/
│       │           │   ├── SpectatorHUDClient.java    # Main entry point
│       │           │   └── SpectatorHUDConfig.java    # Configuration
│       │           └── mixin/
│       │               ├── InGameHudMixin.java        # HUD rendering
│       │               └── GuiInventoryMixin.java     # Inventory screen
│       └── resources/
│           ├── fabric.mod.json          # Fabric mod metadata
│           └── spectatorhud.mixins.json # Mixin configuration
└── LICENSE
```

## Compatibility

- **Minecraft**: 1.18.2 only
- **Fabric Loader**: 0.14.10+
- **Fabric API**: Required
- **Java**: 17+

## Known Issues

- When spectating rapidly between players, the HUD might flicker
- Some custom HUD mods might conflict with this mod

## Credits

- Inspired by [SpectatorPlus](https://github.com/hpfxd/SpectatorPlus) by hpfxd
- Built with Fabric MC

## License

MIT License - See LICENSE file for details

## Version History

### 1.0.0
- Initial release
- Health bar display for spectated players
- Hotbar display for spectated players
- Food/hunger bar display
- Experience bar display
- Full inventory view
- Armor and air bar display
