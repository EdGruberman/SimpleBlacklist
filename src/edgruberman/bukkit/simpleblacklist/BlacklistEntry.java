package edgruberman.bukkit.simpleblacklist;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Material class wrapper defining access
 */
public class BlacklistEntry {

    final Material material;
    final String description;

    protected BlacklistEntry(final Material material, final String description) {
        this.material = material;
        this.description = description;
    }

    protected boolean isAllowed(final Player player) {
        return player.hasPermission(String.format(Main.PERMISSION_MATERIAL, this.material.name())) || player.hasPermission(Main.PERMISSION_ALL);
    }

}
