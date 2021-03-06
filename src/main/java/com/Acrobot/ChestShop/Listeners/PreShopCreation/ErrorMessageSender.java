package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Acrobot
 */
public class ErrorMessageSender implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        if (!event.isCancelled()) {
            return;
        }

        String message = null;

        switch (event.getOutcome()) {
            case UNKNOWN_COMPANY:
                message = Messages.COMPANY_NOT_FOUND;
                break;
            case TOO_CLOSE_TO_OTHERS:
                message = Messages.TOO_CLOSE_TO_OTHERS;
                break;
            case NO_OWNING_COMPANIES:
                message = Messages.NO_OWNING_COMPANIES;
                break;
            case NOT_PART_OF_COMPANY:
                message = Messages.NOT_PART_OF_COMPANY;
                break;
            case INVALID_ITEM:
                message = Messages.INCORRECT_ITEM_ID;
                break;
            case INVALID_PRICE:
                message = Messages.INVALID_SHOP_PRICE;
                break;
            case INVALID_QUANTITY:
                message = Messages.INVALID_SHOP_QUANTITY;
                break;
            case SELL_PRICE_HIGHER_THAN_BUY_PRICE:
                message = Messages.YOU_CANNOT_CREATE_SHOP;
                break;
            case SELL_PRICE_ABOVE_MAX:
                message = Messages.SELL_PRICE_ABOVE_MAX;
                break;
            case SELL_PRICE_BELOW_MIN:
                message = Messages.SELL_PRICE_BELOW_MIN;
                break;
            case BUY_PRICE_ABOVE_MAX:
                message = Messages.BUY_PRICE_ABOVE_MAX;
                break;
            case BUY_PRICE_BELOW_MIN:
                message = Messages.BUY_PRICE_BELOW_MIN;
                break;
            case NO_CHEST:
                message = Messages.NO_CHEST_DETECTED;
                break;
            case NO_PERMISSION:
                message = Messages.NO_PERMISSION;
                break;
            case NO_PERMISSION_FOR_TERRAIN:
                message = Messages.CANNOT_CREATE_SHOP_HERE;
                break;
            case NO_PERMISSION_FOR_CHEST:
                message = Messages.CANNOT_ACCESS_THE_CHEST;
                break;
            case NOT_ENOUGH_MONEY:
                message = Messages.NOT_ENOUGH_MONEY;
                break;
            case ITEM_AUTOFILL:
                message = Messages.CLICK_TO_AUTOFILL_ITEM;
                break;
            default:
                break;
        }

        if (message != null) {
            event.getPlayer().sendMessage(Messages.prefix(message));
        }
    }
}
