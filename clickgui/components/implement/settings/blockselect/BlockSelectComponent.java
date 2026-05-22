package kronex.fun.display.screens.clickgui.components.implement.settings.blockselect;

import com.mojang.blaze3d.systems.RenderSystem;
import kronex.fun.Kronex;
import kronex.fun.features.impl.render.Interface;
import kronex.fun.other.utils.display.other.animation.implement.DecelerateAnimation;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import kronex.fun.other.utils.display.other.animation.Animation;
import kronex.fun.other.utils.display.other.animation.Direction;
import kronex.fun.other.common.animation.implement.Decelerate;
import kronex.fun.other.utils.display.font.FontRenderer;
import kronex.fun.display.screens.clickgui.components.implement.settings.AbstractSettingComponent;
import kronex.fun.other.utils.display.scissor.ScissorAssist;
import kronex.fun.features.module.setting.implement.BlockSelectSetting;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.common.localization.Localization;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static kronex.fun.other.utils.display.font.Fonts.Type.*;

public class BlockSelectComponent extends AbstractSettingComponent {
    private final BlockSelectSetting setting;
    private final Map<String, Animation> blockAnimations = new HashMap<>();
    private final Map<String, Animation> blockHoverAnimations = new HashMap<>();

    private float blocksStartX;
    private float blocksStartY;
    private float blocksMaxW;
    private float blocksEndY;

    private boolean searchFocused = false;
    private long lastClickTime = 0;

    public BlockSelectComponent(BlockSelectSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrices = context.getMatrices();

        float headerH = 12f;
        float searchH = 16f;

        // Render setting name
        float nameX = x + 8;
        float nameY = y + 9f;
        float maxNameW = Math.max(0, (x + width) - 6f - nameX);
        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(matrices.peek().getPositionMatrix(), nameX, y + 2f, maxNameW, headerH);
        Fonts.getSize(13, DEFAULT).drawStringWithScroll(matrices, Localization.get(setting.getName()), nameX, nameY,
                maxNameW, Color.WHITE.getRGB());
        scissor.pop();

        // Render search field
        float searchX = x + 8f;
        float searchY = y + headerH + 4f;
        float searchW = width - 16f;
        renderSearchField(context, searchX, searchY, searchW, searchH, mouseX, mouseY);

        // Render blocks
        blocksStartX = x + 8f;
        blocksStartY = searchY + searchH + 4f;
        blocksMaxW = Math.max(40f, width - 16f);

        renderBlocks(context, mouseX, mouseY);
        height = (int) Math.max(headerH + searchH + 12f, (blocksEndY - y) + 4f);
    }

    private void renderSearchField(DrawContext context, float x, float y, float w, float h, int mouseX, int mouseY) {
        MatrixStack matrices = context.getMatrices();
        FontRenderer font = Fonts.getSize(12, DEFAULT);
        FontRenderer iconFont = Fonts.getSize(14, HUD2);

        int bgColor = searchFocused ? new Color(50, 50, 55, 180).getRGB() : new Color(40, 40, 45, 150).getRGB();
        int outlineColor = searchFocused ? new Color(100, 100, 110, 200).getRGB() : new Color(60, 60, 65, 180).getRGB();

        rectangle.render(ShapeProperties.create(matrices, x, y, w, h)
                .round(3)
                .thickness(1f)
                .outlineColor(outlineColor)
                .color(bgColor)
                .build());

        String displayText = setting.getSearchQuery().isEmpty() ? "Search blocks..." : setting.getSearchQuery();
        int textColor = setting.getSearchQuery().isEmpty() ? new Color(150, 150, 150, 200).getRGB() : Color.WHITE.getRGB();

        String icon = "g";
        float iconW = iconFont.getStringWidth(icon);
        float iconX = x + w - 5f - iconW;
        float iconY = y + (h - iconFont.getStringHeight(icon)) / 2f + 1f;

        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(matrices.peek().getPositionMatrix(), x + 4, y, w - 8 - (iconW + 6f), h);
        font.drawString(matrices, displayText, x + 4, y + (h - font.getStringHeight(displayText)) / 2f + 1, textColor);
        scissor.pop();

        iconFont.drawString(matrices, icon, iconX, iconY, textColor);
    }

