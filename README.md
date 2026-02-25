![](https://media.forgecdn.net/attachments/description/null/description_580ca572-62fa-4e19-bd36-c1c9a1f90ec7.png)

# Infernal Expansion Redux

Is an __**in-development**__ _rebuild-from-the-ground-up_ of the iconic[**Infernal Expansion
**](https://www.curseforge.com/minecraft/mc-mods/infernal-expansion) mod, designed for multi-loader support between both
Forge and Fabric on 1.20.1, and NeoForge and Fabric on future versions eventually too. It occupies its own page as it is
both being developed by a different team than the original, and because its final contents will deviate from the
original mod too.

# Glowstone Canyon

![](https://media.forgecdn.net/attachments/1490/399/3-png.png)

The **Glowstone Canyon** biome returns, featuring _Glowstone Stalagmites & Stalagtites_, a desert of _Shimmer Sand_ with
pits obscured by falling _Glimmer Gravel,_ and littered with _Luminous Fungus_, which lights up when approached; and the
prickly _Dullthorns_; Both of which will give you _Luminous_ when touched, an Effect that lights you up, but which will
draw the ire of the local inhabitants...

## Glowsquito

![](https://media.forgecdn.net/attachments/description/1407992/description_3f8fdbd5-a2a0-4725-9a95-d652912b1b9f.gif)

A nasty firefly-like giant Mosquito that will suck the life out of any nearby _Glowstone, Dimstone_ and _Shroomlight_,
turning them into the new _Dimstone, Dullstone_ and _Hollowlight_ respectively. It is neutral by default, but will swarm
you if it thinks it can suck the light out of you via the _Luminous_ effect.

Killing it will have it drop the compressed Glowdust from its guts,_Glowcoke_, which can be used to make special
_Glowlight_\-variants of normal light items.

## Blindsight

![](https://media.forgecdn.net/attachments/description/1407992/description_3ac047c0-045a-485b-a977-0f96ce95985c.gif)

A michevious frog-like clam that swallows _Glowsquitos_ whole. Unlike a Frog, its tongue actually knocks things away!
Don't gain _Luminous_around this thing, or it might just confuse you for a Glowsquito and become a whole lot more
aggressive. When slain, said tongue can actually be eaten as part of a delicious and nutricious Stew.

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

1. **Define the Version:**
   Open `gradle.properties` and add a new property for your dependency's version.
   ```properties
   # gradle.properties
   my_mod_version=1.0.0
   ```

2. **Add the Repository (if needed):**
   If the dependency is hosted on a repository not already listed (e.g., CurseMaven, Modrinth, etc.), add it to
   `buildSrc/src/main/groovy/multiloader-common.gradle` inside the `repositories` block.

3. **Add to Common:**
   Open `common/build.gradle` and add the dependency. Since `common` is not a loader itself, you typically use
   `compileOnly` so the code can reference the classes, or `implementation` if it's a platform-agnostic library.
   ```groovy
   // common/build.gradle
   dependencies {
       compileOnly "com.example.mod:my-mod-common:${my_mod_version}"
   }
   ```

4. **Add to Loaders:**
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

5. **Refresh Gradle:** Run `./gradlew` or refresh the project in your IDE to download the new dependencies.