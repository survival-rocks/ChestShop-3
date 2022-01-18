package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.DyeColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * @author Acrobot
 */
public class SignCreate implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public static void onSignChange(SignChangeEvent event) {
        Block signBlock = event.getBlock();

        if (!BlockUtil.isSign(signBlock)) {
            return;
        }

        Sign sign = (Sign) signBlock.getState();

        if (ChestShopSign.isValid(event.lines(), false))
        {
            signBlock.breakNaturally();
            event.getPlayer().sendMessage(Messages.prefix(Messages.INVALID_SHOP_PRICE));
            return;
        }

        if (!ChestShopSign.isValid(event.lines(), true)) {
            return;
        }

        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), sign, event);
        ChestShop.callEvent(preEvent);

        if (preEvent.getOutcome().shouldBreakSign()) {
            signBlock.breakNaturally();
            return;
        }

        // already done in the event
//        for (byte i = 0; i < preEvent.getSignLines().length && i < 4; ++i) {
//            event.setLine(i, preEvent.getSignLine(i));
//        }

        if (preEvent.isCancelled()) {
            return;
        }

        sign.setGlowingText(true);
        sign.setColor(DyeColor.GRAY);
        sign.update();

        ShopCreatedEvent postEvent = new ShopCreatedEvent(
                preEvent.getPlayer(),
                preEvent.getSign(),
                uBlock.findConnectedContainer(preEvent.getSign()),
                event,
                preEvent.getCompany()
        );
        ChestShop.callEvent(postEvent);
    }
}
