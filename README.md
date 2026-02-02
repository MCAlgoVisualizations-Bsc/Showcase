# Thesis Minecraft Server (Minestom)

This is a custom Minecraft server built using the **Minestom** framework. It runs on a high-performance multi-container setup via Docker and DigitalOcean.

---

## 🎮 How to Join

To join, open Minecraft Java Edition (Version 1.21 or newer) and use **Direct Connect** with the addresses below.

### 🚀 Production Server (Official)
The stable version of the project.
- **Address:** `123.456.78.90`
- **Port:** `25565`

### 🛠️ Development Server (Beta)
Where we test the latest features. Expect bugs!
- **Address:** `123.456.78.90:25566`
- **Port:** `25566`

---

## 🛠️ Technical Info
- **Engine:** Minestom (Non-Mojang rewrite)
- **Language:** Kotlin / Java
- **OS:** CachyOS (Local) / Ubuntu (Server)
- **Infrastructure:** Docker Containers on DigitalOcean

---

## 🚀 Local Development
1. Clone the repository.
2. Run `./gradlew shadowJar` to build the "fat" jar.
3. Run `java -jar build/libs/*-all.jar` to start the server locally.
4. Connect via `localhost:25565`.
