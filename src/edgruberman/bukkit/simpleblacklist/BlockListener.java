package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

public class BlockListener extends org.bukkit.event.block.BlockListener {
    
    BlockListener(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvent(Event.Type.BLOCK_PLACE, this, Event.Priority.Normal, plugin);
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getBlock().getType())) return;
        
        Main.notify(event.getPlayer(), event.getBlock().getType());
        event.setCancelled(true);
    }
}