/*
 * Copyright (C) 2013 mewin<mewin001@hotmail.de>
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

package de.mewin.wgchf;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class WorldListener implements Listener
{    
    private WorldGuardPlugin wgp;
    
    public WorldListener(WorldGuardPlugin wgp)
    {
        this.wgp = wgp;
    }
    
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e)
    {
        RegionManager rm = wgp.getRegionManager(e.getWorld());
        
        if (rm != null)
        {
            ProtectedCuboidRegion chunkRegion = new ProtectedCuboidRegion("tmpChunkRegion", 
                    new BlockVector(e.getChunk().getX() * 16, 0, e.getChunk().getZ() * 16), 
                    new BlockVector(e.getChunk().getX() * 16 + 15, 255, e.getChunk().getZ() * 16 + 15));
            
            for (ProtectedRegion region : rm.getApplicableRegions(chunkRegion))
            {
                if (region.getFlag(WGChunkFlagPlugin.UNLOAD_FLAG) == StateFlag.State.DENY)
                {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }
    
    @EventHandler
    public void onWorldLoad(WorldLoadEvent e)
    {
        RegionManager rm = wgp.getRegionManager(e.getWorld());
        
        if (rm != null)
        {
            for (ProtectedRegion region : rm.getRegions().values())
            {
                if (region.getFlag(WGChunkFlagPlugin.UNLOAD_FLAG) == StateFlag.State.DENY)
                {
                    BlockVector min = region.getMinimumPoint();
                    BlockVector max = region.getMaximumPoint();
                    Location minLoc = new Location(e.getWorld(), min.getX(), min.getY(), min.getZ());
                    Location maxLoc = new Location(e.getWorld(), max.getX(), max.getY(), max.getZ());
                    
                    for (int x = minLoc.getChunk().getX(); x <= maxLoc.getChunk().getX(); x++)
                    {
                        for (int z = minLoc.getChunk().getZ(); z <= maxLoc.getChunk().getZ(); z++)
                        {
                            e.getWorld().getChunkAt(x, z).load();
                        }
                    }
                }
            }
        }
    }
}