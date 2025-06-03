# 🎮 Pokémon Clone (Pure Java Game)

A Pokémon-style 2D RPG game built **entirely in Java without any external game engines or libraries**. All rendering, input handling, game logic, and resource management were done using **core Java (AWT/Swing)** and standard libraries.

This project showcases custom game engine logic, tile-based world rendering, a basic battle system, item management, and resource loading from JSON and sprite sheets.

---

## 🕹️ Controls

| Key       | Action             |
|-----------|--------------------|
| `W`       | Move Up            |
| `A`       | Move Left          |
| `S`       | Move Down          |
| `D`       | Move Right         |
| `J`       | Confirm / Interact |
| `K`       | Cancel / Go Back   |
| `Enter`   | Open Main Menu     |

---

## ✨ Features

- 🔹 **Custom Game Loop** – written from scratch using Java
- 🔹 **Tile-Based Rendering** – loads maps from `.json` files exported via Tiled
- 🔹 **Resource Management** – sprites, tilesets, and data loaded using Java class loaders
- 🔹 **Basic Battle System** – with animations, HP bars, and Pokémon stats
- 🔹 **Inventory System** – reads item definitions from JSON and displays item sprites
- 🔹 **Sound Effects** – includes menu, battle, and victory sounds

---

### Requirements
- Java 18 or later

---

### Instructions
1. Download or clone the repository.
2. Compile the project or use the provided `.jar` file:
   ```bash
   java -jar PokemonClone.jar
