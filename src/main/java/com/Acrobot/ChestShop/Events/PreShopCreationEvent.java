package com.Acrobot.ChestShop.Events;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;
import rocks.survival.minecraft.network.server.survival.companies.storage.Company;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Represents a state before shop is created
 *
 * @author Acrobot
 */
public class PreShopCreationEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private Player creator;
    private Company company;
    private CreationOutcome outcome = CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
    private Sign sign;
    private SignChangeEvent event;

    public PreShopCreationEvent(Player creator, Sign sign, SignChangeEvent event) {
        this.creator = creator;
        this.sign = sign;
        this.event = event;
    }

    /**
     * Returns if event is cancelled
     *
     * @return Is event cancelled?
     */
    @Override
    public boolean isCancelled() {
        return outcome != CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
    }

    /**
     * Set if event is cancelled. This sets a generic {@link CreationOutcome#OTHER};
     *
     * @param cancel Cancel the event?
     */
    @Override
    public void setCancelled(boolean cancel) {
        if (cancel) {
            outcome = CreationOutcome.OTHER;
        } else {
            outcome = CreationOutcome.SHOP_CREATED_SUCCESSFULLY;
        }
    }

    /**
     * Returns the outcome of the event
     *
     * @return Event's outcome
     */
    public CreationOutcome getOutcome() {
        return outcome;
    }

    /**
     * Sets the event's outcome
     *
     * @param outcome Outcome
     */
    public void setOutcome(CreationOutcome outcome) {
        this.outcome = outcome;
    }

    /**
     * Sets the shop's creator
     *
     * @param creator Shop's creator
     */
    public void setCreator(Player creator) {
        this.creator = creator;
    }

    /**
     * Sets the sign attached to the shop
     *
     * @param sign Shop sign
     */
    public void setSign(Sign sign) {
        this.sign = sign;
    }

    /**
     * Sets the text on the sign
     *
     * @param signLines Text to set
     */
    public void setSignLines(List<Component> signLines) {
        for (int i = 0; i < 4; i++)
        {
            this.event.line(i, signLines.get(i));
        }
    }

    /**
     * Sets one of the lines on the sign
     *
     * @param line Line number to set (0-3)
     * @param text Text to set
     */
    public void setSignLine(int line, Component text) {
        this.event.line(line, text);
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
     * Returns the text on the sign
     *
     * @param line Line number (0-3)
     * @return Text on the sign
     */
    public Component getSignLine(int line) {
        return event.line(line);
    }

    public String getSignLineRaw(int line) {
        return ChatColor.stripColor(event.getLine(line));
    }

    /**
     * Returns the text on the sign
     *
     * @return Text on the sign
     */
    public List<Component> getSignLines() {
        return event.lines();
    }

    /**
     * Get the account of the shop owner
     *
     * @return the Account of the shop owner; null if not found
     */
    @Nullable
    public Company getCompany() {
        return company;
    }

    /**
     * Set the account of the shop owner
     *
     * @param company the Account of the shop owner
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * Possible outcomes
     */
    public static enum CreationOutcome {
        INVALID_ITEM,
        INVALID_PRICE,
        INVALID_QUANTITY,

        ITEM_AUTOFILL(false),

        UNKNOWN_COMPANY,

        SELL_PRICE_HIGHER_THAN_BUY_PRICE,
        SELL_PRICE_ABOVE_MAX,
        SELL_PRICE_BELOW_MIN,
        BUY_PRICE_ABOVE_MAX,
        BUY_PRICE_BELOW_MIN,

        NO_CHEST,

        NO_PERMISSION,
        NO_PERMISSION_FOR_TERRAIN,
        NO_PERMISSION_FOR_CHEST,
        TOO_CLOSE_TO_OTHERS,

        NOT_ENOUGH_MONEY,
        NO_OWNING_COMPANIES,
        NOT_PART_OF_COMPANY,
        CREATE_VIA_COMPANY,

        /**
         * For plugin use
         */
        OTHER(false),
        /**
         * Break the sign
         */
        OTHER_BREAK,

        SHOP_CREATED_SUCCESSFULLY(false);

        private final boolean breakSign;

        CreationOutcome() {
            this.breakSign = true;
        }

        CreationOutcome(boolean breakSign) {
            this.breakSign = breakSign;
        }

        /**
         * Get whether or not this outcome should result in the shop sign getting broken
         *
         * @return Whether or not the shop sign gets broken
         */
        public boolean shouldBreakSign() {
            return breakSign;
        }
    }
}
