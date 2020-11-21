package me.justeli.chestshop;

import me.justeli.survival.companies.storage.Company;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eli on November 19, 2020.
 * chestshop: me.justeli.chestshop
 */
public class DelayedNotificationEvent
        extends Event
{
    private static final HandlerList handlers = new HandlerList();

    private final Set<DelayedMessage> messages = new HashSet<>();
    private final Company company;

    public DelayedNotificationEvent (DelayedMessage message, Company company)
    {
        this.messages.add(message);
        this.company = company;
    }

    public Set<DelayedMessage> getMessages ()
    {
        return messages;
    }

    public Company getCompany ()
    {
        return company;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
