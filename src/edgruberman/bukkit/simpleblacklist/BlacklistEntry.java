package edgruberman.bukkit.simpleblacklist;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Material class wrapper defining access.
 */
public class BlacklistEntry {

    private final Material material;
    private final String description;

    protected BlacklistEntry(final Material material, final String description) {
        this.material = material;
        this.description = description;
    }

    protected Material getMaterial() {
        return this.material;
    }

    protected String getDescription() {
        return this.description;
    }

    protected boolean isAllowed(final Player player) {
        return player.hasPermission(String.format(Main.PERMISSION, this.material.name()));
    }

}
