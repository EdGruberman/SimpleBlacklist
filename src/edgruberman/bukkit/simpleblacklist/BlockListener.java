package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.block.BlockPlaceEvent;

import edgruberman.bukkit.messagemanager.MessageLevel;

public class BlockListener extends org.bukkit.event.block.BlockListener {
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getBlock().getType())) return;
        
        String message = Main.getMessage(event.getBlock().getTypeId());
        if (message.length() != 0)
            Main.messageManager.send(event.getPlayer(), MessageLevel.RIGHTS, message);
        
        event.setCancelled(true);
    }
}