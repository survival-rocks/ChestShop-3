package com.Acrobot.ChestShop.Listeners.PostTransaction;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Commands.Toggle;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Economy.Economy;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import me.justeli.api.wide.Convert;
import me.justeli.api.wide.Text;
import me.justeli.chestshop.DelayedMessage;
import me.justeli.chestshop.DelayedNotificationEvent;
import me.justeli.survival.companies.storage.Company;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.ITEM_LINE;

/**
 * @author Acrobot
 */
public class TransactionMessageSender implements Listener {
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public static void onTransaction(TransactionEvent event) {
        if (event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
            sendBuyMessage(event);
        } else {
            sendSellMessage(event);
        }
    }

    @EventHandler
    public void delayed (DelayedNotificationEvent event)
    {
        Text notify = new Text().primary("Sales from").variable(event.getCompany().getDisplayName()).primary("in the past minute:");
        List<Text> messages = new ArrayList<>();

        for (DelayedMessage message : event.getMessages())
        {
            messages.add(new Text().primary("- Sold").variable(message.getAmount()).primary("of").item(message.getItem()).primary("for")
                    .coins(message.getPricePaid()).primary("to").variable(Convert.listToSentence(message.getBuyerNames().toArray(new String[0]))));
        }

        for (Player player : event.getCompany().getOnlinePrivateShareHolders())
        {
            if (event.getMessages().size() == 0)
                continue;

            notify.chatServer(player);
            messages.forEach(message -> message.chatServer(player));
        }
    }

    // String = raw company name
    private final static HashMap<String, DelayedNotificationEvent> cachedNotification = new HashMap<>();

    protected static void sendBuyMessage(TransactionEvent event) {
        Player player = event.getClient();

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            sendMessage(player, event.getClient().getName(), Messages.YOU_BOUGHT_FROM_SHOP, event, "owner", event.getCompany().getDisplayName());
        }

        // custom part
        /*
        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getCompany()))
        {
            sendMessage(event.getCompany(), event.getCompany().getDisplayName(), Messages.SOMEBODY_BOUGHT_FROM_YOUR_SHOP, event, "buyer", player.getName());
        }
         */

        ItemStack item = event.getStock()[0].clone();
        int amount = Arrays.stream(event.getStock()).mapToInt(ItemStack::getAmount).sum();

        DelayedMessage message = new DelayedMessage(player.getName(), amount, item, event.getSign().getLine(ITEM_LINE), event.getExactPrice().longValue());

        if (!cachedNotification.containsKey(event.getCompany().getRawName()))
        {
            cachedNotification.put(event.getCompany().getRawName(), new DelayedNotificationEvent(message, event.getCompany()));

            Bukkit.getScheduler().runTaskLater(ChestShop.getPlugin(), () ->
            {
                Bukkit.getPluginManager().callEvent(cachedNotification.get(event.getCompany().getRawName()));
                cachedNotification.remove(event.getCompany().getRawName());
            }, 1200);
        }
        else
        {
            DelayedNotificationEvent delayed = cachedNotification.get(event.getCompany().getRawName());
            for (DelayedMessage existing : delayed.getMessages())
            {
                if (existing.getItemCode().equals(event.getSign().getLine(ITEM_LINE)))
                {
                    existing.addAmount(amount);
                    existing.addBuyerName(player.getName());
                    existing.addPricePaid(event.getExactPrice().longValue());
                    return;
                }
            }

            delayed.getMessages().add(message);
        }
    }
    
    protected static void sendSellMessage(TransactionEvent event) {
        Player player = event.getClient();

        if (Properties.SHOW_TRANSACTION_INFORMATION_CLIENT) {
            sendMessage(player, event.getClient().getName(), Messages.YOU_SOLD_TO_SHOP, event, "buyer", event.getCompany().getDisplayName());
        }

        if (Properties.SHOW_TRANSACTION_INFORMATION_OWNER && !Toggle.isIgnoring(event.getCompany())) {
            sendMessage(event.getCompany(), event.getCompany().getDisplayName(), Messages.SOMEBODY_SOLD_TO_YOUR_SHOP, event, "seller", player.getName());
        }
    }

    private static void sendMessage(Company company, String playerName, String rawMessage, TransactionEvent event, String... replacements) {
        for (UUID uuid : company.getPrivateShareHolders())
        {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player == null || !player.isOnline())
                continue;

            sendMessage(player.getPlayer(), playerName, rawMessage, event, replacements);
        }
    }
    
    private static void sendMessage(Player player, String playerName, String rawMessage, TransactionEvent event, String... replacements) {
        Location loc = event.getSign().getLocation();
        String message = Messages.prefix(rawMessage)
                .replace("%price", Economy.formatBalance(event.getExactPrice()))
                .replace("%world", loc.getWorld().getName())
                .replace("%x", String.valueOf(loc.getBlockX()))
                .replace("%y", String.valueOf(loc.getBlockY()))
                .replace("%z", String.valueOf(loc.getBlockZ()));
        
        for (int i = 0; i + 1 < replacements.length; i+=2) {
            message = message.replace("%" + replacements[i], replacements[i + 1]);
        }

        if (player != null) {
            if (Properties.SHOWITEM_MESSAGE && MaterialUtil.Show.sendMessage(player, message, event.getStock())) {
                return;
            }
            player.sendMessage(message.replace("%item", MaterialUtil.getItemList(event.getStock())));
        } else if (playerName != null) {
            ChestShop.sendBungeeMessage(playerName, message.replace("%item", MaterialUtil.getItemList(event.getStock())));
        }
    }

}
