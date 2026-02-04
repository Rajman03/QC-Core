# QC-Core 🚀

**QC-Core** is a comprehensive Minecraft server core plugin designed for performance, security, and advanced administration. Built for version **1.19.4**, it provides a wide range of features from custom queue systems to deep Discord integration.

## ✨ Features

### 🛡️ Administration & Security
- **Console Filtering**: Automatically hides sensitive information like passwords and specific commands from the server console.
- **Advanced Logging**: All critical events (joins, quits, admin actions) are logged asynchronously to a **Discord Webhook**.
- **Comprehensive Admin Command**: A powerful `/qc` command that consolidates dozens of administrative tasks.
- **Vanish System**: Full invisibility for admins to monitor the server.

### 👥 Player Management
- **Smart Queue System**: Handles player connections when the server reaches its capacity limit.
- **IP Lookup**: Quickly retrieve player connection details.
- **Teleportation Tools**: Enhanced TP commands for single and double player targets.
- **Auto-OP/De-OP**: Simplified operator status management.

### 🛠️ Utilities & Items
- **Quick Item Give**: Shortcuts for common items (e.g., `/qc cc` for food, `/qc aa` for diamonds).
- **Enchanted Books**: Instant generation of max-level enchanted books.
- **Repair & Dupe**: One-click repair for entire inventories and item duplication.
- **Potion Effects**: Preset sets for PvP or individual effects with custom durations.

### ⚡ Performance
- **Java 17 Optimized**: Utilizes modern Java features for better efficiency.
- **Async Processing**: Discord logging and sensitive tasks are handled off the main thread to prevent TPS drops.

---

## 🛠️ Commands

The primary command is `/qc` (Requirement: **Server Operator / OP**).

| Command | Description |
| :--- | :--- |
| `/qc v [player]` | Toggle vanish mode |
| `/qc gms/gmc/gmsp/a` | Quick gamemode switching |
| `/qc tp <player> [target]` | Teleport player(s) |
| `/qc rr` | Repair all items in inventory |
| `/qc dd` | Duplicate held item |
| `/qc e <effect>` | Apply potion effects (e.g., `pvp`, `str`, `res`) |
| `/qc a [speed]` | Adjust attack speed |
| `/qc ma <mult>` | Set damage dealt multiplier |
| `/qc mb <mult>` | Set damage received multiplier |
| `/qc k <player>` | Kick a player immediately |
| `/qc ip <player>` | Get player's IP address |
| `/qc cmdconsole <cmd>` | Execute a command as console |

### 📦 Item Shortcuts
- `/qc cc`: Cooked Porkchop
- `/qc ee`: Ender Pearls
- `/qc bb`: Blaze Rods
- `/qc tt`: Totem of Undying
- `/qc kkp/kks/kku...`: Max enchanted books (Protection, Sharpness, Unbreaking, etc.)

---

## 🚀 Installation & Setup

1. **Requirements & Compatibility**: 
   - Java 17+ (Built-in runtime check)
   - Minecraft Server 1.19+ (Wszystkie silniki: Spigot, Paper, Purpur itp.)
   - **ProtocolLib** (provided in `libs/`)
2. **Build**:
   - Run `mvn clean package` to generate the shaded JAR.
3. **Deploy**:
   - Place the generated `QC-Core-1.0-SNAPSHOT.jar` in your server's `plugins` folder.
   - Ensure `libs/ProtocolLib.jar` is available if building from source.

## ⚙️ Development

**Built with:**
- [Spigot-API](https://hub.spigotmc.org/nexus/content/repositories/snapshots/) (1.19.4-R0.1-SNAPSHOT)
- [ProtocolLib](https://github.com/dmulloy2/ProtocolLib) (5.1.0)
- [Maven](https://maven.apache.org/)

---
*Created by Antigravity for QC Project.*
