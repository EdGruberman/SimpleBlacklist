package edgruberman.bukkit.simpleblacklist.messaging;

import edgruberman.bukkit.simpleblacklist.messaging.messages.Confirmation;

public interface Recipients {

    public abstract Confirmation send(Message message);

}
