package edgruberman.bukkit.simpleblacklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import edgruberman.bukkit.messagemanager.MessageLevel;
import edgruberman.bukkit.messagemanager.MessageManager;

public class Main extends org.bukkit.plugin.java.JavaPlugin {
    
    private static ConfigurationManager configurationManager;
    private static MessageManager messageManager;
    
    private static Map<Integer, BlacklistEntry> blacklist = new HashMap<Integer, BlacklistEntry>();
    
    public void onLoad() {
        Main.configurationManager = new ConfigurationManager(this);
        Main.getConfigurationManager().load();
        
        Main.messageManager = new MessageManager(this);
        Main.getMessageManager().log("Version " + this.getDescription().getVersion());
    }
    
    public void onEnable() {
        this.loadBlacklist();
        
        this.registerEvents();

        Main.getMessageManager().log("Plugin Enabled");
    }
    
    public void onDisable() {
        Main.getMessageManager().log("Plugin Disabled");
    }
    
    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        
        PlayerListener playerListener = new PlayerListener();
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT_ENTITY, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_BUCKET_FILL, playerListener, Event.Priority.Normal, this);
        pluginManager.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);
        
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
    
    protected static void notify(Player player, Material material) {
        Main.getMessageManager().log(MessageLevel.RIGHTS, player.getName()
                + " attempted to use blacklisted "
                + material.toString()
        );
        
        String message = Main.blacklist.get(material.getId()).getMessage();
        if (message.length() != 0)
            Main.getMessageManager().send(player, MessageLevel.RIGHTS, message);
    }
    
    protected static ConfigurationManager getConfigurationManager() {
        return Main.configurationManager;
    }
    
    protected static MessageManager getMessageManager() {
        return Main.messageManager;
    }
}
