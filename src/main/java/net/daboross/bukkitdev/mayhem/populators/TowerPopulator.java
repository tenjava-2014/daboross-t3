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
import java.util.List;
import java.util.Random;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class TowerPopulator extends BlockPopulator {

    private static final List<BlockFace> FACES = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);

    private void ensureAdjacentChunkLoaded(Block block, BlockFace face) throws AdjacentChunkNotLoadedException {
        Location location = block.getLocation().add(face.getModX(), face.getModY(), face.getModZ());
        if (!block.getWorld().isChunkLoaded((int) location.getX() >> 4, (int) location.getZ() >> 4)) {
            throw new AdjacentChunkNotLoadedException();
        }
    }

    private Block isLedge(Block startingPosition, BlockFace ledgeDirection) throws AdjacentChunkNotLoadedException {
        boolean switched = false;
        BlockFace currentDirection;
        if (ledgeDirection == BlockFace.NORTH || ledgeDirection == BlockFace.SOUTH) {
            currentDirection = BlockFace.EAST;
        } else {
            currentDirection = BlockFace.NORTH;
        }
        Block currentBlock = startingPosition;
        while (true) {
            ensureAdjacentChunkLoaded(currentBlock, ledgeDirection);
            ensureAdjacentChunkLoaded(currentBlock, currentDirection);
            if (currentBlock.getRelative(ledgeDirection).getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                Block ledgeBlock = isLedge(currentBlock.getRelative(ledgeDirection), ledgeDirection);
                if (ledgeBlock != null) {// if we aren't a ledge, report so.
                    return ledgeBlock;
                }
            }
            if (currentBlock.getRelative(ledgeDirection).getType() != Material.AIR)
                return currentBlock.getRelative(ledgeDirection);
            currentBlock = currentBlock.getRelative(currentDirection);
            if (currentBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                if (switched) {
                    return null; // We're a ledge!
                } else {
                    switched = true;
                    currentDirection = currentDirection.getOppositeFace();
                    currentBlock = currentBlock.getRelative(currentDirection);
                }
            }
        }
    }

    @Override
    public void populate(final World world, final Random random, final Chunk source) {
        try {
            Block currentBlock = source.getBlock(0, 0, 0);
            while (true) {
                if (currentBlock.getType() != Material.AIR) {
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
                    ensureAdjacentChunkLoaded(currentBlock, face);
                    if (currentBlock.getRelative(face).getType() != Material.AIR) {
                        currentBlock = currentBlock.getRelative(face);
                        changed = true;
                        break;
                    }
                }
                if (changed) {
                    continue;
                }
                boolean onTop = true;
                for (BlockFace face : FACES) {
                    Block nonLedgeBlock = isLedge(currentBlock, face);
                    if (nonLedgeBlock != null) {
                        currentBlock = nonLedgeBlock;
                        onTop = false;
                        break;
                    }
                }
                if (onTop) {
                    currentBlock.setType(Material.WOOL);
                    return;
                }
            }
        } catch (AdjacentChunkNotLoadedException ignored) {
            // Let's let the loaded chunk deal with this.
            // ignore the exception and just don't put a tower.
        }
    }

    public class AdjacentChunkNotLoadedException extends Exception {

        public AdjacentChunkNotLoadedException() {
        }
    }
}
