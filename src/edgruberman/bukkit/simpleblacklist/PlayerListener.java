package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import edgruberman.bukkit.simpleblacklist.MessageManager.MessageLevel;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {
    
    private Main main;
    
    public PlayerListener(Main main) {
        this.main = main;
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        if (this.main.isAllowed(event.getPlayer(), event.getPlayer().getItemInHand().getType())) return;
        
        String message = this.main.blacklist.get(event.getPlayer().getItemInHand().getTypeId());
        if (message.length() != 0)
            Main.messageManager.send(event.getPlayer(), MessageLevel.RIGHTS, message);
        
        event.setCancelled(true);
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Update online players lists to include this player if appropriate.
        this.main.addOnlinePlayer(event.getPlayer());
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Update online players lists to remove this player if appropriate.
        this.main.removeOnlinePlayer(event.getPlayer());
    }
}