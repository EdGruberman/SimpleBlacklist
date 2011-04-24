package edgruberman.bukkit.simpleblacklist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

import edgruberman.bukkit.simpleblacklist.MessageManager.MessageLevel;

public class Main extends org.bukkit.plugin.java.JavaPlugin {
    
    private final String DEFAULT_LOG_LEVEL       = "RIGHTS";
    private final String DEFAULT_SEND_LEVEL      = "RIGHTS";

    public static MessageManager messageManager = null;
    public static GroupManager groupManager = null;
    
    public Map<Integer, String> blacklist = new HashMap<Integer, String>();
    
    private List<String> allowed = new ArrayList<String>();
    private List<String> allowedOnline = new ArrayList<String>();
    
    private Map<String, List<String>> groupsConfig = new HashMap<String, List<String>>();
    
    public void onEnable() {
        Main.messageManager = new MessageManager(this);
        Main.messageManager.log("Version " + this.getDescription().getVersion());
        
        Configuration.load(this);
        
        Main.messageManager.setLogLevel(MessageLevel.parse(      this.getConfiguration().getString("logLevel",       this.DEFAULT_LOG_LEVEL)));
        Main.messageManager.setSendLevel(MessageLevel.parse(     this.getConfiguration().getString("sendLevel",      this.DEFAULT_SEND_LEVEL)));
        
        Main.groupManager = new GroupManager(this);
        this.loadGroups();
        
        this.loadBlacklist();
        this.loadAllowed();
        
        this.registerEvents();

        Main.messageManager.log("Plugin Enabled");
    }
    
    public void onDisable() {
        //TODO Unregister listeners when Bukkit supports it.
        
        Main.groupManager = null;
        
        Main.messageManager.log("Plugin Disabled");
        Main.messageManager = null;
    }
    
    private void registerEvents() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        
        PlayerListener playerListener = new PlayerListener(this);
        pluginManager.registerEvent(Event.Type.PLAYER_JOIN    , playerListener, Event.Priority.Monitor, this);
        pluginManager.registerEvent(Event.Type.PLAYER_QUIT    , playerListener, Event.Priority.Monitor, this);
        
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal , this);

        BlockListener blockListener = new BlockListener(this);
        pluginManager.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
    }
    
    public void loadBlacklist() {
        Map<Integer, String> blacklist = new HashMap<Integer, String>();
        
        for (String key : this.getConfiguration().getNodes("blacklist").keySet()) {
            blacklist.put(this.getConfiguration().getInt("blacklist." + key + ".id", -1)
                    , this.getConfiguration().getString("blacklist." + key + ".message"));
        }
        
        this.blacklist = blacklist;
    }
    
    public void loadAllowed() {
        List<String> allowed = new ArrayList<String>();

        for (String member : this.getConfiguration().getStringList("allow", null)) {
            if (member.startsWith("[") && member.endsWith("]")) {
                // Expand group name
                allowed.addAll(Main.groupManager.getMembers(member.substring(1, member.length() - 1)));
            } else {
                // Direct player name
                allowed.add(member);
            }
        }
        
        this.allowed = allowed;
        this.refreshOnline();
    }
    
    public boolean isAllowed(Player player, Material item) {
        // For material not in the blacklist, it's allowed.
        if (!this.blacklist.containsKey(item.getId())) return true;
        
        // For players specifically defined, it's allowed.
        if (this.allowedOnline.contains(player.getName())) return true;
        
        // Otherwise, no!
        return false;
    }
    
    public void refreshOnline() {
        List<String> allowedOnline = new ArrayList<String>();
        
        for (String member : this.allowed) {
            if (!allowedOnline.contains(member) && (this.getServer().getPlayer(member) != null))
                allowedOnline.add(member);
        }
        
        this.allowedOnline = allowedOnline;
    }
    
    public void addOnlinePlayer(Player player) {
        if (this.allowed.contains(player.getName()) && !this.allowedOnline.contains(player.getName()))
            this.allowedOnline.add(player.getName());
    }
    
    public void removeOnlinePlayer(Player player) {
        this.allowedOnline.remove(player.getName());
    }
    
    // TODO Overhaul group management
    private void loadGroups() {
        Map<String, List<String>> groups = new HashMap<String, List<String>>();
        List<String> members = new ArrayList<String>();
        for (String player : this.getConfiguration().getStringList("groups.Players", null)) {
            members.add(player);
        }
        groups.put("Players", members);
        this.groupsConfig = groups;
        Main.messageManager.log(MessageLevel.FINE, "[Players]=" + this.groupsGetMembers("Players"));
    }
    
    public List<String> groupsGetMembers(String group) {
        return this.groupsConfig.get(group);
    }
}