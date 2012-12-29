package edgruberman.bukkit.simpleblacklist;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;

import edgruberman.bukkit.simpleblacklist.commands.Reload;
import edgruberman.bukkit.simpletemplate.messaging.ConfigurationCourier;
import edgruberman.bukkit.simpletemplate.util.CustomPlugin;

public class Main extends CustomPlugin {

    static final String PERMISSION_ALL = "simpleblacklist.override";
    static final String PERMISSION_MATERIAL = "simpleblacklist.material.%s";

    public static ConfigurationCourier courier;

    @Override
    public void onLoad() { this.putConfigMinimum("3.2.0"); }

    @Override
    public void onEnable() {
        this.reloadConfig();
        Main.courier = ConfigurationCourier.Factory.create(this).build();

        final ConfigurationSection section = this.getConfig().getConfigurationSection("blacklist");

        final List<BlacklistEntry> blacklist = new ArrayList<BlacklistEntry>();
        for (final String name : section.getKeys(false)) {
            final Material material = Material.matchMaterial(name);
            if (material == null) {
                this.getLogger().warning("Unable to match material: " + name);
                continue;
            }

            final String description = section.getString(name);
            blacklist.add(new BlacklistEntry(material, description));
        }

        Bukkit.getPluginManager().registerEvents(new BlacklistGuard(this, blacklist), this);

        this.getCommand("simpleblacklist:reload").setExecutor(new Reload(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Main.courier = null;
    }

}
