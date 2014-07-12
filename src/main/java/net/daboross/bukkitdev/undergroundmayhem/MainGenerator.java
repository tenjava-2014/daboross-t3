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
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class MainGenerator extends ChunkGenerator {

    private static final int WALLS_DISTANCE = 100;
    private SimplexNoiseGenerator noiseGenerator;

    private SimplexNoiseGenerator getNoiseGenerator(World world) {
        if (noiseGenerator == null) {
            noiseGenerator = new SimplexNoiseGenerator(world);
        }
        return noiseGenerator;
    }

    @SuppressWarnings("deprecation")
    @Override
    public byte[][] generateBlockSections(final World world, final Random random, final int chunkX, final int chunkZ, final BiomeGrid biomes) {
        NoiseGenerator generator = getNoiseGenerator(world);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                biomes.setBiome(x, z, Biome.FLOWER_FOREST);
            }
        }
        byte[][] result = new byte[world.getMaxHeight() / 16][];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int bedrockHeight = (int) (1 + generator.noise(chunkX * 16 + x, chunkZ * 16 + z) * 5);
                int stoneHeight = (int) (60 + generator.noise(chunkX * 16 + x, chunkZ * 16 + z) * 20);
                int obsidianHeight = stoneHeight + (int) generator.noise(chunkX * 16 + x, chunkZ * 16 + z);
                int bedrockTop = (int) (world.getMaxHeight() - 1 - -generator.noise(chunkX * 16 + x, chunkZ * 16 + z) * 5);
                int glowStoneTop = (int) (world.getMaxHeight() - 40 - generator.noise(chunkX * 16 + x, chunkZ * 16 + z) * 5);
                for (int y = 0; y < obsidianHeight; y++) {
                    if (result[y >> 4] == null) {
                        result[y >> 4] = new byte[4096];
                    }
                    Material material;
                    if (y <= bedrockHeight) {
                        material = Material.BEDROCK;
                    } else if (y <= stoneHeight) {
                        material = Material.STONE;
                    } else {
                        material = Material.OBSIDIAN;
                    }
                    result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
                }
                for (int y = world.getMaxHeight() - 1; y > glowStoneTop; y--) {
                    if (result[y >> 4] == null) {
                        result[y >> 4] = new byte[4096];
                    }
                    Material material;
                    if (y >= bedrockTop) {
                        material = Material.BEDROCK;
                    } else {
                        material = Material.GLOWSTONE;
                    }
                    result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
                }
            }
        }

        return result;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.<BlockPopulator>asList(new WallPopulator());
    }
}