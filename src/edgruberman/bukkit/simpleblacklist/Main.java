package edgruberman.bukkit.simpleblacklist;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import edgruberman.bukkit.simpleblacklist.commands.Reload;
import edgruberman.bukkit.simpleblacklist.messaging.Courier.ConfigurationCourier;
import edgruberman.bukkit.simpleblacklist.util.CustomPlugin;

public class Main extends CustomPlugin {

    static final String PERMISSION_ALL = "simpleblacklist.override";
    static final String PERMISSION_MATERIAL = "simpleblacklist.material.{0}";

    public static ConfigurationCourier courier;

    @Override
    public void onLoad() { this.putConfigMinimum("3.4.0"); }

    @Override
    public void onEnable() {
        this.reloadConfig();
        Main.courier = ConfigurationCourier.Factory.create(this).build();

        // load blacklist entries
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

            // set default for each blacklisted material to false to avoid ops being allowed by default
            final String specific = MessageFormat.format(Main.PERMISSION_MATERIAL, material.name());
            final Permission permission = new Permission(specific, PermissionDefault.FALSE);
            this.getServer().getPluginManager().addPermission(permission);
        }

        // register blacklist guard
        final BlacklistGuard guard = new BlacklistGuard(this.getLogger(), blacklist);
        Bukkit.getPluginManager().registerEvents(guard, this);

        // commands
        this.getCommand("simpleblacklist:reload").setExecutor(new Reload(this));
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        Main.courier = null;
    }

}
