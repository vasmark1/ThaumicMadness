# Thaumic Madness

[ English ](README.md) | [ Русский ](README_RU.md)

---

**Thaumic Madness** is a next-generation Thaumcraft 4 addon for Minecraft 1.7.10 designed to modernize, expand, and streamline thaumaturgical gameplay while bridging Thaumcraft with Blood Magic, modern rendering engines, expanded bauble architectures, and mapping systems.

---

## 🔮 Core Features & System Overview

### 📖 The All-Seeing Thaumonomicon & Thaumaturgical Atlas
* **Unified Thaumaturgical Tome**:
  * Forged upon the Infusion Matrix by infusing the standard *Thaumonomicon* with a *Thaumometer*, *Vis-Sensitive Amulet*, and *Goggles of Revealing*.
  * **Dual-Action Interface**:
    * **Right-Click**: Opens the standard Thaumonomicon research book.
    * **Shift + Right-Click**: Opens the mystical **Thaumaturgical Atlas** (Aura Node Database).
* **Passive Revealer Vision (Zero Armor Overhead)**:
  * Grants full *Goggles of Revealing* vision and detailed aura node aspect analysis passively from anywhere inside the player's main inventory.
  * Implemented using native SpongePowered bytecode Mixins (`MixinTileNodeRenderer`, `MixinRenderEventHandler`) — completely eliminating dummy armor swaps, rendering glitches, and mod incompatibilities.

---

### 🧭 Aura Node Tracker & Interactive JourneyMap Objects
* **Cross-Dimensional Node Database**:
  * Automatically records all aura nodes surveyed with a Thaumometer across all dimensions (Overworld, Nether, End, Outer Lands, Twilight Forest, Pocket Planes, etc.).
  * Tracks coordinates, dimension, node type, modifier (Bright, Fading, Pale), and full aspect breakdown with live vis capacities.
* **In-Game Waypoint HUD & Compass**:
  * On-screen tracking HUD displaying target node coordinates, real-time distance in meters, aspect quantities, and a 3D directional compass needle.
* **Dynamic JourneyMap Interactive Map Objects**:
  * **Native Map Rendering**: Renders tracked aura nodes directly onto JourneyMap's fullscreen map and minimap as vibrant, glowing circular aura spheres.
  * **Gentle Breathing Animation**: Subtle 15-second pulsation cycle for natural magical aesthetics.
  * **Accurate Type-Based Color Coding**:
    * 💎 **Pure Node**: Vibrant Cyan (`#00E5FF`)
    * 🩸 **Hungry Node**: Neon Crimson Red (`#FF1744`)
    * ☣️ **Tainted Node**: Flux Magenta (`#D500F9`)
    * 🌑 **Dark / Sinister Node**: Deep Indigo Purple (`#4A148C`)
    * ⚡ **Unstable Node**: Electric Gold (`#FFD600`)
    * ✨ **Normal Node**: Colored according to dominant primal aspect (*Aer* Yellow, *Ignis* Orange-Red, *Aqua* Sky Blue, *Terra* Spring Green, *Ordo* Silver-White, *Perditio* Charcoal) or Arcane Violet.
  * **Interactive Hover Tooltips**: Displays full node summary (Type, Modifier, Coordinates, Total Vis, Aspect list) directly under the cursor.
  * **Click-to-Inspect Dialog**: Clicking any sphere on JourneyMap opens a dedicated popup (`GuiNodeDetailPopup`) with direct GPS tracking toggles and one-click navigation to the Thaumaturgical Atlas.

---

