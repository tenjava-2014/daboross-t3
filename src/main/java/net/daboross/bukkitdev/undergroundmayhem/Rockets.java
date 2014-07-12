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

import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

public class Rockets {

    private static final String name = ChatColor.RED + "Rocket";
    private static final List<String> lore = Arrays.asList(ChatColor.GREEN + "It's a rocket");

    public ItemStack makeRocket() {
        ItemStack rocket = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = rocket.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        rocket.setItemMeta(meta);
        return rocket;
    }

    public boolean isRocket(ItemStack rocket) {
        if (rocket.getType() != Material.BLAZE_ROD) return false;
        ItemMeta meta = rocket.getItemMeta();
        return name.equals(meta.getDisplayName()) && lore.equals(meta.getLore());
    }

    public void launch(Player p) {
        knockback(p);
        shootFrom(p);
    }

    private void knockback(Player p) {
        double force = 0.2;
        Vector vectorForce = p.getLocation().getDirection().multiply(-force);
        Vector playerVelocitory = p.getVelocity();
        p.setVelocity(playerVelocitory.add(vectorForce));
    }

    private void shootFrom(Player player) {
        Location arrowLocation = player.getEyeLocation().clone(); // Clone the player's position
        arrowLocation.add(arrowLocation.getDirection()); // Move one blcok away
        Vector direction = arrowLocation.getDirection(); // Get the direction
        Arrow a = arrowLocation.getWorld().spawnArrow(arrowLocation, direction, 2.0f, 2);
        a.setBounce(false);
        a.setShooter(player);
    }
}
