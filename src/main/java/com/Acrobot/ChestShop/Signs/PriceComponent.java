/*
 * Copyright (c) 2022, survival.rocks - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 */

package com.Acrobot.ChestShop.Signs;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/** by Eli on January 14, 2022 **/
public class PriceComponent
{
    public static final PlainTextComponentSerializer SERIALIZER = PlainTextComponentSerializer.plainText();

    public static final TextColor BUY_COLOR = TextColor.color(0x519D51);
    public static final TextColor SELL_COLOR = TextColor.color(0xBD4D45);
    public static final TextComponent DOT = Component.text(" • ");

    public static Component createPrice (Integer buy, Integer sell)
    {
        if (buy == null && sell == null)
            return null;

        TextComponent.Builder builder = Component.text();

        if (buy != null)
        {
            Component part = Component.text().append(Component.text(buy <= 0? "Free" : withSeparators(buy) + "¢")).color(BUY_COLOR).build();
            builder.append(part);
        }

        if (sell != null && buy != null)
        {
            builder.append(DOT);
        }

        if (sell != null)
        {
            String price = sell <= 0? "0" : withSeparators(sell);
            Component part = Component.text().append(Component.text(price + "¢")).decorate(TextDecoration.ITALIC).color(SELL_COLOR).build();
            builder.append(part);
        }

        return builder.build();
    }

    public static record ReadPrice (Integer buy, Integer sell){}

    private static final GsonComponentSerializer GSON = GsonComponentSerializer.gson();

    public static ReadPrice readPrice (Component priceLine)
    {
        Integer buy = null;
        Integer sell = null;

        for (Component component : getComponentList(priceLine))
        {
            if (BUY_COLOR.equals(component.color()))
            {
                buy = parsePrice(component);
            }
            else if (SELL_COLOR.equals(component.color()))
            {
                sell = parsePrice(component);
            }
        }

        return new ReadPrice(buy, sell);
    }

    private static final DecimalFormat DECIMAL_FORMATTER_INTEGER = new DecimalFormat("#,###");

    public static String withSeparators (int number)
    {
        return DECIMAL_FORMATTER_INTEGER.format(number);
    }

    public static Integer parseInt (@Nullable String string)
    {
        if (string == null)
            return null;

        try
        {
            return Integer.parseInt(string);
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    private static List<Component> getComponentList (Component signLine)
    {
        List<Component> list = new ArrayList<>();
        append(list, signLine);

        return list;
    }

    private static void append (List<Component> components, Component component)
    {
        components.add(component);
        for (Component child : component.children())
        {
            append(components, child);
        }
    }

    private static Integer parsePrice (Component component)
    {
        String serialized = SERIALIZER.serialize(component).replaceAll("[,¢]", "");

        if (serialized.equalsIgnoreCase("free"))
            return 0;

        try
        {
            return Integer.parseInt(serialized);
        }
        catch (Exception exception)
        {
            return null;
        }
    }
}
