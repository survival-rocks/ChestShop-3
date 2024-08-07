package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import com.Acrobot.ChestShop.Events.Protection.BuildPermissionEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.Security;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NO_PERMISSION_FOR_TERRAIN;
import static com.Acrobot.ChestShop.Permission.ADMIN;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class TerrainChecker implements Listener {

    @EventHandler
    public static void onPreShopCreation(PreShopCreationEvent event) {
        String nameLine = event.getSignLineRaw(NAME_LINE);

        if (ChestShopSign.isAdminShop(nameLine)) {
            return;
        }

        Player player = event.getPlayer();

        if (Permission.has(player, ADMIN)) {
            return;
        }

        if (!Security.canPlaceSign(player, event.getSign())) {
            event.setOutcome(NO_PERMISSION_FOR_TERRAIN);
            return;
        }

        Container connectedContainer = uBlock.findConnectedContainer(event.getSign().getBlock());
        Location containerLocation = (connectedContainer != null ? connectedContainer.getLocation() : null);

        BuildPermissionEvent bEvent = new BuildPermissionEvent(player, containerLocation, event.getSign().getLocation());
        ChestShop.callEvent(bEvent);

        if (!bEvent.isAllowed()) {
            event.setOutcome(NO_PERMISSION_FOR_TERRAIN);
        }

    }
}