    private void renderBlocks(DrawContext context, int mouseX, int mouseY) {
        MatrixStack matrices = context.getMatrices();
        FontRenderer font = Fonts.getSize(11, DEFAULT);
        List<BlockSelectSetting.BlockEntry> blocks = setting.getFilteredBlocks();

        float blockSize = 24f;
        float gap = 4f;
        float textH = 10f;
        float totalBlockH = blockSize + textH + 2f;

        float cx = blocksStartX;
        float cy = blocksStartY;
        float maxX = blocksStartX + blocksMaxW;

        int clientColor = Interface.getInstance().colorSetting.getColor() | 0xFF000000;

        int maxRender = 50; // Limit rendering for performance
        int renderCount = Math.min(blocks.size(), maxRender);
        int columns = Math.max(1, (int) Math.floor((blocksMaxW + gap) / (blockSize + gap)));
        int rows = renderCount == 0 ? 0 : (int) Math.ceil(renderCount / (float) columns);
        float contentH = rows == 0 ? totalBlockH : (rows * totalBlockH) + Math.max(0, rows - 1) * gap;
        float clipH = Math.min(112f, contentH);

        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(matrices.peek().getPositionMatrix(), x, blocksStartY, width, clipH);

        int rendered = 0;

        for (BlockSelectSetting.BlockEntry entry : blocks) {
            if (rendered >= maxRender) break;

            if (cx + blockSize > maxX) {
                cx = blocksStartX;
                cy += totalBlockH + gap;
            }

            boolean hovered = Calculate.isHovered(mouseX, mouseY, cx, cy, blockSize, totalBlockH);
            Animation hoverAnim = blockHoverAnimations.computeIfAbsent(entry.registryName,
                    k -> new DecelerateAnimation().setMs(120).setValue(1));
            hoverAnim.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);
            float hoverT = Math.max(0f, Math.min(1f, hoverAnim.getOutput().floatValue()));

            boolean selected = setting.isSelected(entry.registryName);
            Animation anim = blockAnimations.computeIfAbsent(entry.registryName,
                    k -> new DecelerateAnimation().setMs(140).setValue(1));
            anim.setDirection(selected ? Direction.FORWARDS : Direction.BACKWARDS);
            float t = Math.max(0f, Math.min(1f, anim.getOutput().floatValue()));

            // Background colors
            int bgOff = new Color(35, 35, 40, 120).getRGB();
            int bgOn = ColorAssist.multAlpha(clientColor, 0.5f);
            int outlineOff = new Color(55, 52, 55, 180).getRGB();
            int outlineOn = ColorAssist.multAlpha(clientColor, 0.9f);

            int bg = ColorAssist.lerp(t, bgOff, bgOn);
            int outline = ColorAssist.lerp(t, outlineOff, outlineOn);

            int bgHoverTint = (ColorAssist.alpha(bg) << 24) | 0xFFFFFF;
            int outlineHoverTint = (ColorAssist.alpha(outline) << 24) | 0xFFFFFF;
            bg = ColorAssist.lerp(hoverT * 0.15f, bg, bgHoverTint);
            outline = ColorAssist.lerp(hoverT * 0.3f, outline, outlineHoverTint);

            // Render block background
            rectangle.render(ShapeProperties.create(matrices, cx, cy, blockSize, blockSize)
                    .round(3)
                    .thickness(1f)
                    .outlineColor(outline)
                    .color(bg)
                    .build());

            // Render block item
            try {
                ItemStack stack = new ItemStack(entry.block.asItem());
                if (!stack.isEmpty()) {
                    matrices.push();
                    matrices.translate(cx + 4, cy + 4, 0);
                    matrices.scale(1.0f, 1.0f, 1.0f);
                    DiffuseLighting.enableGuiDepthLighting();
                    context.drawItem(stack, 0, 0);
                    DiffuseLighting.disableGuiDepthLighting();
                    matrices.pop();
                }
            } catch (Exception ignored) {
            }

            // Render block name
            String displayName = entry.displayName;
            float textW = font.getStringWidth(displayName);
            if (textW > blockSize) {
                // Trim text manually
                int maxChars = displayName.length();
                while (maxChars > 0 && font.getStringWidth(displayName.substring(0, maxChars) + "...") > blockSize) {
                    maxChars--;
                }
                displayName = displayName.substring(0, Math.max(1, maxChars)) + "...";
            }
            float textX = cx + (blockSize - font.getStringWidth(displayName)) / 2f;
            float textY = cy + blockSize + 2f;
            font.drawString(matrices, displayName, textX, textY, new Color(200, 200, 200, 255).getRGB());

            cx += blockSize + gap;
            rendered++;
        }

        scissor.pop();
        blocksEndY = blocksStartY + clipH;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            // Check search field click
            float searchX = x + 8f;
            float searchY = y + 12f + 4f;
            float searchW = width - 16f;
            float searchH = 16f;

            if (Calculate.isHovered(mouseX, mouseY, searchX, searchY, searchW, searchH)) {
                searchFocused = true;
                return true;
            } else {
                searchFocused = false;
            }

            // Check block click
            handleBlockClick(mouseX, mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (searchFocused && chr >= 32) {
            setting.setSearchQuery(setting.getSearchQuery() + chr);
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (searchFocused) {
            if (keyCode == 259) { // Backspace
                String query = setting.getSearchQuery();
                if (!query.isEmpty()) {
                    setting.setSearchQuery(query.substring(0, query.length() - 1));
                }
                return true;
            } else if (keyCode == 256) { // Escape
                searchFocused = false;
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void handleBlockClick(double mouseX, double mouseY) {
        List<BlockSelectSetting.BlockEntry> blocks = setting.getFilteredBlocks();

        float blockSize = 24f;
        float gap = 4f;
        float textH = 10f;
        float totalBlockH = blockSize + textH + 2f;

        float cx = blocksStartX;
        float cy = blocksStartY;
        float maxX = blocksStartX + blocksMaxW;

        int rendered = 0;
        int maxRender = 50;

        for (BlockSelectSetting.BlockEntry entry : blocks) {
            if (rendered >= maxRender) break;

            if (cx + blockSize > maxX) {
                cx = blocksStartX;
                cy += totalBlockH + gap;
            }

            if (Calculate.isHovered(mouseX, mouseY, cx, cy, blockSize, totalBlockH)) {
                setting.toggle(entry.registryName);
                return;
            }

            cx += blockSize + gap;
            rendered++;
        }
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        return Calculate.isHovered(mouseX, mouseY, x, y, width, height);
    }
}
