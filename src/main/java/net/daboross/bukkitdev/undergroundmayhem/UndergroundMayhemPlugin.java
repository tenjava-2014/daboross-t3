/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
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

import java.io.IOException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.MetricsLite;

public class UndergroundMayhemPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        MetricsLite metrics = null;
        try {
            metrics = new MetricsLite(this);
        } catch (IOException ex) {
            // We just won't do metrics
        }
        if (metrics != null) {
            metrics.start();
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
        return new MainGenerator(2, 3);
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length < 2) {
            return false;
        }
        double frequency = Double.parseDouble(args[0]);
        double amplitude = Double.parseDouble(args[1]);
        World mayhem = Bukkit.createWorld(new WorldCreator("Mayhem").generator(new MainGenerator(frequency, amplitude)));
        sender.sendMessage("Sending to Mayhem");
        ((Player) sender).teleport(mayhem.getSpawnLocation());
        return true;
    }
}
