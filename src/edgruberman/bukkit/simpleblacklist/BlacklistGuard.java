package edgruberman.bukkit.simpleblacklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

final class BlacklistGuard implements Listener {

    private final Plugin plugin;
    private final Map<Integer, BlacklistEntry> blacklist = new HashMap<Integer, BlacklistEntry>();
    private String denied = null;

    BlacklistGuard(final Plugin plugin, final List<BlacklistEntry> blacklist, final String denied) {
        this.plugin = plugin;
        for (final BlacklistEntry entry : blacklist) this.blacklist.put(entry.getMaterial().getId(), entry);
        this.denied = denied;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean isAllowed(final Player player, final Material material) {
        final BlacklistEntry entry = this.blacklist.get(material.getId());
        if (entry == null) return true;

        return entry.isAllowed(player);
    }

    private void notify(final Player player, final Material material, final Location location) {
        this.plugin.getLogger().fine(player.getName() + " attempted to use blacklisted " + material.toString()
                + " at x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());

        if (this.denied == null) return;

        final String message = String.format(this.denied, this.blacklist.get(material.getId()).getDescription());
        if (message.length() == 0) return;

        MessageManager.of(this.plugin).tell(player, message, MessageLevel.RIGHTS, false);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (this.isAllowed(event.getPlayer(), event.getBlock().getType())) return;

        event.setCancelled(true);
        this.notify(event.getPlayer(), event.getBlock().getType(), event.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        // Check the item being used
        if (!this.isAllowed(event.getPlayer(), event.getPlayer().getItemInHand().getType())) {
            event.setCancelled(true);
            this.notify(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getPlayer().getLocation());
            return;
        }

        // Check the block being clicked
        if (!this.isAllowed(event.getPlayer(), event.getClickedBlock().getType())) {
            event.setCancelled(true);
            this.notify(event.getPlayer(), event.getClickedBlock().getType(), event.getClickedBlock().getLocation());
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        if (this.isAllowed(event.getPlayer(), event.getPlayer().getItemInHand().getType())) return;

        event.setCancelled(true);
        this.notify(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getRightClicked().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent event) {
        if (this.isAllowed(event.getPlayer(), event.getBucket())) return;

        event.setCancelled(true);
        this.notify(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getBlockClicked().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(final PlayerBucketFillEvent event) {
        if (this.isAllowed(event.getPlayer(), event.getBucket())) return;

        event.setCancelled(true);
        this.notify(event.getPlayer(), event.getPlayer().getItemInHand().getType(), event.getBlockClicked().getLocation());
    }

}
