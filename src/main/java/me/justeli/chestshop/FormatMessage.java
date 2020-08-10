package me.justeli.chestshop;

import me.justeli.api.wide.Text;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Eli on July 28, 2020.
 * ChestShop-3: me.justeli.chestshop
 */
public class FormatMessage
{
    public static TextComponent transaction (String message, ItemStack item)
    {
        String[] parts = message.split(" %item ");

        if (parts.length == 1)
            return new TextComponent(message);

        Text text = new Text().colored(parts[0]);
        if (item.getAmount() != 1)
            text.number(item.getAmount());

        return text.item(item).colored(parts[1]).toComponent();
    }
}
