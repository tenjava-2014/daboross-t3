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
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

// This class has some *very* convoluted loops in it, but it functions!
// I'd explain more of how they work, but I'm running out of time, if you understand.
public class TowerPopulator extends BlockPopulator {

    private static final List<BlockFace> FACES = Arrays.asList(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);

    private void ensureAdjacentChunkLoaded(Block block, BlockFace face) throws AdjacentChunkNotLoadedException {
        Location location = block.getLocation().add(face.getModX(), face.getModY(), face.getModZ());
        if (!block.getWorld().isChunkLoaded((int) location.getX() >> 4, (int) location.getZ() >> 4)) {
            throw new AdjacentChunkNotLoadedException();
        }
    }

    private Block isLedge(Block startingPosition, BlockFace ledgeDirection, int count) throws AdjacentChunkNotLoadedException {
        boolean switched = false;
        boolean aheadIsLedge = true;
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
            if (currentBlock.getRelative(ledgeDirection).getType() != Material.AIR) {
                return currentBlock.getRelative(ledgeDirection);
            }
            if (currentBlock.getType() != Material.AIR) {
                return currentBlock;
            }
            if (currentBlock.getRelative(ledgeDirection).getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                if (!aheadIsLedge) {
                    Block ledgeBlock = isLedge(currentBlock.getRelative(ledgeDirection), ledgeDirection, count + 1);
                    if (ledgeBlock != null) {// if we aren't a ledge, report so.
                        return ledgeBlock;
                    } else {
                        aheadIsLedge = true;
                    }
                }
            } else {
                aheadIsLedge = false;
            }
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
                while (currentBlock.getType() != Material.AIR) {
                    if (currentBlock.getType() != Material.STONE && currentBlock.getType() != Material.BEDROCK) {
                        // We won't cross any other blocks.
                        return;
                    }
                    currentBlock = currentBlock.getRelative(BlockFace.UP);
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
                    Block nonLedgeBlock = isLedge(currentBlock, face, 0);
                    if (nonLedgeBlock != null) {
                        currentBlock = nonLedgeBlock;
                        onTop = false;
                        break;
                    }
                }
                if (onTop) {
                    makeTower(currentBlock, random);
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

    public void makeTower(Block block, Random random) {

        for (int y = -3; y <= 0; y++) {
            Block yBlock = block.getRelative(0, y, 0);
            for (BlockFace face : BlockFace.values()) {
                if (yBlock.getRelative(face).getType() == Material.AIR) {
                    yBlock.getRelative(face).setType(Material.STONE);
                }
            }
        }
        for (int y = 0; y <= 8; y++) {
            Block yBlock = block.getRelative(0, y, 0);
            yBlock.setType(Material.LOG);
            for (BlockFace face : FACES) {
                if (random.nextBoolean()) {
                    yBlock.getRelative(face).setType(Material.STONE);
                } else {
                    yBlock.getRelative(face).setType(Material.BRICK);
                }
            }
        }
        for (int y = 9; y <= 19; y++) {
            Block yBlock = block.getRelative(0, y, 0);
            yBlock.setType(Material.GLASS);
        }
        block.getRelative(0, 20, 0).setType(Material.GLOWSTONE);

        block.setType(Material.CHEST);
        Chest chest = (Chest) block.getState();
        Inventory inv = chest.getBlockInventory();

        // populate with items
        for (int i = 0; i < 1 + random.nextInt(2); i++) {
            switch (random.nextInt(4)) {
                case 0:
                    inv.addItem(new ItemStack(Material.APPLE, 1 + random.nextInt(4)));
                case 1:
                    inv.addItem(new ItemStack(Material.WOOD_PICKAXE, 1));
                case 2:
                    if (random.nextInt(100) > 3) {
                        inv.addItem(new ItemStack(Material.GOLDEN_APPLE, 1 + random.nextInt(1)));
                    }
                case 3:
                    inv.addItem(new ItemStack(Material.LOG, 5 + random.nextInt(20)));
            }
        }
        chest.update();
    }
}
