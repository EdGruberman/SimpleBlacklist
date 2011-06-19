package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.player.PlayerInteractEvent;

import edgruberman.bukkit.messagemanager.MessageLevel;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getPlayer().getItemInHand().getType())) return;
        
        String message = Main.getMessage(event.getPlayer().getItemInHand().getTypeId());
        if (message.length() != 0)
            Main.messageManager.send(event.getPlayer(), MessageLevel.RIGHTS, message);
        
        event.setCancelled(true);
    }
}