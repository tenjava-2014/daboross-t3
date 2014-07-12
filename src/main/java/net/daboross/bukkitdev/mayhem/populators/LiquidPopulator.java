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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

public class LiquidPopulator extends BlockPopulator {

    public static final List<Material> LIQUIDS = Arrays.asList(Material.LAVA, Material.WATER, Material.STATIONARY_LAVA, Material.STATIONARY_WATER);

    private Material getMaterial(World world, Chunk source, Random random) {
        Block zeroBlock = source.getBlock(0, 5, 0);
        boolean eastLoaded = world.isChunkLoaded(source.getX() - 1, source.getZ());
        boolean westLoaded = world.isChunkLoaded(source.getX() + 1, source.getZ());
        boolean northLoaded = world.isChunkLoaded(source.getX(), source.getZ() + 1);
        boolean southLoaded = world.isChunkLoaded(source.getX(), source.getZ() - 1);
        for (int xOrZ = 0; xOrZ < 16; xOrZ++) {
            // So much code duplication :( - I need this to be a bit performant, so the elegant list solution is out.
            if (eastLoaded) {
                Material type = zeroBlock.getRelative(-1, 0, xOrZ).getType();
                if (LIQUIDS.contains(type)) {
                    return type;
                }
            }
            if (westLoaded) {
                Material type = zeroBlock.getRelative(16, 0, xOrZ).getType();
                if (LIQUIDS.contains(type)) {
                    return type;
                }
            }
            if (northLoaded) {
                Material type = zeroBlock.getRelative(xOrZ, 0, 16).getType();
                if (LIQUIDS.contains(type)) {
                    return type;
                }
            }
            if (southLoaded) {
                Material type = zeroBlock.getRelative(xOrZ, 0, -1).getType();
                if (LIQUIDS.contains(type)) {
                    return type;
                }
            }
        }
        if (random.nextInt(3) == 0) {
            return Material.WATER;
        } else {
            return Material.LAVA;
        }
    }

    @Override
    public void populate(final World world, final Random random, final Chunk source) {
        Material liquidType = null;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                Block block = source.getBlock(x, 5, z);
                if (block.getType() == Material.AIR) {
                    // We only want to calculate liquid type if we need to use it
                    if (liquidType == null) {
                        liquidType = getMaterial(world, source, random);
                        if (liquidType == Material.STATIONARY_LAVA) {
                            liquidType = Material.LAVA;
                        } else if (liquidType == Material.STATIONARY_WATER) {
                            liquidType = Material.WATER;
                        }
                    }
                    block.setType(liquidType);
                    block = block.getRelative(BlockFace.DOWN);
                    while (block.getType() == Material.AIR) {
                        block.setType(liquidType);
                        block = block.getRelative(BlockFace.DOWN);
                    }
                }
            }
        }
    }
}
