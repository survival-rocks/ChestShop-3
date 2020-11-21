package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import me.justeli.survival.companies.Companies;
import me.justeli.survival.companies.storage.Company;
import me.justeli.survival.companies.storage.ShareHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Set;

import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.CREATE_VIA_COMPANY;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NOT_PART_OF_COMPANY;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.NO_OWNING_COMPANIES;
import static com.Acrobot.ChestShop.Events.PreShopCreationEvent.CreationOutcome.UNKNOWN_COMPANY;
import static com.Acrobot.ChestShop.Signs.ChestShopSign.NAME_LINE;

/**
 * @author Acrobot
 */
public class NameChecker
        implements Listener
{

    @EventHandler (priority = EventPriority.LOW)
    public static void onPreShopCreation (PreShopCreationEvent event)
    {
        handleEvent(event);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public static void onPreShopCreationHighest (PreShopCreationEvent event)
    {
        handleEvent(event);
    }

    private static void handleEvent (PreShopCreationEvent event)
    {
        String name = event.getSignLine(NAME_LINE);
        Player player = event.getPlayer();

        Company company = event.getCompany();
        ShareHolder shareHolder = Companies.shareHolder(player.getUniqueId());

        if (shareHolder == null)
        {
            event.setOutcome(NO_OWNING_COMPANIES);
            return;
        }

        else if (company == null && name.isEmpty())
        {
            Set<Company> owns = shareHolder.getOwnedCompanies();
            company = owns == null || owns.size() == 0? null : owns.stream().findFirst().get();

            if (company == null)
            {
                company = shareHolder.getMostPrivateSharesOwned();
            }

            if (company == null)
            {
                event.setOutcome(NO_OWNING_COMPANIES);
                return;
            }
        }

        else if (company == null)
        {
            company = Companies.get(name);
            if (company == null && !name.equalsIgnoreCase(player.getName()))
            {
                event.setOutcome(UNKNOWN_COMPANY);
                return;
            }
            else if (company == null && name.equalsIgnoreCase(player.getName()))
            {
                event.setOutcome(CREATE_VIA_COMPANY);
                return;
            }
            else if (!company.getPrivateShareHolders().contains(player.getUniqueId()))
            {
                event.setOutcome(NOT_PART_OF_COMPANY);
                return;
            }
        }

        event.setCompany(company);
        event.setSignLine(NAME_LINE, company.getDisplayName());
    }
}
