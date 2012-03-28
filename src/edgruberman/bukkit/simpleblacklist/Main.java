package edgruberman.bukkit.simpleblacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    final static String PERMISSION = "simpleblacklist.%s";

    private static final String MINIMUM_VERSION_CONFIG = "2.0.0";

    @Override
    public void onEnable() {
        final ConfigurationFile config = new ConfigurationFile(this);
        config.setMinVersion(Main.MINIMUM_VERSION_CONFIG);
        config.load();
        config.setLoggingLevel();

        final ConfigurationSection section = config.getConfig().getConfigurationSection("blacklist");

        final List<BlacklistEntry> blacklist = new ArrayList<BlacklistEntry>();
        for (final String name : section.getKeys(false)) {
            final Material material = Material.matchMaterial(name);
            if (material == null) {
                this.getLogger().log(Level.WARNING, "Unable to match material: " + name);
                continue;
            }

            final String description = section.getString(name);
            blacklist.add(new BlacklistEntry(material, description));
        }

        new BlacklistGuard(this, blacklist, config.getConfig().getString("denied"));
    }

}
