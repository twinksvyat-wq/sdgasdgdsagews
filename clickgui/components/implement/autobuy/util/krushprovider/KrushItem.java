package kronex.fun.display.screens.clickgui.components.implement.autobuy.util.krushprovider;

import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.AutoBuyableItem;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.settings.AutoBuyItemSettings;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.settings.AutoBuySettingsManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class KrushItem implements AutoBuyableItem {
    private final String displayName;
    private final ItemStack reference;
    private final Item material;
    private final int price;
    private final AutoBuyItemSettings settings;
    private boolean enabled;

    public KrushItem(String displayName, Item material, ItemStack reference, int price) {
        this.displayName = displayName;
        this.material = material;
        this.reference = reference;
        this.price = price;
        this.enabled = true;
        this.settings = new AutoBuyItemSettings(price, material, displayName);
        AutoBuySettingsManager.getInstance().loadSettings(displayName, this.settings);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getBuyName() {
        // Для предметов Крушителя buyName = displayName (без префиксов в названии)
        return displayName;
    }

    @Override
    public String getParseName() {
        // parseName = displayName без префиксов
        return displayName.replaceAll("^\\[.+?\\]\\s*", "");
    }

    @Override
    public ItemStack createItemStack() {
        return reference.copy();
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public AutoBuyItemSettings getSettings() {
        return settings;
    }
}


