package kronex.fun.display.screens.clickgui.components.implement.autobuy.items.list;

import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.AutoBuyableItem;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.customitem.CustomItem;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.defaultsetpricec.Defaultpricec;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuffProvider {
    public static List<AutoBuyableItem> getBuffs() {
        List<AutoBuyableItem> buffs = new ArrayList<>();

        // Зелье Ассасина (черное взрывное зелье)
        List<Text> assassinLore = List.of(
                Text.literal("Темная сила ассасина"),
                Text.literal("Скрывает в тенях"),
                Text.literal("Усиливает удар")
        );
        buffs.add(createSplashPotion(
                "Зелье Ассасина",
                "[★] Зелье Ассасина",
                "Зелье Ассасина",
                0x000000, // Черный цвет
                Defaultpricec.getPrice("Зелье Ассасина"),
                assassinLore
        ));

        // Зелье Палладина (синее взрывное зелье)
        List<Text> paladinLore = List.of(
                Text.literal("Святая сила паладина"),
                Text.literal("Защищает от зла"),
                Text.literal("Дарует стойкость")
        );
        buffs.add(createSplashPotion(
                "Зелье Палладина",
                "[★] Зелье Палладина",
                "Зелье Палладина",
                0x0000FF, // Синий цвет
                Defaultpricec.getPrice("Зелье Палладина"),
                paladinLore
        ));

        // Зелье Гнева (красное взрывное зелье)
        List<Text> rageLore = List.of(
                Text.literal("Ярость берсерка"),
                Text.literal("Увеличивает урон"),
                Text.literal("Но ослабляет защиту")
        );
        buffs.add(createSplashPotion(
                "Зелье Гнева",
                "[★] Зелье Гнева",
                "Зелье Гнева",
                0xFF0000, // Красный цвет
                Defaultpricec.getPrice("Зелье Гнева"),
                rageLore
        ));

        // Зелье Радиации (зеленое взрывное зелье)
        List<Text> radiationLore = List.of(
                Text.literal("Токсичная радиация"),
                Text.literal("Отравляет врагов"),
                Text.literal("Наносит урон со временем")
        );
        buffs.add(createSplashPotion(
                "Зелье Радиации",
                "[★] Зелье Радиации",
                "Зелье Радиации",
                0x00FF00, // Зеленый цвет
                Defaultpricec.getPrice("Зелье Радиации"),
                radiationLore
        ));

        return buffs;
    }

    private static AutoBuyableItem createSplashPotion(String displayName, String buyName, String parseName, int color, int price, List<Text> lore) {
        NbtCompound nbt = new NbtCompound();
        nbt.putBoolean("HideFlags", true);

        // Создаем PotionContentsComponent для splash potion с нужным цветом
        PotionContentsComponent potionContents = new PotionContentsComponent(
                Optional.empty(),
                Optional.of(color),
                List.<StatusEffectInstance>of(),
                Optional.empty()
        );

        return new CustomItem(displayName, buyName, parseName, nbt, Items.SPLASH_POTION, price, potionContents, lore);
    }
}
