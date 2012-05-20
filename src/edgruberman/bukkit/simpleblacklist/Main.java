package edgruberman.bukkit.simpleblacklist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    static final String PERMISSION_ALL = "simpleblacklist.all";
    static final String PERMISSION_MATERIAL = "simpleblacklist.material.%s";

    @Override
    public void onEnable() {
        if (!new File(this.getDataFolder(), "config.yml").isFile()) this.saveDefaultConfig();
        this.reloadConfig();
        this.setLoggingLevel(this.getConfig().getString("logLevel", "INFO"));

        final ConfigurationSection section = this.getConfig().getConfigurationSection("blacklist");

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

        new BlacklistGuard(this, blacklist, this.getConfig().getString("denied"));
    }

    private void setLoggingLevel(final String name) {
        Level level;
        try { level = Level.parse(name); } catch (final Exception e) {
            level = Level.INFO;
            this.getLogger().warning("Defaulting to " + level.getName() + "; Unrecognized java.util.logging.Level: " + name);
        }

        // Only set the parent handler lower if necessary, otherwise leave it alone for other configurations that have set it.
        for (final Handler h : this.getLogger().getParent().getHandlers())
            if (h.getLevel().intValue() > level.intValue()) h.setLevel(level);

        this.getLogger().setLevel(level);
        this.getLogger().config("Logging level set to: " + this.getLogger().getLevel());
    }

}
