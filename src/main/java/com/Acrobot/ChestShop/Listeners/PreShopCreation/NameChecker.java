package com.Acrobot.ChestShop.Listeners.PreShopCreation;

import com.Acrobot.ChestShop.ChestShop;
import com.Acrobot.ChestShop.Events.PreShopCreationEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import rocks.survival.minecraft.network.server.survival.companies.storage.Company;
import rocks.survival.minecraft.network.server.survival.companies.storage.ShareHolder;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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

    private static final HashMap<UUID, String> LATEST_COMPANY_USED = new HashMap<>();

    private static void handleEvent (PreShopCreationEvent event)
    {
        String name = event.getSignLineRaw(NAME_LINE);
        Player player = event.getPlayer();

        Company company = event.getCompany();
        ShareHolder shareHolder = ChestShop.survivalMain.shareHolder(player.getUniqueId());

        if (shareHolder == null)
        {
            event.setOutcome(NO_OWNING_COMPANIES);
            return;
        }

        else if (company == null && name.isEmpty())
        {
            if (LATEST_COMPANY_USED.containsKey(player.getUniqueId()))
            {
                Optional<Company> foundCompany = ChestShop.survivalMain.company(LATEST_COMPANY_USED.get(player.getUniqueId()));
                if (foundCompany.isPresent())
                {
                    company = foundCompany.get();
                }
            }

            Set<String> owns = shareHolder.getOwnedCompaniesRaw();
            if (company == null && owns != null && owns.size() != 0)
            {
                Optional<Company> foundCompany = ChestShop.survivalMain.company(owns.stream().findFirst().get());
                if (foundCompany.isPresent())
                {
                    company = foundCompany.get();
                }
            }

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
            company = ChestShop.survivalMain.company(name).orElse(null);
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
            else if (company == null || !company.getPrivateShareHolders().contains(player.getUniqueId()))
            {
                event.setOutcome(NOT_PART_OF_COMPANY);
                return;
            }
        }

        event.setCompany(company);
        LATEST_COMPANY_USED.put(player.getUniqueId(), company.getShortSignName());

        Component component = Component.text().append(Component.text(company.getShortSignName()).color(TextColor.color(company.getColor()))).build();
        event.setSignLine(NAME_LINE, component);
    }
}
