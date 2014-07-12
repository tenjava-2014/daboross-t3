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
package net.daboross.bukkitdev.undergroundmayhem.populators;

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestPopulator extends BlockPopulator {

    private void createOriginChest(World world, Random r) {
        Location spawn = world.getSpawnLocation();
        // origin chest
        int x = spawn.getBlockX() + 15 - r.nextInt(30);
        int z = spawn.getBlockZ() + 15 - r.nextInt(30);
        Block block = world.getHighestBlockAt(x, z);
        block.setType(Material.OBSIDIAN);
        block = block.getRelative(BlockFace.UP);
        block.setType(Material.OBSIDIAN);
        block = block.getRelative(BlockFace.UP);
        block.setType(Material.CHEST);
    }

    @Override
    public void populate(final World world, final Random r, final Chunk source) {
        if (world.getSpawnLocation().getChunk() == source) createOriginChest(world, r);

        if (r.nextInt(100) > 90) {
            // spawn a chest
            int x = r.nextInt(16) + source.getX() * 16;
            int z = r.nextInt(16) + source.getZ() * 16;
            Block highest = world.getHighestBlockAt(x, z);
            if (!LiquidPopulator.LIQUIDS.contains(highest.getType())) {
                // don't spawn on water
                Block block = highest.getRelative(0, 2, 0);
                block.setType(Material.CHEST);
                Chest chest = (Chest) block.getState();
                Inventory inv = chest.getBlockInventory();

                // populate with items
                for (int i = 0; i < 1 + r.nextInt(2); i++) {
                    switch (r.nextInt(4)) {
                        case 0:
                            inv.addItem(new ItemStack(Material.APPLE, 1 + r.nextInt(4)));
                        case 1:
                            inv.addItem(new ItemStack(Material.WOOD_PICKAXE, 1));
                        case 2:
                            if (r.nextInt(100) > 3) {
                                inv.addItem(new ItemStack(Material.GOLDEN_APPLE, 1 + r.nextInt(1)));
                            }
                        case 3:
                            inv.addItem(new ItemStack(Material.LOG, 5 + r.nextInt(20)));
                    }
                }

                chest.update();
            }
        }
    }
}
