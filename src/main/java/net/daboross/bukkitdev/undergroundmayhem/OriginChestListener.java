/*
 * Copyright (C) 2013 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.undergroundmayhem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class OriginChestListener implements Listener {

    private final MayhemPlugin plugin;
    private final YamlConfiguration storage;
    private final Path saveLocation;
    private List<String> opened;

    public OriginChestListener(final MayhemPlugin plugin) {
        this.plugin = plugin;
        saveLocation = plugin.getDataFolder().toPath().resolve("origin-chest.yml");
        if (Files.exists(saveLocation)) {
            storage = YamlConfiguration.loadConfiguration(saveLocation.toFile());
            opened = storage.getStringList("opened");
        } else {
            storage = new YamlConfiguration();
            storage.options().header("Origin chest store");
            try {
                storage.save(saveLocation.toFile());
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Couldn't save " + saveLocation.toAbsolutePath(), e);
            }
        }
        if (opened == null) {
            opened = new ArrayList<String>();
        }
    }

    public void save() {
        storage.set("opened", opened);
        try {
            storage.save(saveLocation.toFile());
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "Couldn't save " + saveLocation.toAbsolutePath(), e);
        }
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent evt) {
        if (evt.getInventory().getHolder() instanceof Chest && evt.getPlayer() instanceof Player) {
            Block block = ((Chest) evt.getInventory().getHolder()).getBlock();
            if (block.getLocation().distance(block.getWorld().getSpawnLocation()) < 50) {
                evt.setCancelled(true);
                final Player player = (Player) evt.getPlayer();
                UUID uuid = player.getUniqueId();
                if (opened.contains(uuid.toString())) {
                    player.sendMessage(ChatColor.DARK_RED + "You've already opened the origin chest.");
                } else {
                    opened.add(uuid.toString());
                    plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            Inventory inventory = plugin.getServer().createInventory(null, 9, "Origin chest");
                            inventory.setItem(5, plugin.getRockets().makeRocket());
                            inventory.setItem(4, new ItemStack(Material.APPLE));
                            inventory.setItem(6, new ItemStack(Material.APPLE));
                            player.openInventory(inventory);
                        }
                    });
                }
            }
        }
    }

    @EventHandler
    public void onChestClose(InventoryCloseEvent evt) {
        // This is so that the player can't leave any items in the origin chest.
        if (evt.getInventory().getHolder() == null && evt.getPlayer() instanceof Player && evt.getInventory().getTitle().equals("Origin chest")) {
            Player player = (Player) evt.getPlayer();
            if (opened.contains(player.getUniqueId().toString())) {
                for (ItemStack stack : evt.getInventory().getContents()) {
                    if (stack != null) {
                        if (player.getInventory().firstEmpty() == -1) {
                            player.getLocation().getWorld().dropItem(player.getLocation(), stack);
                        } else {
                            player.getInventory().addItem(stack);
                        }
                    }
                }
            }
        }
    }
}
