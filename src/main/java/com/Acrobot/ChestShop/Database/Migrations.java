package com.Acrobot.ChestShop.Database;

import com.Acrobot.ChestShop.ChestShop;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

/**
 * File handling the database migrations
 *
 * @author Andrzej Pomirski
 */
public class Migrations {
    public static final int CURRENT_DATABASE_VERSION = 4;

    /**
     * Migrates a database from the given version
     *
     * @param currentVersion Current version of the database
     * @return Current database version
     */
    public static int migrate(int currentVersion) {
        if (currentVersion != CURRENT_DATABASE_VERSION) {
            ChestShop.getBukkitLogger().info("Updating database...");
        } else {
            return CURRENT_DATABASE_VERSION;
        }

        switch (currentVersion) {
            case 1:
                if (migrateTo2()) {
                    currentVersion++;
                } else {
                    return -1;
                }
            case 3:
                if (migrateTo4()) {
                    currentVersion++;
                } else {
                    return -1;
                }
            case 4:
            default:
                break;
                //do nothing
        }

        return currentVersion;
    }

    private static boolean migrateTo2() {
        try {
            Dao<Account, String> accounts = DaoCreator.getDao(Account.class);

            accounts.executeRaw("ALTER TABLE `accounts` ADD COLUMN lastSeenName VARCHAR");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean migrateTo4() {
        try {
            Dao<Item, Integer> itemsOld = DaoCreator.getDao(Item.class);

            itemsOld.executeRawNoArgs("ALTER TABLE `items` RENAME TO `items-old`");

            Dao<Item, Integer> items = DaoCreator.getDaoAndCreateTable(Item.class);

            long start = System.currentTimeMillis();
            try {
                items.executeRawNoArgs("INSERT INTO `items` (id, code) SELECT id, code uuid FROM `items-old`");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ChestShop.getBukkitLogger().log(Level.INFO, "Migration of items table finished in " + (System.currentTimeMillis() - start) / 1000.0 + "s!");

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
