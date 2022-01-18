package com.Acrobot.Breeze.Utils;

import com.Acrobot.ChestShop.Signs.PriceComponent;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * @author Acrobot
 */
public class PriceUtil {
    public static final BigDecimal NO_PRICE = BigDecimal.valueOf(-1);
    public static final BigDecimal FREE = BigDecimal.valueOf(0);
    public static final BigDecimal MAX = BigDecimal.valueOf(Double.MAX_VALUE);

    public static final String FREE_TEXT = "Free";

    public static final char CREATE_BUY_INDICATOR = 'b';
    public static final char CREATE_SELL_INDICATOR = 's';

    /**
     * Gets the exact price from the text
     *
     * @param text      Text to check
     * @param indicator Price indicator (for example, B for buy)
     * @return exact price
     */
    public static BigDecimal getCreationExact(String text, char indicator) {
        String[] split = text.replace(" ", "").toLowerCase(Locale.ROOT).split(":");
        String character = String.valueOf(indicator).toLowerCase(Locale.ROOT);

        return getExactDifference(split, character);
    }

//    public static BigDecimal getTransactionExact(String text, String indicator) {
//        String[] split = text.replace(" ", "").replace(CURRENCY, "").toLowerCase().split(ChatColor.BLACK + ":");
//        String character = indicator.toLowerCase(Locale.ROOT);
//
//        return getExactDifference(split, character);
//    }

    private static BigDecimal getExactDifference (String[] split, String character)
    {
        for (String part : split) {
            if (!part.startsWith(character) && !part.endsWith(character)) {
                continue;
            }

            part = part.replace(character, "");

            if (part.equalsIgnoreCase(FREE_TEXT)) {
                return FREE;
            }

            try {
                BigDecimal price = new BigDecimal(part);

                if (price.compareTo(MAX) > 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                    return NO_PRICE;
                } else {
                    return price;
                }
            } catch (NumberFormatException ignored) {}
        }

        return NO_PRICE;
    }

    /**
     * Gets the exact buy price from the text
     *
     * @param text Text to check
     * @return Exact buy price
     */
    public static BigDecimal getExactBuyPrice(Component text) {
        PriceComponent.ReadPrice readPrice = PriceComponent.readPrice(text);
        return readPrice.buy() == null? NO_PRICE : BigDecimal.valueOf(readPrice.buy());
    }

    public static BigDecimal getExactBuyPriceCreation(String text) {
        return getCreationExact(text, CREATE_BUY_INDICATOR);
    }

    /**
     * Gets the exact sell price from the text
     *
     * @param text Text to check
     * @return Exact sell price
     */
    public static BigDecimal getExactSellPrice(Component text) {
        PriceComponent.ReadPrice readPrice = PriceComponent.readPrice(text);
        return readPrice.sell() == null? NO_PRICE : BigDecimal.valueOf(readPrice.sell());
    }

    public static BigDecimal getExactSellPriceCreation(String text) {
        return getCreationExact(text, CREATE_SELL_INDICATOR);
    }

    /**
     * Tells if there is a buy price
     *
     * @param text Price text
     * @return If there is a buy price
     */
    public static boolean hasBuyPrice(Component text) {
        return PriceComponent.readPrice(text).buy() != null;
    }

    public static boolean hasBuyPriceCreation(String text)
    {
        return hasPrice(text, CREATE_BUY_INDICATOR);
    }

    /**
     * Tells if there is a sell price
     *
     * @param text Price text
     * @return If there is a sell price
     */
    public static boolean hasSellPrice(Component text) {
        return PriceComponent.readPrice(text).sell() != null;
    }

    public static boolean hasSellPriceCreation(String text)
    {
        return hasPrice(text, CREATE_SELL_INDICATOR);
    }

    /**
     * Tells if there is a price with the specified indicator
     *
     * @param text      Price text
     * @param indicator Price indicator
     * @return If the text contains indicated price
     */
    public static boolean hasPrice(String text, char indicator) {
        return getCreationExact(text, indicator).compareTo(NO_PRICE) != 0;
    }

//    public static boolean hasPrice (String text, String indicator)
//    {
//        return getTransactionExact(text, indicator).compareTo(NO_PRICE) != 0;
//    }

    /**
     * Checks if the string is a valid price
     *
     * @param text Text to check
     * @return Is the string a valid price
     */
    public static boolean isPrice(String text) {
        if (NumberUtil.isDouble(text)) {
            return true;
        }

        return text.trim().equalsIgnoreCase(FREE_TEXT);
    }
}
