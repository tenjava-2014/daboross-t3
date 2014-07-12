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
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.OctaveGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

public class MainGenerator extends ChunkGenerator {

    private final double frequency;
    private final double amplitude;
    private SimplexOctaveGenerator octave;
    private SimplexNoiseGenerator noise;

    public MainGenerator(final double frequency, final double amplitude) {
        this.frequency = frequency;
        this.amplitude = amplitude;
    }

    private SimplexOctaveGenerator getOctave(World world) {
        if (octave == null) {
            octave = new SimplexOctaveGenerator(world, 5);
        }
        octave.setScale(1 / 256.0);
        return octave;
    }

    private SimplexNoiseGenerator getNoise(World world) {
        if (noise == null) {
            noise = new SimplexNoiseGenerator(world);
        }
        return noise;
    }

    @SuppressWarnings("deprecation")
    @Override
    public byte[][] generateBlockSections(final World world, final Random random, final int chunkX, final int chunkZ, BiomeGrid grid) {
        OctaveGenerator octave = getOctave(world);
        NoiseGenerator noise = getNoise(world);
        byte[][] result = new byte[world.getMaxHeight() / 16][];
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int bedrockHeight = (int) (1 + noise.noise(chunkX * 16 + x, chunkZ * 16 + z) * 2);
                int stoneHeight = (int) (30 + octave.noise(chunkX * 16 + x, chunkZ * 16 + z, frequency, amplitude));
                if (stoneHeight > world.getMaxHeight()) {
                    stoneHeight = world.getMaxHeight();
                }
                for (int y = 0; y <= stoneHeight || y < bedrockHeight; y++) {
                    if (result[y >> 4] == null) {
                        result[y >> 4] = new byte[4096];
                    }
                    Material material;
                    if (y <= bedrockHeight) {
                        material = Material.BEDROCK;
                    } else {
                        material = Material.STONE;
                    }
                    result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = (byte) material.getId();
                }
            }
        }
        return result;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.<BlockPopulator>asList(new ChestPopulator());
    }
}