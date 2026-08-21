# Thaumic Madness

[ English ](README.md) | [ Русский ](README_RU.md)

---

**Thaumic Madness** — это современный комплексный аддон для Thaumcraft 4 (Minecraft 1.7.10), созданный для глубокой модернизации, расширения и оптимизации тауматургического геймплея. Мод объединяет Thaumcraft с Blood Magic, новейшим графическим конвейером Angelica, расширенной системой слотов Baubles-Expanded и интерактивной картографией JourneyMap.

---

## 🔮 Ключевые особенности и механики

### 📖 Всевидящий Таумономикон и Тауматургический Атлас (Thaumaturgical Atlas)
* **Единый тауматургический фолиант**:
  * Создается в матрице наполнения объединением стандартного *Таумономикона*, *Таумометра*, *Чувствительного к вис амулета* и *Очков откровения*.
  * **Двойной интерфейс использования**:
    * **ПКМ**: Открывает стандартную книгу исследований Таумономикона.
    * **Shift + ПКМ**: Открывает мистический **Тауматургический Атлас** (базу данных узлов ауры).
* **Пассивное зрение откровения (без нагрузки и фиктивной брони)**:
  * Предоставляет полный эффект *Очков откровения* и подробный анализ аспектов узлов ауры прямо из инвентаря игрока без необходимости надевать их в слот шлема.
  * Реализовано на нативных байткод-миксинах SpongePowered (`MixinTileNodeRenderer`, `MixinRenderEventHandler`), что полностью исключает рассинхронизацию слотов брони, мерцания и конфликты с другими модами.

---

### 🧭 Трекер узлов ауры и Интерактивные объекты JourneyMap
* **Межпространственная база данных узлов**:
  * Автоматически сканирует и сохраняет все исследованные таумометром узлы во всех измерениях (Обычный мир, Ад, Энд, Древний мир / Outer Lands, Сумеречный Лес, Карманные измерения и др.).
  * Сохраняет точные координаты, измерение, тип узла, модификатор (Яркий, Угасающий, Блеклый) и полный состав аспектов с текущим запасом Vis.
* **Внутриигровой HUD-компас и целеуказатель**:
  * Наэкранный HUD отображает координаты выбранного узла, расстояние в метрах в реальном времени, аспекты и объемную 3D-стрелку компаса, указывающую прямо на узел.
* **Интерактивная интеграция с картой JourneyMap**:
  * **Нативный рендеринг на карте**: Отображает найденные узлы на полноэкранной и мини-карте JourneyMap в виде аккуратных светящихся магических сфер.
  * **Мягкая пульсация**: Замедленный 15-секундный дыхательный цикл пульсации сферы.
  * **Точная цветовая дифференциация по типам узлов**:
    * 💎 **Чистый (Pure)**: Светящийся циан (`#00E5FF`)
    * 🩸 **Голодный (Hungry)**: Ярко-алый неоновый (`#FF1744`)
    * ☣️ **Заражённый (Tainted)**: Порченый маджента (`#D500F9`)
    * 🌑 **Зловещий / Тёмный (Dark / Sinister)**: Глубокий индиго-фиолетовый (`#4A148C`)
    * ⚡ **Нестабильный (Unstable)**: Электрическое золото (`#FFD600`)
    * ✨ **Обычный (Normal)**: Окрашивается по доминирующему первичному аспекту (*Aer* — жёлтый, *Ignis* — огненно-красный, *Aqua* — небесно-голубой, *Terra* — зелёный, *Ordo* — серебристо-белый, *Perditio* — темно-серый) либо классический таум-фиолетовый.
  * **Информативные карточки при наведении**: Всплывающая подсказка под курсором с координатами, измерением, суммарным запасом Vis и списком аспектов.
  * **Окно сведений при клике**: Нажатие на сферу открывает подробный диалог (`GuiNodeDetailPopup`) с кнопками включения GPS-навигации и мгновенного перехода в Атлас.

---

