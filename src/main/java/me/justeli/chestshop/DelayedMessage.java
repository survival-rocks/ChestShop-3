package me.justeli.chestshop;

import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Eli on November 20, 2020.
 * chestshop: me.justeli.chestshop
 */
public class DelayedMessage
{
    private final Set<String> buyerNames = new HashSet<>();
    private int amount;
    private final ItemStack item;
    private final String itemCode;
    private long pricePaid;

    public DelayedMessage (String buyerNames, int amount, ItemStack item, String itemCode, long pricePaid)
    {
        this.buyerNames.add(buyerNames);
        this.amount = amount;
        this.item = item;
        this.itemCode = itemCode;
        this.pricePaid = pricePaid;
    }

    public Set<String> getBuyerNames ()
    {
        return buyerNames;
    }

    public int getAmount ()
    {
        return amount;
    }

    public String getItemCode ()
    {
        return itemCode;
    }

    public ItemStack getItem ()
    {
        return item;
    }

    public long getPricePaid ()
    {
        return pricePaid;
    }

    public void addBuyerName (String buyerName)
    {
        this.buyerNames.add(buyerName);
    }

    public void addAmount (int amount)
    {
        this.amount += amount;
    }

    public void addPricePaid (long pricePaid)
    {
        this.pricePaid += pricePaid;
    }
}
