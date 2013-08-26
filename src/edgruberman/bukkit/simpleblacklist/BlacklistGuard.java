package edgruberman.bukkit.simpleblacklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

final class BlacklistGuard implements Listener {

    private final Logger logger;
    private final Map<Integer, BlacklistEntry> blacklist = new HashMap<Integer, BlacklistEntry>();

    BlacklistGuard(final Logger logger, final List<BlacklistEntry> blacklist) {
        this.logger = logger;
        for (final BlacklistEntry entry : blacklist) this.blacklist.put(entry.material.getId(), entry);
    }

    private boolean isAllowed(final Player player, final Material material) {
        final BlacklistEntry entry = this.blacklist.get(material.getId());
        if (entry == null) return true;
        return entry.isAllowed(player);
    }

    private void notify(final Player player, final Material material, final Location location) {
        this.logger.log(Level.FINE, "{0} attempted to use blacklisted {1} at x:{2} y:{3} z:{4}"
                , new Object[] { player.getName(), material.toString(), location.getBlockX(), location.getBlockY(), location.getBlockZ() });
        Main.courier.send(player, "denied", this.blacklist.get(material.getId()).description);
        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
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
