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

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

public class OriginChestListener implements Listener {

    private final MayhemPlugin plugin;

    public OriginChestListener(final MayhemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChestOpen(InventoryOpenEvent evt) {
        if (evt.getInventory().getHolder() instanceof Chest && evt.getPlayer() instanceof Player) {
            Block block = ((Chest) evt.getInventory().getHolder()).getBlock();
            if (block.getLocation().distance(block.getWorld().getSpawnLocation()) < 50) {
                evt.setCancelled(true);
                final Player player = (Player) evt.getPlayer();
                plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        Inventory inventory = plugin.getServer().createInventory(null, 9, "Origin");
                        inventory.setItem(4, plugin.getRockets().makeRocket());
                        player.openInventory(inventory);
                    }
                });
            }
        }
    }
}
