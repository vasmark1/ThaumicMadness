# Thaumic Madness

[ English ](README.md) | [ Русский ](README_RU.md)

---

**Thaumic Madness WIP** is an advanced Thaumcraft 4 addon for Minecraft 1.7.10 designed to modernize, expand, and streamline thaumaturgical gameplay while bridging Thaumcraft with Blood Magic and other popular magic addons.

---

## 🔮 Key Features

### 📖 The All-Seeing Thaumonomicon & Thaumaturgical Atlas
* **Unified Tome**: Combines the Thaumonomicon, Thaumometer, Vis-sensitive Amulet, and Goggles of Revealing on the Infusion Matrix.
* **Dual Interface**:
  * **Right-Click**: Standard Thaumonomicon research book.
  * **Shift + Right-Click**: Opens the mystical **Aura Node Atlas**.
* **Passive Revealer Vision**: Grants full Goggles of Revealing vision and aura node aspect inspection passively from anywhere in the player's main inventory (using native SpongePowered Mixins — no dummy armor manipulation).

### 🧭 Aura Node Tracker & JourneyMap Interactive Map Objects
* **Aura Node Database**: Scans and saves nodes surveyed with a Thaumometer across all dimensions.
* **Live Sorting & Search**: Filter by aspect composition, node type (Pure, Sinister, Hungry, Tainted, Unstable), or brightness.
* **In-Game Waypoint HUD**: On-screen distance, directional compass arrow, and aspect list pointing directly towards the selected node.
* **Dynamic JourneyMap Integration**: 
  * Displays tracked aura nodes directly on JourneyMap fullscreen and minimaps as glowing, color-coded magical spheres.
  * Node colors accurately reflect node types (Pure, Hungry, Tainted, Sinister, Unstable) and primal aspects.
  * Interactive hover tooltips displaying aspect breakdown, coordinates, and total Vis.
  * Click-to-open detail popup with direct GPS tracking and "Open in Atlas" navigation.

### 🩸 Blood Magic & Blood Arsenal Integration
* **Dedicated Research Tab**: Fully integrated research branch inside the Thaumonomicon.
* **Blood Wand Rod & Caps**: Craftable with Infusion and Blood Altars, offering passive vis discounts and LP recharge.
* **Wand Focus: Blood Sacrifice**: Channel LP into runic projectile attacks or self-sacrificial blood charging.
* **Altar & Sanguine Wand Charging**: Safely siphon Life Essence to replenish wand vis stores.

### 🧠 Sanity & Warp HUD
* **Inventory Warp Meter**: Live visual display of permanent, sticky, and temporary Warp directly on the player's inventory screen when carrying a Sanity Checker.
* **NEI Integration**: Inspect Warp and testing utilities through NotEnoughItems.

### 🖋️ HD Typography & Visual Enhancements
* **Crisp HD Font**: Clear, anti-aliased font rendering for Thaumonomicon pages.
* **Aspect Tooltip Scaler**: 2x scaling option for small, hard-to-read aspect icons.

### ⚙️ Modular In-Game Configuration
* Access configuration via `Mods` -> `Thaumic Madness` -> `Config` in the main menu or in-game.
* Toggle visual aids, HUD waypoints, passive vision, HD fonts, and content modules on the fly without game restarts.

---

## 📦 Dependencies

| Dependency | Type | Description |
| :--- | :--- | :--- |
| **[Thaumcraft 4](https://www.curseforge.com/minecraft/mc-mods/thaumcraft)** (>= 4.2.3.5) | **Hard (Required)** | Core thaumaturgy API, aura nodes, research, and aspects. |
| **[JourneyMap](https://www.curseforge.com/minecraft/mc-mods/journeymap)** | *Soft (Optional)* | Dynamic interactive map objects, color-coded node spheres, and popup inspect. |
| **[Blood Magic](https://www.curseforge.com/minecraft/mc-mods/blood-magic)** | *Soft (Optional)* | Blood wand crafting, LP recharge, and Focus of Blood Sacrifice. |
| **[Blood Arsenal](https://www.curseforge.com/minecraft/mc-mods/blood-arsenal)** | *Soft (Optional)* | Bound wood and blood infuser recipes. |
| **[Gadomancy](https://www.curseforge.com/minecraft/mc-mods/gadomancy)** | *Soft (Optional)* | Node rendering hooks and custom aura mechanics. |
| **[Automagy](https://www.curseforge.com/minecraft/mc-mods/automagy)** | *Soft (Optional)* | Aspect tag inspection on redstone devices. |
| **[Thaumic Exploration](https://www.curseforge.com/minecraft/mc-mods/thaumic-exploration)** | *Soft (Optional)* | Extended addon research & unified Russian localization. |
| **[Thaumic Bases](https://www.curseforge.com/minecraft/mc-mods/thaumic-bases)** | *Soft (Optional)* | Unified localization and aspect balance. |
| **[Witching Gadgets](https://www.curseforge.com/minecraft/mc-mods/witching-gadgets)** | *Soft (Optional)* | Baubles and cloth support. |
| **[Warp Theory](https://www.curseforge.com/minecraft/mc-mods/warp-theory)** | *Soft (Optional)* | Extended warp effects. |
| **[NotEnoughItems](https://www.curseforge.com/minecraft/mc-mods/notenoughitems)** | *Soft (Optional)* | Warp button handlers and recipe search. |
| **[Baubles](https://www.curseforge.com/minecraft/mc-mods/baubles)** | *Soft (Optional)* | Accessory inventory integration. |
| **[Traveller's Gear](https://www.curseforge.com/minecraft/mc-mods/travellers-gear)** | *Soft (Optional)* | Extended equipment slot safety. |

---

## 🛠️ Building from Source

### Prerequisites
* Java Development Kit (JDK) 8 or newer (configured with toolchains/Jabel).

### Build Command
```bash
./gradlew spotlessApply build
```
The compiled mod `.jar` will be placed in `build/libs/`.

### Run Client in Development
```bash
./gradlew runClient
```

---

## 👥 Authors & Credits

* **Author & Lead Developer**: AVM / vasmark1
* **Credits**:
  * Azanor (Thaumcraft)
  * WayofTime (Blood Magic)
  * GT New Horizons Team (Modernized 1.7.10 Gradle & Mixin toolchain)
  * The Thaumcraft Modding Community

---

## 📄 License
Licensed under the [MIT License](LICENSE).
