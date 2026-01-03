<h1 align="center">
    <img src="logo.png" width="75%" alt="Infernal Expansion">
</h1>

<div align="center">
  <a href="https://infernalstudios.org/discord">
    <img alt="Discord" src="https://img.shields.io/discord/681976442220839008?color=yellow&label=Discord&style=for-the-badge">
  </a>

  <a href="https://infernalstudios.org/infernalexpansion/curseforge">
    <img alt="CurseForge Downloads" src="https://img.shields.io/curseforge/dt/395078?color=orange&style=for-the-badge&label=CurseForge">
  </a>

  <a href="https://infernalstudios.org/infernalexpansion/modrinth">
    <img alt="Modrinth Downloads" src="https://img.shields.io/modrinth/dt/ZrpxHZN4?color=red&style=for-the-badge&label=Modrinth">
  </a>
</div>
<br>
<br>

[Infernal Expansion](https://www.curseforge.com/minecraft/mc-mods/infernal-expansion) is an in-development rebuild-from-the-ground-up of the iconic Infernal Expansion mod, designed for multi-loader support between both Forge and Fabric on 1.20.1, and hopefully NeoForge and Fabric on future versions eventually too. It occupies its own page as it is both being developed by a different team than the original, and because its final contents will eventually be different from the original mod too.

Currently, only a basic implementation of the Glowstone Canyon has been readded, alongside numerous building blocks and a handful of generation features in other biomes, but it works well!

If you find any bugs, let us know in the issue tracker!

## Developers

### Building the Project

This project uses Gradle to manage dependencies and building.

**Prerequisites:**
* **JDK 17**: Ensure you have Java 17 installed.

**Basic Commands:**

* **Build everything:**
    ```bash
    ./gradlew build
    ```
  This will compile and build jars for Common, Fabric, and Forge.

* **Run Client:**
  To launch the game from your development environment:
    * **Fabric:** `./gradlew :fabric:runClient`
    * **Forge:** `./gradlew :forge:runClient`

### Adding Dependencies

Dependencies are managed across several files for each loader.
1.  **Define the Version:**
    Open `gradle.properties` and add a new property for your dependency's version.
    ```properties
    # gradle.properties
    my_mod_version=1.0.0
    ```

2.  **Add the Repository (if needed):**
    If the dependency is hosted on a repository not already listed (e.g., CurseMaven, Modrinth, etc.), add it to `buildSrc/src/main/groovy/multiloader-common.gradle` inside the `repositories` block.

3.  **Add to Common:**
    Open `common/build.gradle` and add the dependency. Since `common` is not a loader itself, you typically use `compileOnly` so the code can reference the classes, or `implementation` if it's a platform-agnostic library.
    ```groovy
    // common/build.gradle
    dependencies {
        compileOnly "com.example.mod:my-mod-common:${my_mod_version}"
    }
    ```

4.  **Add to Loaders:**
    Add the loader-specific implementation to `fabric/build.gradle` and `forge/build.gradle` so it is present at runtime.

    * **Fabric (`fabric/build.gradle`):**
        ```groovy
        dependencies {
            modImplementation "com.example.mod:my-mod-fabric:${my_mod_version}"
        }
        ```

    * **Forge (`forge/build.gradle`):**
        ```groovy
        dependencies {
            modImplementation "com.example.mod:my-mod-forge:${my_mod_version}"
        }
        ```

5.  **Refresh Gradle:** Run `./gradlew` or refresh the project in your IDE to download the new dependencies.