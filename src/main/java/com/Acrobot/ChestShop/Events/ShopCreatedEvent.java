package com.Acrobot.ChestShop.Events;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;
import rocks.survival.minecraft.network.server.survival.companies.storage.Company;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a state after shop creation
 *
 * @author Acrobot
 */
public class ShopCreatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player creator;

    private final Sign sign;
    private final SignChangeEvent signLines;
    private final Company company;
    @Nullable private final Container container;

    public ShopCreatedEvent(Player creator, Sign sign, @Nullable Container container, SignChangeEvent signLines, Company company) {
        this.creator = creator;
        this.sign = sign;
        this.container = container;
        this.signLines = signLines;
        this.company = company;
    }

    /**
     * Returns the text on the sign
     *
     * @param line Line number (0-3)
     * @return Text on the sign
     */
    public Component getSignLine(int line) {
        return signLines.line(line);
    }

    public String getSignLineRaw(int line) {
        return ChatColor.stripColor(signLines.getLine(line));
    }

    /**
     * Returns the text on the sign
     *
     * @return Text on the sign
     */
    public List<Component> getSignLines() {
        return signLines.lines();
    }

    /**
     * Returns the shop's creator
     *
     * @return Shop's creator
     */
    public Player getPlayer() {
        return creator;
    }

    /**
     * Returns the shop's sign
     *
     * @return Shop's sign
     */
    public Sign getSign() {
        return sign;
    }

    /**
     * Returns the shop's container (if applicable)
     *
     * @return Shop's container
     */
    @Nullable public Container getContainer() {
        return container;
    }

    /**
     * @deprecated Use {@link #getContainer()}
     */
    @Deprecated
    @Nullable public Chest getChest() {
        return container instanceof Chest ? (Chest) container : null;
    }

    /**
     * Get the account of the shop's owner
     *
     * @return The account of the shop's owner; null if no Account could be found
     */
    @Nullable
    public Company getCompany() {
        return company;
    }

    /**
     * Check whether or not the created shop is owned by the creator
     *
     * @return <tt>true</tt> if the owner account is the creators one (or null); <tt>false</tt> if it's not
     */
    public boolean createdByOwner() {
        return company != null && company.getPrivateShareHolders().contains(creator.getUniqueId());
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
