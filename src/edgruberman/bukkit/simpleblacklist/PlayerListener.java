package edgruberman.bukkit.simpleblacklist;

import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PlayerListener extends org.bukkit.event.player.PlayerListener {
    
    PlayerListener(Plugin plugin) {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, this, Event.Priority.Normal, plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, this, Event.Priority.Normal, plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_BUCKET_FILL, this, Event.Priority.Normal, plugin);
        pluginManager.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, this, Event.Priority.Normal, plugin);
    }
    
    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) return;
        
        // Check the item being used.
        if (!Main.isAllowed(event.getPlayer(), event.getPlayer().getItemInHand().getType())) {
            event.setCancelled(true);
            Main.notify(event.getPlayer(), event.getPlayer().getItemInHand().getType());
            return;
        }
        
        // Check the block being clicked.
        if (!Main.isAllowed(event.getPlayer(), event.getClickedBlock().getType())) {
            event.setCancelled(true);
            Main.notify(event.getPlayer(), event.getClickedBlock().getType());
            return;
        }
    }
    
    @Override
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getPlayer().getItemInHand().getType())) return;
        
        Main.notify(event.getPlayer(), event.getPlayer().getItemInHand().getType());
        event.setCancelled(true);
    }
    
    @Override
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getBucket())) return;
        
        Main.notify(event.getPlayer(), event.getBucket());
        event.setCancelled(true);
    }
    
    @Override
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;
        
        if (Main.isAllowed(event.getPlayer(), event.getBucket())) return;
        
        Main.notify(event.getPlayer(), event.getBucket());
        event.setCancelled(true);
    }
}