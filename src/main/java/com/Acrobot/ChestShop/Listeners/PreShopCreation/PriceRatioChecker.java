package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.math.BigDecimal;

import static com.Acrobot.Breeze.Utils.PriceUtil.hasBuyPrice;
import static com.Acrobot.Breeze.Utils.PriceUtil.hasSellPrice;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.SELL_PRICE_HIGHER_THAN_BUY_PRICE;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.PRICE_LINE;
import static org.bukkit.event.EventPriority.HIGH;

/**
 * @author Acrobot
 */
public class PriceRatioChecker implements Listener {

    @EventHandler(priority = HIGH)
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String priceLine = event.getSignLine(PRICE_LINE);

        if (hasBuyPrice(priceLine, true) && hasSellPrice(priceLine, true)) {
            BigDecimal buyPrice = PriceUtil.getExactBuyPrice(priceLine, true);
            BigDecimal sellPrice = PriceUtil.getExactSellPrice(priceLine, true);
            if (sellPrice.compareTo(buyPrice) > 0) {
                event.setOutcome(SELL_PRICE_HIGHER_THAN_BUY_PRICE);
            }
        }
    }
}
