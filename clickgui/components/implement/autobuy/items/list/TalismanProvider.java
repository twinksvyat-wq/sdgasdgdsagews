package kronex.fun.display.screens.clickgui.components.implement.autobuy.items.list;

import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.AutoBuyableItem;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.customitem.CustomItem;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.defaultsetpricec.Defaultpricec;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import java.util.ArrayList;
import java.util.List;

public class TalismanProvider {
    public static List<AutoBuyableItem> getTalismans() {
        List<AutoBuyableItem> talismans = new ArrayList<>();

        talismans.add(new CustomItem(
                "[★] Талисман Вихря",           // displayName
                "[★] Талисман Вихря",           // buyName (на аукционе с большой буквы)
                "Талисман вихря",               // parseName (для /ah search с маленькой)
                null,                           // nbt
                Items.TOTEM_OF_UNDYING,         // item
                Defaultpricec.getPrice("Талисман вихря"), // price
                null,                           // potionContents
                List.of(Text.literal("Талисман неукротимого"),
                        Text.literal("вихря и бури"))));

        talismans.add(new CustomItem(
                "[★] Талисман Демона",
                "[★] Талисман Демона",
                "Талисман демона",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман демона"),
                null,
                List.of(Text.literal("Талисман тёмного"),
                        Text.literal("демона преисподней"))));

        talismans.add(new CustomItem(
                "[★] Талисман Карателя",
                "[★] Талисман Карателя",
                "Талисман Карателя",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман Карателя"),
                null,
                List.of(Text.literal("Талисман Карателя - это"),
                        Text.literal("мощь небесного палача,"),
                        Text.literal("сокрушающего недругов"))));

        talismans.add(new CustomItem(
                "[★] Талисман Крушителя",
                "[★] Талисман Крушителя",
                "Талисман Крушителя",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман Крушителя"),
                null,
                List.of(Text.literal("Талисман Крушителя - это"),
                        Text.literal("легендарный талисман"),
                        Text.literal("несокрушимых крушителей"))));

        talismans.add(new CustomItem(
                "[★] Талисман Мрака",
                "[★] Талисман Мрака",
                "Талисман мрака",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман мрака"),
                null,
                List.of(Text.literal("Талисман непроглядного"),
                        Text.literal("ночного мрака"))));

        talismans.add(new CustomItem(
                "[★] Талисман Раздора",
                "[★] Талисман Раздора",
                "Талисман раздора",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман раздора"),
                null,
                List.of(Text.literal("Талисман вечного"),
                        Text.literal("раздора и смуты"))));

        talismans.add(new CustomItem(
                "[★] Талисман Тирана",
                "[★] Талисман Тирана",
                "Талисман тирана",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман тирана"),
                null,
                List.of(Text.literal("Талисман жестокого"),
                        Text.literal("тирана и владыки"))));

        talismans.add(new CustomItem(
                "[★] Талисман Ярости",
                "[★] Талисман Ярости",
                "Талисман ярости",
                null,
                Items.TOTEM_OF_UNDYING,
                Defaultpricec.getPrice("Талисман ярости"),
                null,
                List.of(Text.literal("Талисман неудержимой"),
                        Text.literal("боевой ярости"))));

        return talismans;
    }
}
