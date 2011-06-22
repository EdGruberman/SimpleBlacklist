package edgruberman.bukkit.simpleblacklist;

import java.util.List;

import org.bukkit.entity.Player;

import edgruberman.bukkit.accesscontrol.SimpleAccess;

public class BlacklistEntry {
    
    private int id;
    private String message;
    private SimpleAccess access;
    
    protected BlacklistEntry(int id, String message, List<String> access) {
        this.id = id;
        this.message = message;
        this.access = new SimpleAccess(access);
    }
    
    protected int getId() {
        return this.id;
    }
    
    protected String getMessage() {
        return this.message;
    }
    
    protected boolean isAllowed(Player player) {
        return this.access.isAllowed(player);
    }
}
