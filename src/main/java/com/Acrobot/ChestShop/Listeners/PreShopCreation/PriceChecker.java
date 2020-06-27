package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Locale;

import static com.Acrobot.Breeze.Utils.PriceUtil.CREATE_BUY_INDICATOR;
import static com.Acrobot.Breeze.Utils.PriceUtil.CREATE_SELL_INDICATOR;
import static com.Acrobot.Breeze.Utils.PriceUtil.isPrice;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.INVALID_PRICE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;

/**
 * @author Acrobot
 */
public class PriceChecker implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String line = event.getSignLine(PRICE_LINE).toUpperCase(Locale.ROOT);
        if (Properties.PRICE_PRECISION <= 0) {
            line = line.replaceAll("\\.\\d*", ""); //remove too many decimal places
        } else {
            line = line.replaceAll("(\\.\\d{0," + Properties.PRICE_PRECISION + "})\\d*", "$1"); //remove too many decimal places
        }
        line = line.replaceAll("(\\.\\d*[1-9])0+", "$1"); //remove trailing zeroes
        line = line.replaceAll("(\\d)\\.0+(\\D|$)", "$1$2"); //remove point and zeroes from strings that only have trailing zeros

        String[] part = line.split(":");

        if (part.length > 1 && (isInvalid(part[0]) ^ isInvalid(part[1]))) {
            line = line.replace(':', ' ');
            part = new String[]{line};
        }

        if (part[0].split(" ").length > 2) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (line.indexOf('B') != line.lastIndexOf('B') || line.indexOf('S') != line.lastIndexOf('S')) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (isPrice(part[0]))
        {
            part[0] = PriceUtil.BUY_INDICATOR + part[0].trim() + PriceUtil.CURRENCY;
        }
        else if (part[0].contains("B"))
        {
            part[0] = PriceUtil.BUY_INDICATOR + PriceUtil.getCreationExact(part[0], CREATE_BUY_INDICATOR).intValue() + PriceUtil.CURRENCY;
        }
        else if (part[0].contains("S") && part.length == 1)
        {
            part[0] = PriceUtil.SELL_INDICATOR + PriceUtil.getCreationExact(part[0], CREATE_SELL_INDICATOR).intValue() + PriceUtil.CURRENCY;
        }

        if (part.length > 1)
        {
            if (isPrice(part[1]))
            {
                part[1] = PriceUtil.SELL_INDICATOR + part[1].trim() + PriceUtil.CURRENCY;
            }
            else if (part[1].contains("S"))
            {
                part[1] = PriceUtil.SELL_INDICATOR + PriceUtil.getCreationExact(part[1], CREATE_SELL_INDICATOR).intValue() + PriceUtil.CURRENCY;
            }
        }

        String priceLine = part.length > 1? part[0] + " " + ChatColor.BLACK + ": " + part[1] : part[0];
        event.setSignLine(PRICE_LINE, priceLine);

        if (priceLine.length() > 15) {
            event.setOutcome(INVALID_PRICE);
            return;
        }

        if (!PriceUtil.hasBuyPrice(priceLine, false) && !PriceUtil.hasSellPrice(priceLine, false)) {
            event.setOutcome(INVALID_PRICE);
        }
    }

    private static boolean isInvalid(String part) {
        char[] characters = {'B', 'S'};

        for (char character : characters) {
            if (part.contains(Character.toString(character))) {
                return !PriceUtil.hasPrice(part, character);
            }
        }

        return false;
    }
}
