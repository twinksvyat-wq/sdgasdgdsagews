package kronex.fun.display.screens.clickgui.components.implement.panels;

import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.features.module.ModuleCategory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PanelsContainerComponent extends AbstractComponent {
    private static final float DEFAULT_PANEL_WIDTH = 120f;
    private static final float DEFAULT_PANEL_HEIGHT = 268f;
    private static final float DEFAULT_PANEL_GAP = 8f;
    private static final float TOP_PADDING = 8f;

    private final List<CategoryPanelComponent> panels = new ArrayList<>();

    public PanelsContainerComponent() {
        for (ModuleCategory category : EnumSet.of(
                ModuleCategory.COMBAT,
                ModuleCategory.MOVEMENT,
                ModuleCategory.RENDER,
                ModuleCategory.PLAYER,
                ModuleCategory.MISC)) {
            panels.add(new CategoryPanelComponent(category));
        }
    }

    private float getPanelWidth() {
        return DEFAULT_PANEL_WIDTH;
    }

    private float getPanelHeight() {
        return DEFAULT_PANEL_HEIGHT;
    }

    private float getPanelGap() {
        return DEFAULT_PANEL_GAP;
    }

    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        float panelW = getPanelWidth();
        float panelH = getPanelHeight();
        float gap = getPanelGap();

        float totalWidth = panels.size() * panelW + (panels.size() - 1) * gap;
        float startX = x + (width - totalWidth) / 2f;
        float startY = y + TOP_PADDING;

        for (int i = 0; i < panels.size(); i++) {
            CategoryPanelComponent panel = panels.get(i);
            panel.position(startX + i * (panelW + gap), startY).size(panelW, panelH);
            panel.render(context, mouseX, mouseY, delta);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CategoryPanelComponent panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (CategoryPanelComponent panel : panels) {
            if (panel.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (CategoryPanelComponent panel : panels) {
            if (panel.mouseScrolled(mouseX, mouseY, amount)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CategoryPanelComponent panel : panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (CategoryPanelComponent panel : panels) {
            if (panel.charTyped(chr, modifiers)) {
                return true;
            }
        }
        return false;
    }
}