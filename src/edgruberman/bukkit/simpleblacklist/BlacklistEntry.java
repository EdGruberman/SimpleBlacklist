package edgruberman.bukkit.simpleblacklist;

import java.text.MessageFormat;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Material class wrapper defining access
 */
final class BlacklistEntry {

    final Material material;
    final String description;

    BlacklistEntry(final Material material, final String description) {
        this.material = material;
        this.description = description;
    }

    boolean isAllowed(final Player player) {
        final String specific = MessageFormat.format(Main.PERMISSION_MATERIAL, this.material.name());
        return player.hasPermission(specific) || player.hasPermission(Main.PERMISSION_ALL);
    }

}