### 💍 Интеграция с Baubles-Expanded (GTNH Team) и слоты аксессуаров
* **Архитектура расширенных слотов**:
  * Полная поддержка мода [Baubles-Expanded](https://github.com/GTNewHorizons/Baubles-Expanded) от команды GT New Horizons с поддержкой до 20 слотов экипировки:
    * `amulet`, `ring`, `belt`, `head`, `body`, `charm`, `cape`, `shield`, `quiver`, `gauntlet`, `earring`, `wings`, `universal`.
* **Двунаправленный адаптер совместимости (Dual-Compatibility)**:
  * Безопасное определение окружения: при наличии `Baubles-Expanded` используется интерфейс `baubles.api.expanded.IBaubleExpanded`, а при стандартном `Baubles 1.0.1.10` автоматически включается поддержка `baubles.api.IBauble` без риска ошибок `NoClassDefFoundError`.
* **Уникальные артефакты**:
  * **Кровавое кольцо (`ItemBloodRing`)**: Экипируется в слоты `ring`, `charm` или `amulet`. Дает пассивную скидку на вис и внутренний буфер LP.
  * **Кровавый амулет (`ItemBloodAmulet`)**: Экипируется в слоты `amulet`, `body` или `charm`. Проводит энергию Life Essence для автоматической зарядки жезлов и активирует защитный барьер при критическом уроне.
  * **Амулет здравомыслия (`ItemSanityCharm`)**: Экипируется в слоты `charm`, `amulet` или `universal`. Подавляет искажение и смягчает эффекты безумия.

---

### 🩸 Интеграция с Blood Magic и Blood Arsenal
* **Специальная ветка исследований**:
  * Полноценный раздел «Кровавая тауматургия» в Таумономиконе.
* **Кровавые стержни и наконечники для жезлов**:
  * Изготавливаются на Алтаре крови и в Матрице наполнения.
  * Обеспечивают постоянную скидку на вис и автоматическую регенерацию заряда жезлов за счёт жизненной эссенции (LP).
* **Набалдашник: Кровавое жертвоприношение (Blood Sacrifice)**:
  * Выпускает скоростные самонаводящиеся рунические снаряды либо конвертирует здоровье/LP в концентрированный вис.
* **Зарядка жезлов на Алтаре крови**:
  * Прямая передача эссенции из сети души для безопасного наполнения жезлов, посохов и скипетров.

---

### 🧠 Интерфейс искажения и рассудка (Warp & Sanity HUD)
* **Индикатор искажения в инвентаре**:
  * Наглядная трехцветная шкала прямо в интерфейсе инвентаря игрока при наличии Измерителя искажения (*Sanity Checker*) или Амулета здравомыслия (*Sanity Charm*).
  * Наглядно разделяет постоянное, призрачное и временное искажение.
* **Интеграция с NotEnoughItems (NEI)**:
  * Просмотр искажения и сервисные инструменты в утилитарном режиме NEI.

---

### 🚀 Графика, оптимизация и современные технологии
* **Совместимость с конвейером Angelica и современными модами**:
  * Полная адаптация под **Angelica** (движок Sodium/Iris на 1.7.10), **Neodymium**, **LWJGL3ify** и **FalseTweaks**.
  * Все рендереры используют изолированное управление матрицами OpenGL (`glPushAttrib`/`glPopAttrib`), исключая утечки матриц, сбои шейдеров и мерцание интерфейса.
* **Сглаженные HD-шрифты**:
  * Четкий шрифт с поддержкой антиалиасинга для комфортного чтения исследований и страниц Таумономикона.
* **Масштабирование аспектов (2x Scaler)**:
  * Опция 2x увеличения иконок аспектов во всплывающих подсказках для мониторов с высоким разрешением.

---

### 🌐 Встроенные локализации для всех аддонов
* Встроенный ресурспак с полным и качественным переводом на русский и английский языки для ключевых аддонов:
  * **Gadomancy**, **Warp Theory**, **Witching Gadgets**, **Thaumic Bases**, **Thaumic Exploration**, **Advanced Thaumaturgy 2**, **Automagy**, **Forbidden Magic**, **TCNodeTracker**, **TCInventoryScan**, **Thaumic Horizons**, **Thaumores** и др.

---

### ⚙️ Внутриигровая конфигурация
* Удобное меню настроек: `Главное меню / Пауза` -> `Моды` -> `Thaumic Madness` -> `Настройки`.
* Настройка позиций HUD, компаса, оверлеев JourneyMap, пассивного зрения и модулей контента на лету без перезапуска игры.

---

## 📦 Таблица совместимости и зависимостей

Все зависимости проекта разрешаются через репозитории CurseForge Maven, Modrinth и GTNH Nexus.

### 🌟 Базовые платформы и API
| Мод / Платформа | Тип | Репозиторий / Источник | Maven Координата | Описание |
| :--- | :--- | :--- | :--- | :--- |
| **[Thaumcraft 4](https://www.curseforge.com/minecraft/mc-mods/thaumcraft)** | **Обязательный** | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumcraft) / [GTNH Fork](https://github.com/GTNewHorizons/Thaumcraft4) | `curse.maven:thaumcraft-223628:2227552` | Базовое API тауматургии, исследования, узлы ауры и аспекты (4.2.3.5). |
| **[Baubles-Expanded](https://github.com/GTNewHorizons/Baubles-Expanded)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/Baubles-Expanded) | `com.github.GTNewHorizons:Baubles-Expanded:2.2.22-GTNH:dev` | Современная расширенная 20-слотовая система аксессуаров. |
| **[JourneyMap](https://modrinth.com/mod/journeymap)** | *Опциональный* | [Modrinth](https://modrinth.com/mod/journeymap) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/journeymap) | `maven.modrinth:journeymap:5.2.20` | Интерактивные объекты на карте, цветные сферы узлов и всплывающие окна. |

### 🩸 Магические аддоны и расширения
| Мод | Тип | Репозиторий / Источник | Maven Координата | Описание |
| :--- | :--- | :--- | :--- | :--- |
| **[Blood Magic](https://github.com/GTNewHorizons/BloodMagic)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/BloodMagic) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/blood-magic) | `com.github.GTNewHorizons:BloodMagic:1.9.10:dev` | Кровавые жезлы, зарядка от LP, набалдашник жертвоприношения. |
| **[Blood Arsenal](https://github.com/GTNewHorizons/BloodArsenal)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/BloodArsenal) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/blood-arsenal) | `com.github.GTNewHorizons:BloodArsenal:1.5.12:dev` | Рецепты со связанным деревом и кровавым наполнителем. |
| **[Thaumic Tinkerer](https://github.com/GTNewHorizons/ThaumicTinkerer)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/ThaumicTinkerer) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-tinkerer) | `com.github.GTNewHorizons:ThaumicTinkerer:2.11.12:dev` | Интеграция исследований Ками и управление аспектами. |
| **[Gadomancy](https://github.com/makeo/Gadomancy)** | *Опциональный* | [GitHub](https://github.com/makeo/Gadomancy) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/gadomancy) | `curse.maven:gadomancy-237271:2338768` | Хуки рендеринга узлов и расширенные механики ауры. |
| **[Automagy](https://www.curseforge.com/minecraft/mc-mods/automagy)** | *Опциональный* | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/automagy) | `curse.maven:automagy-222153:2285272` | Просмотр аспектов на устройствах красного камня и автокрафт. |
| **[Forbidden Magic](https://github.com/Spiteful-Fox/ForbiddenMagic)** | *Опциональный* | [GitHub](https://github.com/Spiteful-Fox/ForbiddenMagic) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/forbidden-magic) | `curse.maven:forbidden-magic-224237:2303822` | Темная магия, клетки ярости и кросс-модовые компоненты жезлов. |
| **[Witching Gadgets](https://github.com/BluSunrize/WitchingGadgets)** | *Опциональный* | [GitHub](https://github.com/BluSunrize/WitchingGadgets) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/witching-gadgets) | `curse.maven:witching-gadgets-228268:2262119` | Первородная экипировка, плащи и слоты аксессуаров. |
| **[Thaumic Bases](https://github.com/Modded-Thaumcraft/ThaumicBases)** | *Опциональный* | [GitHub](https://github.com/Modded-Thaumcraft/ThaumicBases) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-bases) | `curse.maven:thaumic-bases-232192:2250677` | Алхимические растения, баланс аспектов и единая локализация. |
| **[Thaumic Exploration](https://github.com/Kihira/ThaumicExploration)** | *Опциональный* | [GitHub](https://github.com/Kihira/ThaumicExploration) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-exploration) | `curse.maven:thaumic-exploration-230969:2263414` | Расширенные исследования аддонов и полная русская локализация. |
| **[Thaumic Horizons](https://www.curseforge.com/minecraft/mc-mods/thaumic-horizons)** | *Опциональный* | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/thaumic-horizons) | `curse.maven:thaumic-horizons-227914:8413552` | Наполнение существ, карманные вихри и манипуляции душами. |
| **[Tainted Magic](https://www.curseforge.com/minecraft/mc-mods/tainted-magic)** | *Опциональный* | [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tainted-magic) | `curse.maven:tainted-magic-231061:3634109` | Баланс Багряной и Теневой магии и локализация. |
| **[Warp Theory](https://github.com/shukaro/WarpTheory)** | *Опциональный* | [GitHub](https://github.com/shukaro/WarpTheory) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/warp-theory) | `curse.maven:warp-theory-224935:2217324` | Расширенные эффекты искажения, очищение и визуальные эффекты. |

### 🚀 Оптимизация, графический стек и утилиты
| Мод | Тип | Репозиторий / Источник | Maven Координата | Описание |
| :--- | :--- | :--- | :--- | :--- |
| **[FalsePatternLib](https://github.com/FalsePattern/FalsePatternLib)** | *Опциональный* | [GitHub](https://github.com/FalsePattern/FalsePatternLib) | `com.falsepattern:falsepatternlib-mc1.7.10:1.12.2` | Базовая оптимизация производительности, Reflection Cache, UniMixins. |
| **[FalseTweaks](https://github.com/FalsePattern/FalseTweaks)** | *Опциональный* | [GitHub](https://github.com/FalsePattern/FalseTweaks) | `com.falsepattern:falsetweaks-mc1.7.10:4.4.5` | Оптимизация рендеринга, исправление утечек матриц и текстур. |
| **[TC4Tweaks](https://github.com/GTNewHorizons/TC4Tweaks)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/TC4Tweaks) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/tc4tweaks) | `curse.maven:tc4tweaks-431297:8668688` | Быстрое сопоставление аспектов и ускорение рендера узлов ауры. |
| **[NotEnoughItems](https://github.com/GTNewHorizons/NotEnoughItems)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/NotEnoughItems) | `com.github.GTNewHorizons:NotEnoughItems:2.8.85-GTNH:dev` | Утилиты управления искажением и рецепты. |
| **[GTNHLib](https://github.com/GTNewHorizons/GTNHLib)** | *Опциональный* | [GitHub](https://github.com/GTNewHorizons/GTNHLib) | `com.github.GTNewHorizons:GTNHLib:0.10.6:dev` | Утилиты байткода ASM, рефлексии и сетевой синхронизации. |
| **[Traveller's Gear](https://github.com/BluSunrize/TravellersGear)** | *Опциональный* | [GitHub](https://github.com/BluSunrize/TravellersGear) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/travellers-gear) | `curse.maven:travellers-gear-224440:2262112` | Совместимость с дополнительными слотами экипировки. |

---

## 🛠️ Сборка из исходного кода

### Требования
* **Java Development Kit (JDK)**: JDK 8 или новее.
* **Git**

### Команда сборки
```bash
# Форматирование и сборка итогового .jar файла
./gradlew spotlessApply build
```
Скомпилированный `.jar` файл будет помещен в каталог `build/libs/`.

### Запуск клиента для тестирования
```bash
./gradlew runClient
```

---

## 👥 Авторы и благодарности

* **Автор и главный разработчик**: AVM / [vasmark1](https://github.com/vasmark1)
* **Особая благодарность**:
  * Azanor (Thaumcraft)
  * WayofTime (Blood Magic)
  * Команда GT New Horizons (тулчейн Gradle и Mixin для 1.7.10)
  * FalsePattern (библиотеки оптимизации и производительности для 1.7.10)
  * Сообщество разработчиков Thaumcraft

---

## 📄 Лицензия
Проект распространяется под свободной лицензией [MIT License](LICENSE).
