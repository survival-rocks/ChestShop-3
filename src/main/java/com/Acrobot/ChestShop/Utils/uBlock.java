package com.Acrobot.ChestShop.Utils;

import com.Acrobot.Breeze.Utils.BlockUtil;
import com.Acrobot.ChestShop.Configuration.Properties;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Chest;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.inventory.InventoryHolder;

import java.util.Optional;

/**
 * @author Acrobot
 */
public class uBlock {//
    public static final BlockFace[] CHEST_EXTENSION_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
    public static final BlockFace[] SHOP_FACES = {
            BlockFace.SELF, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH, BlockFace.UP, BlockFace.DOWN
    };
    @Deprecated
    public static final BlockFace[] NEIGHBOR_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Sign getConnectedSign(BlockState blockState) {
        return getConnectedSign(blockState.getBlock());
    }

    public static Sign getConnectedSign(Block block) {
        Sign sign = findAnyNearbyShopSign(block);

        if (sign == null) {
            Block neighbor = findNeighbor(block);
            if (neighbor != null) {
                sign = findAnyNearbyShopSign(neighbor);
            }
        }

        return sign;
    }


    public static final BlockFace[] STICKED_FACES = {BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};

    public static Optional<Sign> improvedGetSign (Block block)
    {
        for (BlockFace bf : STICKED_FACES)
        {
            Block faceBlock = block.getRelative(bf);
            if (!(faceBlock.getBlockData() instanceof WallSign) || !(faceBlock.getState() instanceof Sign sign))
                continue;

            if (!ChestShopSign.isValid(sign, false))
                continue;

            if (BlockUtil.getAttachedBlock(sign).getLocation().equals(block.getLocation()))
                return Optional.of(sign);
        }

        return Optional.ofNullable(getConnectedSign(block));
    }



    /**
     * @deprecated Use {@link #findConnectedContainer(Sign)}
     */
    @Deprecated
    public static org.bukkit.block.Chest findConnectedChest(Sign sign) {
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        return findConnectedChest(sign.getBlock(), signFace);
    }

    /**
     * @deprecated Use {@link #findConnectedContainer(Block)}
     */
    @Deprecated
    public static org.bukkit.block.Chest findConnectedChest(Block block) {
        BlockFace signFace = null;
        if (BlockUtil.isSign(block)) {
            BlockData data = block.getBlockData();
            if (data instanceof WallSign) {
                signFace = ((WallSign) data).getFacing().getOppositeFace();
            }
        }
        return findConnectedChest(block, signFace);
    }

    /**
     * @deprecated Use {@link #findConnectedContainer(Location, BlockFace)}
     */
    @Deprecated
    private static org.bukkit.block.Chest findConnectedChest(Block block, BlockFace signFace) {
        if (signFace != null) {
            Block faceBlock = block.getRelative(signFace);
            if (BlockUtil.isChest(faceBlock)) {
                return (org.bukkit.block.Chest) faceBlock.getState();
            }
        }

        for (BlockFace bf : SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = block.getRelative(bf);
                if (BlockUtil.isChest(faceBlock)) {
                    return (org.bukkit.block.Chest) faceBlock.getState();
                }
            }
        }
        return null;
    }

    public static Container findConnectedContainer(Sign sign) {
        BlockFace signFace = null;
        BlockData data = sign.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        return findConnectedContainer(sign.getLocation(), signFace);
    }

    public static Container findConnectedContainer(Block block) {
        BlockFace signFace = null;
        BlockData data = block.getBlockData();
        if (data instanceof WallSign) {
            signFace = ((WallSign) data).getFacing().getOppositeFace();
        }
        return findConnectedContainer(block.getLocation(), signFace);
    }

    private static Container findConnectedContainer(Location location, BlockFace signFace) {
        if (signFace != null) {
            Block faceBlock = location.clone().add(signFace.getModX(), signFace.getModY(), signFace.getModZ()).getBlock();
            if (uBlock.couldBeShopContainer(faceBlock)) {
                return (Container) faceBlock.getState();
            }
        }

        for (BlockFace bf : SHOP_FACES) {
            if (bf != signFace) {
                Block faceBlock = location.clone().add(bf.getModX(), bf.getModY(), bf.getModZ()).getBlock();
                if (uBlock.couldBeShopContainer(faceBlock)) {
                    return (Container) faceBlock.getState();
                }
            }
        }
        return null;
    }

    public static Sign findValidShopSign(Block block, String originalName) {
        Sign ownerShopSign = null;

        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (ChestShopSign.isValid(sign, false) && signIsAttachedToBlock(sign, block)) {
                if (!sign.getLine(0).equals(originalName)) {
                    return sign;
                } else if (ownerShopSign == null) {
                    ownerShopSign = sign;
                }
            }
        }

        return ownerShopSign;
    }

    public static Sign findAnyNearbyShopSign(Block block) {
        for (BlockFace bf : SHOP_FACES) {
            Block faceBlock = block.getRelative(bf);

            if (!BlockUtil.isSign(faceBlock)) {
                continue;
            }

            Sign sign = (Sign) faceBlock.getState();

            if (ChestShopSign.isValid(sign, false)) {
                return sign;
            }
        }
        return null;
    }

    public static org.bukkit.block.Chest findNeighbor(org.bukkit.block.Chest chest) {
        Block neighbor = findNeighbor(chest.getBlock());
        return neighbor != null ? (org.bukkit.block.Chest) neighbor.getState() : null;
    }

    public static Block findNeighbor(Block block) {
        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Chest)) {
            return null;
        }

        Chest chestData = (Chest) blockData;
        if (chestData.getType() == Chest.Type.SINGLE) {
            return null;
        }

        BlockFace chestFace = chestData.getFacing();
        // we have to rotate is to get the adjacent chest
        // west, right -> south
        // west, left -> north
        if (chestFace == BlockFace.WEST) {
            chestFace = BlockFace.NORTH;
        } else if (chestFace == BlockFace.NORTH) {
            chestFace = BlockFace.EAST;
        } else if (chestFace == BlockFace.EAST) {
            chestFace = BlockFace.SOUTH;
        } else if (chestFace == BlockFace.SOUTH) {
            chestFace = BlockFace.WEST;
        }
        if (chestData.getType() == Chest.Type.RIGHT) {
            chestFace = chestFace.getOppositeFace();
        }

        Block neighborBlock = block.getRelative(chestFace);
        if (neighborBlock.getType() == block.getType()) {
            return neighborBlock;
        }

        return null;
    }

    private static boolean signIsAttachedToBlock(Sign sign, Block block) {
        return sign.getBlock().equals(block) || BlockUtil.getAttachedBlock(sign).equals(block);
    }

    public static boolean couldBeShopContainer(Block block) {
        return block != null && Properties.SHOP_CONTAINERS.contains(block.getType());
    }

    public static boolean couldBeShopContainer(InventoryHolder holder) {
        return holder instanceof Container && couldBeShopContainer(((Container) holder).getBlock());
    }
}
