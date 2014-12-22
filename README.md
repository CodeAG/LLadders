## Luppii's Ladders

Official sites:
* Repository: https://github.com/CodeAG/LLadders
* Webpage: http://ladders.luppii.se
* Minecraftforum.net thread: http://www.minecraftforum.net/topic/2568061-172164-luppiis-ladders/

Issues and feature requests, register them at https://github.com/CodeAG/LLadders/issues.

### Setup dev environment for Luppii's Ladders
1. Ensure that you have [Java JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html) and [Git](http://git-scm.com/). If you use Windows you can replace Git with [Github for Windows](https://windows.github.com/) for easy (point-and-click) cloning and commiting.
2. Clone the LLadders repository.
3. You will need a deobfuscated copy of Ropes+, Thaumcraft-api and Biomes O' Plenty in order to build (not required for dev environment).
 * You need to build an deobfuscated version from source
    ** You can find the source code for Ropes+ at: https://code.google.com/p/atomicstrykers-minecraft-mods/source/browse
    ** Place the deobfuscated Ropes+ jar-file in the 'libs' (create it if necessarily) folder in root directory (LLadders) for the repository.
 
 * There is no jar for ThaumcraftAPI and we use Eclipse so here are the instructions for Eclipse:
    ** Download Thaumcraft-api zip-file from here https://github.com/Azanor/thaumcraft-api and extract it to src/main/java
    ** Open Eclipse and there should now be a new source tree for thaumcraft-api. Right click it and choose "Export" then export it as a Jar file and check the option to have the source files included. 
    ** Move the Jar file from where you saved it into the 'libs' folder
    ** Remove thaumcraft-api folder from src/main/java
 
 * For Biomes O' Plenty we use the deobfuscated jar. The API jar doesn't include the class we use in the api package, but the deobfuscated jar does.
 ** Download the deobf for 1.7.10 from http://files.minecraftforge.net/BiomesOPlenty/ and place it in the 'libs' folder
 
>>>>>>> origin/Biomeoplenty
4. Navigate to root directory (LLadders) for the repository using the terminal/command prompt and run the following commands:
 * `gradlew setupDecompWorkspace` to setup a complete development environment.
 * `gradlew --refresh-dependencies eclipse` for a pre-built Eclipse java project. If you use `IntelliJ IDEA` you can replace `eclipse` with `idea`.
 * On Windows: use `gradlew.bat` instead of `gradlew`.
5. Building a ready to use package:
 * `gradlew build` to build the .jar-file.
 * The compiled and obfuscated file will be located in 'LLadders/build/libs/'.

### Run configurations in Eclipse
* Client:
  * Main class: `net.minecraft.launchwrapper.Launch`
  * Program arguments: `--version 1.7 --tweakClass cpw.mods.fml.common.launcher.FMLTweaker --accessToken dev -username=Player --userProperties {} --assetIndex 1.7.10 --assetsDir ~/.gradle/caches/minecraft/assets`
    * You might need to replace `--assetsDir ~/.gradle/caches/minecraft/assets` with an absolute path to that folder.
  * VM arguments: `-Dfml.ignoreInvalidMinecraftCertificates=true`
* Server:
  * Main class: `cpw.mods.fml.relauncher.ServerLaunchWrapper`
  * Starting the server:
    1. Run server once, it will crash. Edit `eula=false ` inside 'eula.txt' to `eula=true`.
    2. Run server again. Once loaded, stop it and edit `online-mode=true` inside 'server.properties' to `online-mode=false`
