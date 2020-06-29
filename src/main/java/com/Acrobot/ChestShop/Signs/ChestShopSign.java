package com.Acrobot.ChestShop.Signs;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.Breeze.Utils.NumberUtil;
import com.Acrobot.Breeze.Utils.StringUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Database.Account;
import com.Acrobot.ChestShop.Events.AccountQueryEvent;
import com.Acrobot.ChestShop.Permission;
import com.Acrobot.ChestShop.UUIDs.NameManager;
import com.Acrobot.ChestShop.Utils.uBlock;
import me.justeli.survival.companies.storage.Company;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Acrobot
 */
public class ChestShopSign {//
    public static final byte NAME_LINE = 0;
    public static final byte QUANTITY_LINE = 1;
    public static final byte PRICE_LINE = 2;
    public static final byte ITEM_LINE = 3;

    public static Pattern[] getShopSignPattern(boolean creation)
    {
        return new Pattern[]
            {
                Pattern.compile("^?[\\w -.:]*$"),
                Pattern.compile("^[1-9][0-9]{0,5}$"),
                Pattern.compile(creation? "(?i)^([\\dbs :]+)$" : "(?i)^(§afree|[\\d§ac¢ :]+)$"),
                Pattern.compile("^(§l\\?|[\\w ?#:-])+$")
            };
    }
    public static final String AUTOFILL_CODE = "?";

    public static boolean isAdminShop(Inventory ownerInventory) {
        return ownerInventory instanceof AdminInventory;
    }

    public static boolean isAdminShop(String owner) {
        return owner.replace(" ", "").equalsIgnoreCase(Properties.ADMIN_SHOP_NAME.replace(" ", ""));
    }

    public static boolean isAdminShop(Sign sign) {
        return isAdminShop(sign.getLine(NAME_LINE));
    }

    public static boolean isValid(Sign sign, boolean creation) {
        return isValid(sign.getLines(), creation);
    }

    public static boolean isValid(String[] lines, boolean creation) {
        if (!creation && NumberUtil.isInteger(lines[PRICE_LINE]))
            return false;

        for (int i = 0; i < 4; i++) {
            String line = ChatColor.translateAlternateColorCodes('&', lines[i]);
            if (!getShopSignPattern(creation)[i].matcher(line).matches()) {
                return false;
            }
        }
        return !lines[NAME_LINE].isEmpty() || creation;
    }

    public static boolean isValid(Block sign) {
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

        String name = sign.getLine(NAME_LINE);
        if (name == null || name.isEmpty()) return true;

        return NameManager.canUseName(player, base, name);
    }

    public static boolean isOwner(Player player, Sign sign) {
        if (player == null || sign == null) return false;

        String name = sign.getLine(NAME_LINE);
        if (name == null || name.isEmpty()) return false;

        Company company = new Company(name);
        if (!company.exists())
            return false;

        return company.getPrivateShareHolders().contains(player.getUniqueId());
    }
}
