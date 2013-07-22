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

import com.mewin.WGCustomFlags.FlagManager;
import com.mewin.WGCustomFlags.WGCustomFlagsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import java.util.logging.Level;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author mewin<mewin001@hotmail.de>
 */
public class WGChunkFlagPlugin extends JavaPlugin
{
    public static final StateFlag UNLOAD_FLAG = new StateFlag("chunk-unload", true);
    
    
    private WorldGuardPlugin wgPlugin;
    private WGCustomFlagsPlugin custPlugin;
    private WorldListener listener;

    @Override
    public void onEnable()
    {
        Plugin plug = getServer().getPluginManager().getPlugin("WGCustomFlags");
        
        if (plug == null || !(plug instanceof WGCustomFlagsPlugin) || !plug.isEnabled())
        {
            getLogger().warning("Could not load WorldGuard Custom Flags Plugin, disabling");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else
        {
            custPlugin = (WGCustomFlagsPlugin) plug;
        }
        
        plug = getServer().getPluginManager().getPlugin("WorldGuard");
        
        if (plug == null || !(plug instanceof WorldGuardPlugin) || !plug.isEnabled())
        {
            getLogger().warning("Could not load WorldGuard Plugin, disabling");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        else
        {
            wgPlugin = (WorldGuardPlugin) plug;
        }
        
        try
        {
            custPlugin.addCustomFlags(WGChunkFlagPlugin.class);
            //FlagManager.addFlagDescription("chunk-unload", "Set to deny to keep a region loaded.");
        }
        catch(Exception ex)
        {
            getLogger().log(Level.SEVERE, "Failed to add flags: ", ex);
        }
        
        listener = new WorldListener(wgPlugin);
        getServer().getPluginManager().registerEvents(listener, this);
    }
}