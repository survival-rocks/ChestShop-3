package com.Acrobot.ChestShop.Listeners.Block;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Messages;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import me.justeli.survival.companies.handlers.DistanceModule;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import static com.Acrobot.ChestShop.Permission.OTHER_NAME_DESTROY;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

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
        String[] lines = event.getLines();

        if (ChestShopSign.isValid(lines, false))
        {
            signBlock.breakNaturally();
            event.getPlayer().sendMessage(Messages.prefix(Messages.INVALID_SHOP_PRICE));
            return;
        }

        if (!ChestShopSign.isValid(lines, true)) {
            return;
        }

        PreShopCreationEvent preEvent = new PreShopCreationEvent(event.getPlayer(), sign, lines);
        ChestShop.callEvent(preEvent);

        if (preEvent.getOutcome().shouldBreakSign()) {
            signBlock.breakNaturally();
            return;
        }

        for (byte i = 0; i < preEvent.getSignLines().length && i < 4; ++i) {
            event.setLine(i, preEvent.getSignLine(i));
        }

        if (preEvent.isCancelled()) {
            return;
        }

        ShopCreatedEvent postEvent = new ShopCreatedEvent(preEvent.getPlayer(), preEvent.getSign(), uBlock.findConnectedContainer(preEvent.getSign()), preEvent.getSignLines(), preEvent.getCompany());
        ChestShop.callEvent(postEvent);
    }
}
