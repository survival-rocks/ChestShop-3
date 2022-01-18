package com.Acrobot.ChestShop.Listeners.PostShopCreation;

import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Events.ShopCreatedEvent;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Sign;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class SignSticker implements Listener {

    @EventHandler
    public static void onShopCreation(ShopCreatedEvent event) {
        if (!Properties.STICK_SIGNS_TO_CHESTS) {
            return;
        }

        if (ChestShopSign.isAdminShop(event.getSignLineRaw(NAME_LINE))) {
            return;
        }

        stickSign(event.getSign().getBlock(), event);
    }

    private static void stickSign(Block signBlock, ShopCreatedEvent event) {
        if (!(signBlock.getBlockData() instanceof Sign)) {
            return;
        }

        BlockFace shopBlockFace = null;

        for (BlockFace face : uBlock.CHEST_EXTENSION_FACES) {
            if (uBlock.couldBeShopContainer(signBlock.getRelative(face))) {
                shopBlockFace = face;
                break;
            }
        }

        if (shopBlockFace == null) {
            return;
        }


        signBlock.setType(Material.OAK_WALL_SIGN);

        org.bukkit.block.Sign sign = (org.bukkit.block.Sign) signBlock.getState();

        WallSign signMaterial = (WallSign) Bukkit.createBlockData(Material.OAK_WALL_SIGN);
        signMaterial.setFacing(shopBlockFace.getOppositeFace());
        sign.setBlockData(signMaterial);

        // wtf?
        for (int i = 0; i < event.getSignLines().size(); ++i) {
            sign.line(i, event.getSignLine(i));
        }

        sign.update(true);
    }
}
