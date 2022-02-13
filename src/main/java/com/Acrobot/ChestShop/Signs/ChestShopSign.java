package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Listeners.PreShopCreation.ItemChecker;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import rocks.survival.minecraft.network.server.survival.companies.storage.Company;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class ChestShopSign {//
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    private static final Pattern SHORT_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9 !$&',\\-.µÀ-ÖØ-öø-žᴀʙᴄᴅᴇꜰɢʜɪᴊᴋʟᴍɴᴏᴘ\uA7AFʀꜱᴛᴜᴠᴡʏᴢ]{3,15}$");
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^[1-9][0-9]{0,5}$");
    private static final Pattern CREATION_PRICE_PATTERN = Pattern.compile("(?i)^([\\dbs :]+)$");
    private static final Pattern ITEM_PATTERN = Pattern.compile("^(\\?|[\\w ?#:-])+$");

    public static final String AUTOFILL_CODE = "?";

    public static boolean isValid (Sign sign, boolean creation)
    {
        return isValid(sign.lines(), creation);
    }

    public static boolean isValid (List<Component> components, boolean creation)
    {
        String companyName = PriceComponent.SERIALIZER.serialize(components.get(0));
        if (!companyName.isEmpty() && !SHORT_NAME_PATTERN.matcher(companyName).find())
            return false;

        if (!AMOUNT_PATTERN.matcher(PriceComponent.SERIALIZER.serialize(components.get(1))).find())
            return false;

        if (creation && !CREATION_PRICE_PATTERN.matcher(PriceComponent.SERIALIZER.serialize(components.get(2))).find())
            return false;

        PriceComponent.ReadPrice readPrice = PriceComponent.readPrice(components.get(2));
        if (!creation && readPrice.buy() == null && readPrice.sell() == null)
            return false;

        return ItemChecker.AUTO_FILL.equals(components.get(3)) || ITEM_PATTERN.matcher(PriceComponent.SERIALIZER.serialize(components.get(3))).find();
    }

    public static boolean isValid (Block sign)
    {
        return BlockUtil.isSign(sign) && isValid((Sign) sign.getState(), false);
    }

    /**
     * @deprecated Use {@link #isShopBlock(Block)}
     */
    @Deprecated
    public static boolean isShopChest(Block chest) {
        if (!BlockUtil.isChest(chest)) {
            return false;
        }

        return uBlock.getConnectedSign((Chest) chest.getState()) != null;
    }

    public static boolean isShopBlock(Block block) {
        if (!uBlock.couldBeShopContainer(block)) {
            return false;
        }

        return uBlock.getConnectedSign(block) != null;
    }

    /**
     * @deprecated Use {@link #isShopBlock(InventoryHolder}
     */
    @Deprecated
    public static boolean isShopChest(InventoryHolder holder) {
        if (!BlockUtil.isChest(holder)) {
            return false;
        }

        if (holder instanceof DoubleChest) {
            return isShopChest(((DoubleChest) holder).getLocation().getBlock());
        } else if (holder instanceof Chest) {
            return isShopChest(((Chest) holder).getBlock());
        } else {
            return false;
        }
    }

    public static boolean isShopBlock(InventoryHolder holder) {
        if (holder instanceof DoubleChest) {
            return isShopBlock(((DoubleChest) holder).getLeftSide())
                    || isShopBlock(((DoubleChest) holder).getRightSide());
        } else if (holder instanceof BlockState) {
            return isShopBlock(((BlockState) holder).getBlock());
        }
        return false;
    }

    public static boolean canAccess(Player player, Sign sign) {
        return hasPermission(player, Permission.OTHER_NAME_ACCESS, sign);
    }

    public static boolean hasPermission(Player player, Permission base, Sign sign) {
        if (player == null) return false;
        if (sign == null) return true;

        String name = ChatColor.stripColor(sign.getLine(NAME_LINE));
        if (name == null || name.isEmpty()) return true;

        return NameManager.canUseName(player, base, name);
    }

    public static boolean isOwner(Player player, Sign sign) {
        if (player == null || sign == null) return false;

        String name = ChatColor.stripColor(sign.getLine(NAME_LINE));
        if (name == null || name.isEmpty()) return false;

        Optional<Company> company = ChestShop.survivalMain.company(name);
        if (company.isEmpty())
            return false;

        return company.get().getPrivateShareHolders().contains(player.getUniqueId());
    }



    public static boolean isAdminShop(Inventory ownerInventory) {
        return false;
    }

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLine(NAME_LINE));
    }

}