### 💍 Baubles-Expanded (GTNH Team) & Multi-Slot Accessories
* **Expanded Slot Architecture**:
  * Fully integrates with [Baubles-Expanded](https://github.com/GTNewHorizons/Baubles-Expanded) by the GT New Horizons team, supporting up to 20 expanded accessory slots:
    * `amulet`, `ring`, `belt`, `head`, `body`, `charm`, `cape`, `shield`, `quiver`, `gauntlet`, `earring`, `wings`, `universal`.
* **Dual-Compatibility Safety Wrapper**:
  * Gracefully detects runtime environment: utilizes `baubles.api.expanded.IBaubleExpanded` when Baubles-Expanded is present, with automatic fallback to standard Baubles 1.0.1.10 (`baubles.api.IBauble`) without `NoClassDefFoundError` crashes.
* **Custom Artifacts**:
  * **Blood Ring (`ItemBloodRing`)**: Equippable in `ring`, `charm`, or `amulet` slots. Grants passive vis discount and LP reserve buffer.
  * **Blood Amulet (`ItemBloodAmulet`)**: Equippable in `amulet`, `body`, or `charm` slots. Channels Life Essence to recharge equipped wands and provides emergency damage absorption.
  * **Sanity Charm (`ItemSanityCharm`)**: Equippable in `charm`, `amulet`, or `universal` slots. Mitigates passive Warp accumulation and softens sanity loss events.

---

### 🩸 Blood Magic & Blood Arsenal Integration
* **Dedicated Research Branch**:
  * Standalone "Blood Thaumaturgy" research tab integrated into the Thaumonomicon.
* **Sanguine Wand Rods & Caps**:
  * Sanguine-infused wood and bound materials crafted across Blood Altars and the Infusion Matrix.
  * Offers passive vis discounts and automated wand vis restoration powered by Life Essence (LP).
* **Wand Focus: Blood Sacrifice**:
  * Casts high-velocity homing runic projectiles or converts personal health/LP into concentrated vis charges.
* **Altar Vis Charging**:
  * Safely siphon altar LP directly into recharging wands, staves, and scepters.

---

### 🧠 Sanity & Warp HUD
* **Inventory Warp Level Indicator**:
  * Real-time HUD meter rendered seamlessly on the player's inventory screen whenever carrying a *Sanity Checker* or *Sanity Charm*.
  * Accurately breaks down permanent, sticky, and temporary Warp with color-coded gauge bars.
* **NotEnoughItems (NEI) Integration**:
  * Direct Warp inspection panel and utility controls in NEI cheat/utility mode.

---

### 🚀 Graphics, Optimization & Modern Compatibility
* **Angelica & Modern Renderer Compatibility**:
  * Fully compatible with **Angelica** (Sodium & Iris rendering engine for 1.7.10), **Neodymium**, **LWJGL3ify**, and **FalseTweaks**.
  * Custom renderers use isolated OpenGL state management (`glPushAttrib`/`glPopAttrib`), preventing matrix leaks, shader breaks, and HUD flickering.
* **High-DPI Font Rendering**:
  * Crisp, anti-aliased font engine for comfortable reading of Thaumonomicon pages and research entries.
* **Aspect Tooltip 2x Scaler**:
  * Configurable option to scale aspect icons 2x inside tooltips for high-resolution displays.

---

### 🌐 Universal Multi-Addon Localizations
* Built-in automatic resource pack providing complete, curated **Russian** and **English** localizations for major Thaumcraft addons:
  * **Gadomancy**, **Warp Theory**, **Witching Gadgets**, **Thaumic Bases**, **Thaumic Exploration**, **Advanced Thaumaturgy 2**, **Automagy**, **Forbidden Magic**, **TCNodeTracker**, **TCInventoryScan**, **Thaumic Horizons**, **Thaumores**, etc.

---

### ⚙️ In-Game Modular Configuration
* Dynamic in-game configuration GUI available via `Main Menu / Pause` -> `Mods` -> `Thaumic Madness` -> `Config`.
* Modify HUD positions, compass behavior, JourneyMap overlays, passive revealer toggles, and module states on the fly without restarting the game.

---

## 📦 Compatibility & Dependencies

All dependencies are cleanly resolved via CurseForge Maven, Modrinth, and GTNH Nexus.

### 🌟 Core Platforms & API
| Mod / Platform | Type | Repository / Source | Maven Coordinate | Description |
| :--- | :--- | :--- | :--- | :--- |
| **[Thaumcraft 4](https://www.curseforge.com/minecraft/mc-mods/thaumcraft)** | **Hard (Required)** | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumcraft) / [GTNH Fork](https://github.com/GTNewHorizons/Thaumcraft4) | `curse.maven:thaumcraft-223628:2227552` | Core thaumaturgy API, nodes, research, and aspects (4.2.3.5). |
| **[Baubles-Expanded](https://github.com/GTNewHorizons/Baubles-Expanded)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/Baubles-Expanded) | `com.github.GTNewHorizons:Baubles-Expanded:2.2.22-GTNH:dev` | Modernized 20-slot expanded accessory system. |
| **[JourneyMap](https://modrinth.com/mod/journeymap)** | *Soft (Optional)* | [Modrinth](https://modrinth.com/mod/journeymap) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/journeymap) | `maven.modrinth:journeymap:5.2.20` | Dynamic interactive map objects, color-coded node spheres, and popup inspect. |

### 🩸 Magic Addons & Expansions
| Mod | Type | Repository / Source | Maven Coordinate | Description |
| :--- | :--- | :--- | :--- | :--- |
| **[Blood Magic](https://github.com/GTNewHorizons/BloodMagic)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/BloodMagic) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/blood-magic) | `com.github.GTNewHorizons:BloodMagic:1.9.10:dev` | Sanguine wand rods, LP vis recharge, and Focus of Blood Sacrifice. |
| **[Blood Arsenal](https://github.com/GTNewHorizons/BloodArsenal)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/BloodArsenal) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/blood-arsenal) | `com.github.GTNewHorizons:BloodArsenal:1.5.12:dev` | Bound wood recipes and blood infuser integration. |
| **[Thaumic Tinkerer](https://github.com/GTNewHorizons/ThaumicTinkerer)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/ThaumicTinkerer) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-tinkerer) | `com.github.GTNewHorizons:ThaumicTinkerer:2.11.12:dev` | Kami research integration and Kami aspect management. |
| **[Gadomancy](https://github.com/makeo/Gadomancy)** | *Soft (Optional)* | [GitHub](https://github.com/makeo/Gadomancy) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/gadomancy) | `curse.maven:gadomancy-237271:2338768` | Custom aura node mechanics and node manipulation hooks. |
| **[Automagy](https://www.curseforge.com/minecraft/mc-mods/automagy)** | *Soft (Optional)* | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/automagy) | `curse.maven:automagy-222153:2285272` | Aspect tag inspection on redstone devices and inventory automation. |
| **[Forbidden Magic](https://github.com/Spiteful-Fox/ForbiddenMagic)** | *Soft (Optional)* | [GitHub](https://github.com/Spiteful-Fox/ForbiddenMagic) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/forbidden-magic) | `curse.maven:forbidden-magic-224237:2303822` | Dark magic, wrath cages, and cross-mod wand components. |
| **[Witching Gadgets](https://github.com/BluSunrize/WitchingGadgets)** | *Soft (Optional)* | [GitHub](https://github.com/BluSunrize/WitchingGadgets) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/witching-gadgets) | `curse.maven:witching-gadgets-228268:2262119` | Primordial gear, cloaks, and bauble compatibility. |
| **[Thaumic Bases](https://github.com/Modded-Thaumcraft/ThaumicBases)** | *Soft (Optional)* | [GitHub](https://github.com/Modded-Thaumcraft/ThaumicBases) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-bases) | `curse.maven:thaumic-bases-232192:2250677` | Alchemical plants, aspects balance, and unified localization. |
| **[Thaumic Exploration](https://github.com/Kihira/ThaumicExploration)** | *Soft (Optional)* | [GitHub](https://github.com/Kihira/ThaumicExploration) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-exploration) | `curse.maven:thaumic-exploration-230969:2263414` | Extended addon research & unified Russian/English localization. |
| **[Thaumic Horizons](https://www.curseforge.com/minecraft/mc-mods/thaumic-horizons)** | *Soft (Optional)* | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-horizons) | `curse.maven:thaumic-horizons-227914:8413552` | Creature infusion, planar vortexes, and soul manipulation. |
| **[Tainted Magic](https://www.curseforge.com/minecraft/mc-mods/tainted-magic)** | *Soft (Optional)* | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tainted-magic) | `curse.maven:tainted-magic-231061:3634109` | Crimson & Shadow magic balance and localization. |
| **[Warp Theory](https://github.com/shukaro/WarpTheory)** | *Soft (Optional)* | [GitHub](https://github.com/shukaro/WarpTheory) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/warp-theory) | `curse.maven:warp-theory-224935:2217324` | Extended Warp hazards, sanity cleansing, and visual effects. |

### 🚀 Optimization, Utilities & Graphics Stack
| Mod | Type | Repository / Source | Maven Coordinate | Description |
| :--- | :--- | :--- | :--- | :--- |
| **[FalsePatternLib](https://github.com/FalsePattern/FalsePatternLib)** | *Soft (Optional)* | [GitHub](https://github.com/FalsePattern/FalsePatternLib) | `com.falsepattern:falsepatternlib-mc1.7.10:1.12.2` | Core performance optimization, reflection cache, and UniMixins support. |
| **[FalseTweaks](https://github.com/FalsePattern/FalseTweaks)** | *Soft (Optional)* | [GitHub](https://github.com/FalsePattern/FalseTweaks) | `com.falsepattern:falsetweaks-mc1.7.10:4.4.5` | Rendering fixes, matrix cleanup, and mipmap optimization. |
| **[TC4Tweaks](https://github.com/GTNewHorizons/TC4Tweaks)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/TC4Tweaks) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tc4tweaks) | `curse.maven:tc4tweaks-431297:8668688` | Fast aspect mapping, node rendering boost, and memory fixes. |
| **[NotEnoughItems](https://github.com/GTNewHorizons/NotEnoughItems)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/NotEnoughItems) | `com.github.GTNewHorizons:NotEnoughItems:2.8.85-GTNH:dev` | Warp GUI utilities and recipe handlers. |
| **[GTNHLib](https://github.com/GTNewHorizons/GTNHLib)** | *Soft (Optional)* | [GitHub](https://github.com/GTNewHorizons/GTNHLib) | `com.github.GTNewHorizons:GTNHLib:0.10.6:dev` | Modernized ASM, reflection, and network synchronization utilities. |
| **[Traveller's Gear](https://github.com/BluSunrize/TravellersGear)** | *Soft (Optional)* | [GitHub](https://github.com/BluSunrize/TravellersGear) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/travellers-gear) | `curse.maven:travellers-gear-224440:2262112` | Extended equipment slot safety and GUI hooks. |

---

## 🛠️ Building from Source

### Prerequisites
* **Java Development Kit (JDK)**: JDK 8 or newer (configured via Gradle toolchains / Jabel).
* **Git**

### Build Command
```bash
# Clean formatting and compile release jar
./gradlew spotlessApply build
```
The compiled mod `.jar` will be generated in `build/libs/`.

### Run Development Client
```bash
./gradlew runClient
```

---

## 👥 Authors & Credits

* **Author & Lead Developer**: AVM / [vasmark1](https://github.com/vasmark1)
* **Special Thanks**:
  * Azanor (Thaumcraft)
  * WayofTime (Blood Magic)
  * GT New Horizons Team (Modernized 1.7.10 Gradle & Mixin toolchain)
  * FalsePattern (Modern 1.7.10 performance & optimization libraries)
  * The Thaumcraft Modding Community

---

## 📄 License
Licensed under the [MIT License](LICENSE).
