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

final class BlacklistGuard implements Listener {

    private final Plugin plugin;
    private final Map<Integer, BlacklistEntry> blacklist = new HashMap<Integer, BlacklistEntry>();

    BlacklistGuard(final Plugin plugin, final List<BlacklistEntry> blacklist) {
        this.plugin = plugin;
        for (final BlacklistEntry entry : blacklist) this.blacklist.put(entry.material.getId(), entry);
    }

    private boolean isAllowed(final Player player, final Material material) {
        final BlacklistEntry entry = this.blacklist.get(material.getId());
        if (entry == null) return true;

        return entry.isAllowed(player);
    }

    private void notify(final Player player, final Material material, final Location location) {
        this.plugin.getLogger().fine(player.getName() + " attempted to use blacklisted " + material.toString()
                + " at x:" + location.getBlockX() + " y:" + location.getBlockY() + " z:" + location.getBlockZ());

        Main.courier.send(player, "denied", this.blacklist.get(material.getId()).description);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent placed) {
        if (this.isAllowed(placed.getPlayer(), placed.getBlock().getType())) return;

        placed.setCancelled(true);
        this.notify(placed.getPlayer(), placed.getBlock().getType(), placed.getBlock().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(final PlayerInteractEvent interaction) {
        // Check the item being used
        if (!this.isAllowed(interaction.getPlayer(), interaction.getPlayer().getItemInHand().getType())) {
            interaction.setCancelled(true);
            this.notify(interaction.getPlayer(), interaction.getPlayer().getItemInHand().getType(), interaction.getPlayer().getLocation());
            return;
        }

        // Check the block being clicked
        if (!this.isAllowed(interaction.getPlayer(), interaction.getClickedBlock().getType())) {
            interaction.setCancelled(true);
            this.notify(interaction.getPlayer(), interaction.getClickedBlock().getType(), interaction.getClickedBlock().getLocation());
            return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent interaction) {
        if (this.isAllowed(interaction.getPlayer(), interaction.getPlayer().getItemInHand().getType())) return;

        interaction.setCancelled(true);
        this.notify(interaction.getPlayer(), interaction.getPlayer().getItemInHand().getType(), interaction.getRightClicked().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketEmpty(final PlayerBucketEmptyEvent empty) {
        if (this.isAllowed(empty.getPlayer(), empty.getBucket())) return;

        empty.setCancelled(true);
        this.notify(empty.getPlayer(), empty.getPlayer().getItemInHand().getType(), empty.getBlockClicked().getLocation());
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(final PlayerBucketFillEvent fill) {
        if (this.isAllowed(fill.getPlayer(), fill.getBucket())) return;

        fill.setCancelled(true);
        this.notify(fill.getPlayer(), fill.getPlayer().getItemInHand().getType(), fill.getBlockClicked().getLocation());
    }

}
