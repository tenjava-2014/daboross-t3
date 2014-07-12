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

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class RocketListener implements Listener {

    private final MayhemPlugin plugin;
    private final HashMap<UUID, Long> timeouts = new HashMap<>();

    public RocketListener(final MayhemPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent evt) {
        Player p = evt.getPlayer();
        if (plugin.getRockets().isRocket(p.getItemInHand())) {
            Long timeout = timeouts.get(p.getUniqueId());
            if (timeout == null || timeout < System.currentTimeMillis()) {
                plugin.getRockets().launch(p);
                timeouts.put(p.getUniqueId(), System.currentTimeMillis() + 350);
            }
        }
    }
}
