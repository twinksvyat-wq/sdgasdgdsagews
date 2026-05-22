package kronex.fun.display.screens.clickgui.components.implement.autobuy.window;

import kronex.fun.Kronex;
import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.items.AutoBuyableItem;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.settings.AutoBuyItemSettings;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.settings.AutoBuySettingsComponent;
import kronex.fun.display.screens.clickgui.components.implement.window.AbstractWindow;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.geometry.Render2D;
import kronex.fun.other.utils.display.scissor.ScissorAssist;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class AutoBuyItemSettingsWindow extends AbstractWindow {
    private final List<AutoBuySettingsComponent> components = new ArrayList<>();
    public final AutoBuyableItem item;
    private final AutoBuyItemSettings settings;

    public AutoBuyItemSettingsWindow(AutoBuyableItem item, AutoBuyItemSettings settings) {
        this.item = item;
        this.settings = settings;
        initializeComponents();
        draggable(true);
    }

    private void initializeComponents() {
        components.add(new AutoBuySettingsComponent.BuyBelowComponent(settings));
        if (settings.isCanHaveQuantity()) {
            components.add(new AutoBuySettingsComponent.MinQuantityComponent(settings));
        }
    }

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();
        ScissorAssist scissorManager = Kronex.getInstance().scissorManager;
        height = MathHelper.clamp(getComponentHeight() + 5, 0, 200);

        blur.render(ShapeProperties.create(context.getMatrices(), x, y, width, height).round(8).quality(64)
                .color(new Color(0, 0, 0, 200).getRGB())
                .build());

        rectangle.render(ShapeProperties.create(context.getMatrices(), x, y, width, height).round(8)
                .softness(2)
                .thickness(0.5f)
                .outlineColor(new Color(18, 19, 20, 225).getRGB())
                .color(
                        new Color(18, 19, 20, 175).getRGB(),
                        new Color(0, 2, 5, 175).getRGB(),
                        new Color(0, 2, 5, 175).getRGB(),
                        new Color(18, 19, 20, 175).getRGB())
                .build());

        rectangle.render(ShapeProperties.create(matrix, x, y + 22, width, 0.5f).round(8)
                .color(new Color(155, 155, 155, 55).getRGB())
                .build());

        ItemStack itemStack = item.createItemStack();
        Render2D.defaultDrawStack(context, itemStack, x + 7, y + 4, false, false, 0.8f);

        String title = item.getDisplayName();
        Fonts.getSize(15, Fonts.Type.SEMI).drawGradientString(context.getMatrices(), title,
                x + 25, y + 10, ColorAssist.getText(), new Color(165, 165, 165, 255).getRGB());

        // Close button
        boolean hoverClose = Calculate.isHovered(mouseX, mouseY, x + width - 20, y + 5, 15, 15);
        Fonts.getSize(17, Fonts.Type.ICONS).drawString(context.getMatrices(), "K", x + width - 15, y + 10, hoverClose ? Color.WHITE.getRGB() : ColorAssist.getText(0.5f));

        boolean isLimitedHeight = MathHelper.clamp(height, 0, 200) == 200;
        if (isLimitedHeight) scissorManager.push(matrix.peek().getPositionMatrix(), x, y + 23, width, height - 24);

        float offset = 0;
        int totalHeight = 0;
        for (int i = components.size() - 1; i >= 0; i--) {
            AutoBuySettingsComponent component = components.get(i);
            component.x = x;
            component.y = (float) (y + 22 + offset + (getComponentHeight() - 25 - component.height) + smoothedScroll);
            component.width = width;
            component.render(context, mouseX, mouseY, delta);
            offset -= component.height;
            totalHeight += (int) component.height;
        }

        if (isLimitedHeight) scissorManager.pop();
        int maxScroll = (int) Math.max(0, totalHeight - (height - 28));
        scroll = MathHelper.clamp(scroll, -maxScroll, 0);
        smoothedScroll = MathHelper.lerp(0.1F, smoothedScroll, scroll);

        if (isLimitedHeight) {
            float viewableHeight = height - 30;
            float scrollbarHeight = Math.max(20, (viewableHeight / totalHeight) * viewableHeight);
            float scrollPercent = (float) (-smoothedScroll / (float) maxScroll);
            float scrollbarY = y + 30 + (scrollPercent * (viewableHeight - scrollbarHeight));

            float scrollbarX = x + width - 6;
            float scrollbarWidth = 3;

            rectangle.render(ShapeProperties.create(matrix, scrollbarX, y + 30, scrollbarWidth, viewableHeight - 6)
                    .round(1)
                    .color(new Color(30, 30, 30, 100).getRGB())
                    .build());

            rectangle.render(ShapeProperties.create(matrix, scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeight - 6)
                    .round(1.5f)
                    .color(new Color(100, 100, 100, 180).getRGB())
                    .build());
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (Calculate.isHovered(mouseX, mouseY, x + width - 20, y + 5, 15, 15)) {
                startCloseAnimation();
                return true;
            }
            if (Calculate.isHovered(mouseX, mouseY, x, y, width, 19)) {
                dragging = true;
                dragX = (int) (x - mouseX);
                dragY = (int) (y - mouseY);
                return true;
            }
        }
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        return Calculate.isHovered(mouseX, mouseY, x, y, width, height) || super.isHovered(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        for (AbstractComponent abstractComponent : components) {
            abstractComponent.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        boolean scrolled = MathHelper.clamp(height, 0, 200) == 200 && Calculate.isHovered(mouseX, mouseY, x, y, width, height);
        if (scrolled) scroll += amount * 20;
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.mouseScrolled(mouseX, mouseY, amount)) return true;
        }
        return scrolled;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.keyPressed(keyCode, scanCode, modifiers)) return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.charTyped(chr, modifiers)) return true;
        }
        return super.charTyped(chr, modifiers);
    }

    public int getComponentHeight() {
        float offsetY = 0;
        for (AutoBuySettingsComponent component : components) {
            offsetY += component.height;
        }
        return (int) (offsetY + 30); // increased slightly for padding
    }
}
