package edgruberman.bukkit.simpleblacklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import edgruberman.bukkit.messagemanager.MessageManager;

public class Main extends org.bukkit.plugin.java.JavaPlugin {
    
    protected static ConfigurationManager configurationManager;
    protected static MessageManager messageManager;
    
    private static Map<Integer, BlacklistEntry> blacklist = new HashMap<Integer, BlacklistEntry>();
    
    public void onLoad() {
        Main.configurationManager = new ConfigurationManager(this);
        Main.configurationManager.load();
        
        Main.messageManager = new MessageManager(this);
        Main.messageManager.log("Version " + this.getDescription().getVersion());
    }
    
    public void onEnable() {
        this.loadBlacklist();
        
        this.registerEvents();

        Main.messageManager.log("Plugin Enabled");
    }
    
    public void onDisable() {
        Main.messageManager.log("Plugin Disabled");
        Main.messageManager = null;
    }
    
    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, new PlayerListener(), Event.Priority.Normal, this);
        
        pluginManager.registerEvent(Event.Type.BLOCK_PLACE, new BlockListener(), Event.Priority.Normal, this);
    }
    
    private void loadBlacklist() {
        for (String id : this.getConfiguration().getNodes("blacklist").keySet()) {
            String message = this.getConfiguration().getString("blacklist." + id + ".message", null);
            List<String> access = this.getConfiguration().getStringList("blacklist." + id + ".allow", null);
            blacklist.put(Integer.parseInt(id), new BlacklistEntry(Integer.parseInt(id), message, access));
        }
    }
    
    protected static boolean isAllowed(Player player, Material item) {
        // For material not in the blacklist, it's allowed.
        if (!Main.blacklist.containsKey(item.getId())) return true;
        
        // For players specifically defined, it's allowed.
        if (Main.blacklist.get(item.getId()).isAllowed(player)) return true;
        
        // Otherwise, no!
        return false;
    }
    
    protected static String getMessage(int id) {
        return Main.blacklist.get(id).getMessage();
    }
}
