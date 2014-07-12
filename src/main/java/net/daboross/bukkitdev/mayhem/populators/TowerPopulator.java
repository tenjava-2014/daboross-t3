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
package net.daboross.bukkitdev.mayhem.populators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class TowerPopulator extends BlockPopulator {

    private static final List<BlockFace> FACES = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);

    private boolean isAdjacentChunkLoaded(Block block, BlockFace face) {
        Location location = block.getLocation().add(face.getModX(), face.getModY(), face.getModZ());
        return block.getWorld().isChunkLoaded((int) location.getX() >> 4, (int) location.getZ() >> 4);
    }

    @Override
    public void populate(final World world, final Random random, final Chunk source) {
        Block currentBlock = source.getBlock(0, 0, 0);
        Set<BlockFace> onTopOf = new HashSet<>(); // Might be inefficient, but it's pretty and nice.
        while (true) {
            if (currentBlock.getType() != Material.AIR) {
                // Just an if as well so we can clear onTopOf.
                onTopOf.clear();
                while (currentBlock.getType() != Material.AIR) {
                    if (currentBlock.getType() == Material.WOOL) {
                        // This is only used to create towers. We shouldn't make more than one wool block!
                        return;
                    }
                    currentBlock = currentBlock.getRelative(BlockFace.UP);
                }
            }
            boolean changed = false;
            for (BlockFace face : FACES) {
                if (!isAdjacentChunkLoaded(currentBlock, face)) {
                    return; // Let's let the loaded chunk deal with this.
                }
                if (currentBlock.getRelative(face).getType() != Material.AIR) {
                    currentBlock = currentBlock.getRelative(face);
                    changed = true;
                    break;
                }
            }
            if (changed) {
                continue;
            }
            for (BlockFace face : FACES) {
                if (onTopOf.contains(face)) {
                    continue;
                }
                if (!isAdjacentChunkLoaded(currentBlock, face)) {
                    return; // Let's let the loaded chunk deal with this.
                }
                Block faceRelative = currentBlock.getRelative(face);
                while (faceRelative.getRelative(BlockFace.DOWN).getType() != Material.AIR && faceRelative.getType() == Material.AIR) {
                    currentBlock = faceRelative;
                    faceRelative = currentBlock.getRelative(face);
                }
                if (faceRelative.getType() != Material.AIR) {
                    break; // Let's let the first two inner loops deal with this.
                }
                // We're on top!
                onTopOf.add(face);
            }
            if (onTopOf.containsAll(FACES)) {
                currentBlock.setType(Material.WOOL);
                return;
            }
        }
    }
}
