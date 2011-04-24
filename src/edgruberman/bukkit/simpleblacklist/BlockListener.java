package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.block.BlockPlaceEvent;

import edgruberman.bukkit.simpleblacklist.MessageManager.MessageLevel;

public class BlockListener extends org.bukkit.event.block.BlockListener {
    
    private Main main;
    
    public BlockListener(Main main) {
        this.main = main;
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        
        if (this.main.isAllowed(event.getPlayer(), event.getBlock().getType())) return;
        
        String message = this.main.blacklist.get(event.getBlock().getTypeId());
        if (message.length() != 0)
            Main.messageManager.send(event.getPlayer(), MessageLevel.RIGHTS, message);
        
        event.setCancelled(true);
    }
}