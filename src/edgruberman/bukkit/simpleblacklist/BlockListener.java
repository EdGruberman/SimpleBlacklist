package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener extends org.bukkit.event.block.BlockListener {
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getBlock().getType())) return;
        
        Main.notify(event.getPlayer(), event.getBlock().getType());
        event.setCancelled(true);
    }
}