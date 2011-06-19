package edgruberman.bukkit.simpleblacklist;

import java.util.List;

import org.bukkit.entity.Player;

import edgruberman.bukkit.simplesecurity.AccessControlList;

public class BlacklistEntry {
    
    private int id;
    private String message;
    private AccessControlList acl;
    
    protected BlacklistEntry(int id, String message, List<String> access) {
        this.id = id;
        this.message = message;
        this.acl = new AccessControlList(access);
    }
    
    protected int getId() {
        return this.id;
    }
    
    protected String getMessage() {
        return this.message;
    }
    
    protected boolean isAllowed(Player player) {
        return this.acl.isAllowed(player);
    }
}
