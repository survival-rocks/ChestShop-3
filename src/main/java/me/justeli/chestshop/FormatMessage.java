package me.justeli.chestshop;

import org.bukkit.inventory.ItemStack;
import rocks.survival.minecraft.network.util.Text;

/**
 * Created by Eli on July 28, 2020.
 * ChestShop-3: me.justeli.chestshop
 */
public class FormatMessage
{
    public static Text transaction (String message, ItemStack item)
    {
        String[] parts = message.split(" %item ");

        if (parts.length == 1)
            return new Text();

        Text text = new Text().colored(parts[0]);
        if (item.getAmount() != 1)
            text.number(item.getAmount());

        return text.item(item).colored(parts[1]);
    }
}
