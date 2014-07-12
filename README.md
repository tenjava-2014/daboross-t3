StoneMayhem
===========

This is daboross's submission for the 2014 ten.java contest. Here's some contest-related info you might need:

- __Theme:__ What sort of world generation could result in a completely different survival experience?
- __Time 3:__ (7/12/2014 14:00 to 7/13/2014 00:00 UTC)
- __MC Version:__ 1.7.9 (latest Bukkit beta)
- __Stream URL:__ https://twitch.tv/daboross

Compiling
---------

- Download & Install [Maven 3](http://maven.apache.org/download.html)
- Clone the repository: `git clone https://github.com/tenjava/daboross-t3`
- Compile and create the plugin package using Maven: `mvn`

Maven will download all required dependencies and build a ready-for-use plugin jar at `target/StoneMayhem.jar`.

Usage
-----

* Move plugin StoneMayhem.jar file to plugins/ dir
* Add the following to `bukkit.yml` to enable for the default world:
  ```
  worlds:
    world:
      generator: StoneMayhem
  ```
* Delete the `world/` world folder, so that it will regenerate with StoneMayhem
* Start the server.
* Expore! You'll find lava/water split lakes, and towers.
 * Look inside the origin chest (above two obsidian blocks, near the spawn point) for some starting materials (including an Iron pickaxe, but also other random items).
 * You'll find towers on top of mountains, each tower contains a bunch of wood inside it that you can use to burn in a furnace,
   or make tools to fight with. There are no trees, so the wood you find in these towers is all you get.
 * Each tower also has a chest with some extra goodies in it, that you will find useful for survival!
