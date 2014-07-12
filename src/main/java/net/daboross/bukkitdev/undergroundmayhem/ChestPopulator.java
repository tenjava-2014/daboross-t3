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

import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class ChestPopulator extends BlockPopulator {

    @Override
    public void populate(final World world, final Random r, final Chunk source) {
        Location spawn = world.getSpawnLocation();
        if (spawn.getChunk() == source) {
            int x = spawn.getBlockX() + 15 - r.nextInt(30);
            int z = spawn.getBlockZ() + 15 - r.nextInt(30);
            Block block = world.getHighestBlockAt(x, z);
            block.setType(Material.OBSIDIAN);
            block = block.getRelative(BlockFace.UP);
            block.setType(Material.OBSIDIAN);
            block = block.getRelative(BlockFace.UP);
            block.setType(Material.CHEST);
        }
    }
}
