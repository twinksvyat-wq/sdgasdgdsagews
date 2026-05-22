package kronex.fun.display.screens.clickgui.components.implement.other;

import kronex.fun.Kronex;
import kronex.fun.display.screens.clickgui.MenuScreen;
import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.other.utils.client.managers.file.impl.ModuleFile;
import kronex.fun.other.utils.client.sound.SoundManager;
import kronex.fun.other.utils.display.font.FontRenderer;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.scissor.ScissorAssist;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import kronex.fun.other.utils.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import kronex.fun.other.utils.display.geometry.Render2D;

import java.awt.*;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class ConfigManagerComponent extends AbstractComponent {
    private static final float CONFIG_ITEM_HEIGHT = 15f;
    private static final float THEME_ITEM_HEIGHT = 15f;
    private static final float INPUT_HEIGHT = 15f;
    private static final float PADDING = 6f;
    private static final float HEADER_HEIGHT = 20f;
    private static final float SCROLL_STEP = 15f;
    private static final float EXPANDED_THEME_ITEM_HEIGHT = 14f;
    private static final float EXPANDED_THEME_GAP = 3f;

    // XOR encryption key
    private static final String XOR_KEY = "|3~rFup59$UW";

    // Configs
    private final List<String> configs = new ArrayList<>();
    private float configsScroll = 0f;
    private float smoothedConfigsScroll = 0f;

    public static boolean typing = false;
    private String inputText = "";
    private int cursorPosition = 0;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private boolean dragging = false;
    private long lastClickTime = 0;
    private float xOffset = 0;

    // Themes
    private static class ThemePreset {
        String name;
        int color;

        public ThemePreset(String name, int color) {
            this.name = name;
            this.color = color;
        }
    }

    private final List<ThemePreset> themes = new ArrayList<>();
    private final java.util.Map<String, kronex.fun.other.common.animation.Animation> themeAnimations = new java.util.HashMap<>();
    private float themesScroll = 0f;
    private float smoothedThemesScroll = 0f;
    private final ClientColorPickerComponent colorPicker = new ClientColorPickerComponent();
    private float customThemeItemY = -1f;
    private final List<String> expandedThemeNames = new ArrayList<>();

    public ConfigManagerComponent() {
        refreshConfigs();
        refreshThemes();
    }

    public void refreshConfigs() {
        configs.clear();
        File configsDir = new File(Kronex.getInstance().getClientInfoProvider().configsDir(), "configs");
        if (!configsDir.exists()) {
            configsDir.mkdirs();
        }
        File[] configFiles = configsDir.listFiles();
        if (configFiles != null) {
            for (File file : configFiles) {
                if (file.isFile() && file.getName().endsWith(".krn")) {
                    configs.add(file.getName().replace(".krn", ""));
                }
            }
        }
    }

    private void refreshThemes() {
        themes.clear();
        expandedThemeNames.clear();
        for (String themeName : ThemeManager.listThemes()) {
            int color = loadThemeColor(themeName);
            themes.add(new ThemePreset(themeName, color));
            expandedThemeNames.add(themeName);
        }
    }

    private int loadThemeColor(String themeName) {
        try {
            File themeFile = new File(ThemeManager.getThemeDir(), themeName + ".color");
            if (!themeFile.exists()) {
                return 0xFF6C9AFD;
            }

            String content = Files.readString(themeFile.toPath(), StandardCharsets.UTF_8);
            JsonObject theme = JsonParser.parseString(content).getAsJsonObject();
            JsonObject colors = theme.has("colors") ? theme.getAsJsonObject("colors") : null;
            if (colors == null) {
                return 0xFF6C9AFD;
            }

            String[] possibleKeys = { "Первый цвет", "Р СџР ВµРЎР‚Р Р†РЎвЂ№Р в„– РЎвЂ Р Р†Р ВµРЎвЂљ", "primary", "Primary Color" };
            for (String key : possibleKeys) {
                if (colors.has(key)) {
                    return colors.get(key).getAsInt() | 0xFF000000;
                }
            }
        } catch (Exception ignored) {
        }
        return 0xFF6C9AFD;
    }

    private double[] transformMouseCoords(double mouseX, double mouseY) {
        MenuScreen menu = MenuScreen.INSTANCE;
        float scale = menu.getScaleAnimation();
        float effective = 0.5f + scale / 2f;
        if (effective <= 0.01f)
            effective = 1f;
        float centerX = menu.x + menu.width / 2f;
        float centerY = menu.y + menu.height / 2f;
        double transformedX = (mouseX - centerX) / effective + centerX;
        double transformedY = (mouseY - centerY) / effective + centerY;
        return new double[] { transformedX, transformedY };
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float panelHeight = height / 2f - 3f;
        float confY = y;
        float themeY = y + panelHeight + 6f;

        renderPanelBg(context, x, confY, width, panelHeight);
        renderConfigsContent(context, x, confY, width, panelHeight, mouseX, mouseY);

        renderPanelBg(context, x, themeY, width, panelHeight);
        renderThemesContent(context, x, themeY, width, panelHeight, mouseX, mouseY);
    }

    private void renderPanelBg(DrawContext context, float px, float py, float pw, float ph) {
        Render2D.drawBackground(context, px, py, pw, ph, 6, 1, 2.25F);
    }

    private void renderConfigsContent(DrawContext context, float px, float py, float pw, float ph, int mouseX,
            int mouseY) {
        MatrixStack matrix = context.getMatrices();
        Matrix4f posMatrix = matrix.peek().getPositionMatrix();

        FontRenderer titleFont = Fonts.getSize(12, Fonts.Type.DEFAULT);
        titleFont.drawString(matrix, "Список конфигураций", px + 8f, py + 8f, Color.WHITE.getRGB());

        float contentY = py + HEADER_HEIGHT + 2f;
        float contentHeight = ph - HEADER_HEIGHT - INPUT_HEIGHT - 12f;

        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(posMatrix, px + PADDING, contentY, pw - PADDING * 2, contentHeight);

        float yOff = 0f;
        float totalH = 0f;
        float renderScroll = Math.round(smoothedConfigsScroll);

        for (String config : configs) {
            float itemY = contentY + yOff + renderScroll;
            renderConfigItem(matrix, config, px + PADDING, itemY, pw - PADDING * 2, CONFIG_ITEM_HEIGHT, mouseX, mouseY);
            yOff += CONFIG_ITEM_HEIGHT + 4f;
            totalH += CONFIG_ITEM_HEIGHT + 4f;
        }
        scissor.pop();

        float maxScroll = Math.max(0f, totalH - contentHeight);
        configsScroll = MathHelper.clamp(configsScroll, -maxScroll, 0f);
        smoothedConfigsScroll = Calculate.interpolateSmooth(2, smoothedConfigsScroll, configsScroll);

        float inputY = py + ph - INPUT_HEIGHT - PADDING;
        renderInputField(context, px + PADDING, inputY, pw - PADDING * 2, INPUT_HEIGHT, mouseX, mouseY);
    }

    private void renderConfigItem(MatrixStack matrix, String config, float itemX, float itemY, float itemW, float itemH,
            int mouseX, int mouseY) {
        boolean hovered = Calculate.isHovered(mouseX, mouseY, itemX, itemY, itemW, itemH);
        rectangle.render(ShapeProperties.create(matrix, itemX, itemY, itemW, itemH)
                .round(4f)
                .color(hovered ? new Color(40, 40, 50, 120).getRGB() : new Color(25, 25, 35, 80).getRGB())
                .build());

        FontRenderer textFont = Fonts.getSize(12, Fonts.Type.DEFAULT);
        String displayName = config;
        float maxNameWidth = itemW - 40f;
        if (textFont.getStringWidth(displayName) > maxNameWidth) {
            displayName = displayName.substring(0, Math.min(displayName.length(), 6)) + "..";
        }
        textFont.drawString(matrix, displayName, itemX + 4f, itemY + (itemH - 8f) / 2f + 2f, Color.WHITE.getRGB());

        FontRenderer smallFont = Fonts.getSize(10, Fonts.Type.DEFAULT);
        float actionX = itemX + itemW - 35f;
        boolean loadHovered = Calculate.isHovered(mouseX, mouseY, actionX, itemY + 1f, 15f, itemH - 2f);
        smallFont.drawString(matrix, "сохр", actionX, itemY + (itemH - 7f) / 2f + 2f,
                loadHovered ? 0xFF7BFF7B : Color.WHITE.getRGB());

        float deleteX = itemX + itemW - 12f;
        boolean deleteHovered = Calculate.isHovered(mouseX, mouseY, deleteX, itemY + 1f, 10f, itemH - 2f);
        smallFont.drawString(matrix, "x", deleteX, itemY + (itemH - 7f) / 2f + 2f,
                deleteHovered ? 0xFFFF7B7B : Color.WHITE.getRGB());
    }

    private void renderThemesContent(DrawContext context, float px, float py, float pw, float ph, int mouseX,
            int mouseY) {
        if (themes.isEmpty()) {
            refreshThemes();
        }

        MatrixStack matrix = context.getMatrices();
        Matrix4f posMatrix = matrix.peek().getPositionMatrix();

        FontRenderer titleFont = Fonts.getSize(12, Fonts.Type.DEFAULT);
        titleFont.drawString(matrix, "Выберите тему на ваш вкус", px + 8f, py + 8f, Color.WHITE.getRGB());

        float contentY = py + HEADER_HEIGHT + 2f;
        float contentHeight = ph - HEADER_HEIGHT - 6f;

        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(posMatrix, px + PADDING, contentY, pw - PADDING * 2, contentHeight);

        float yOff = 0f;
        float totalH = 0f;
        float renderScroll = Math.round(smoothedThemesScroll);

        int currentColor = ThemeManager.primaryColor.getColor();

        for (ThemePreset theme : themes) {
            float itemY = contentY + yOff + renderScroll;
            float itemW = pw - PADDING * 2;
            boolean selected = (theme.color | 0xFF000000) == (currentColor | 0xFF000000);

            kronex.fun.other.common.animation.Animation anim = themeAnimations.computeIfAbsent(theme.name,
                    k -> {
                        kronex.fun.other.common.animation.Animation a = new kronex.fun.other.common.animation.implement.Decelerate()
                                .setMs(150).setValue(1);
                        a.setDirection(selected ? kronex.fun.other.common.animation.Direction.FORWARDS
                                : kronex.fun.other.common.animation.Direction.BACKWARDS);
                        return a;
                    });
            anim.setDirection(selected ? kronex.fun.other.common.animation.Direction.FORWARDS
                    : kronex.fun.other.common.animation.Direction.BACKWARDS);
            float t = anim.getOutput().floatValue();

            boolean hovered = Calculate.isHovered(mouseX, mouseY, px + PADDING, itemY, itemW, THEME_ITEM_HEIGHT);
            rectangle.render(ShapeProperties.create(matrix, px + PADDING, itemY, itemW, THEME_ITEM_HEIGHT)
                    .round(4f)
                    .color(hovered ? new Color(40, 40, 50, 120).getRGB() : new Color(25, 25, 35, 80).getRGB())
                    .build());

            FontRenderer textFont = Fonts.getSize(12, Fonts.Type.DEFAULT);
            int textColor = Color.WHITE.getRGB();
            textFont.drawString(matrix, theme.name, px + PADDING + 4f, itemY + (THEME_ITEM_HEIGHT - 8f) / 2f + 2f,
                    textColor);

            float toggleW = 16f;
            float toggleH = 8f;
            float toggleX = px + PADDING + itemW - toggleW - 4f;
            float toggleY = itemY + (THEME_ITEM_HEIGHT - toggleH) / 2f;

            int toggleBg = kronex.fun.other.utils.display.color.ColorAssist.lerp(t, new Color(40, 40, 50).getRGB(),
                    theme.color & 0x7FFFFFFF);
            rectangle.render(ShapeProperties.create(matrix, toggleX, toggleY, toggleW, toggleH)
                    .round(toggleH / 2f)
                    .color(toggleBg)
                    .build());

            float knobSize = 10f;
            float knobX = toggleX + (toggleW - knobSize) * t;
            float knobY = toggleY - (knobSize - toggleH) / 2f;
            int knobColor = kronex.fun.other.utils.display.color.ColorAssist.lerp(t, new Color(80, 80, 90).getRGB(),
                    theme.color);
            rectangle.render(ShapeProperties.create(matrix, knobX, knobY, knobSize, knobSize)
                    .round(knobSize / 2f)
                    .color(knobColor)
                    .build());

            yOff += THEME_ITEM_HEIGHT + 4f;
            totalH += THEME_ITEM_HEIGHT + 4f;
        }

        // Custom Theme row
        float customY = contentY + yOff + renderScroll;
        customThemeItemY = customY;
        float customW = pw - PADDING * 2;
        boolean customHovered = Calculate.isHovered(mouseX, mouseY, px + PADDING, customY, customW, THEME_ITEM_HEIGHT);
        rectangle.render(ShapeProperties.create(matrix, px + PADDING, customY, customW, THEME_ITEM_HEIGHT)
                .round(4f)
                .color(customHovered ? new Color(40, 40, 50, 120).getRGB() : new Color(25, 25, 35, 80).getRGB())
                .build());

        FontRenderer textFont = Fonts.getSize(12, Fonts.Type.DEFAULT);
        textFont.drawString(matrix, "Своя тема", px + PADDING + 4f, customY + (THEME_ITEM_HEIGHT - 8f) / 2f + 2f,
                0xFFE0E0E0);

        FontRenderer actionFont = Fonts.getSize(10, Fonts.Type.DEFAULT);
        String actionText = colorPicker.isExpanded() ? "hide" : "pick";
        float actionTextWidth = actionFont.getStringWidth(actionText);
        float actionTextX = px + PADDING + customW - 30f - actionTextWidth;
        actionFont.drawString(matrix, actionText, actionTextX, customY + (THEME_ITEM_HEIGHT - 7f) / 2f + 2f,
                0xFFD4D6E1);

        float pickerW = 24f;
        float pickerH = 24f;
        colorPicker.position(px + PADDING + customW - pickerW, customY + (THEME_ITEM_HEIGHT - pickerH) / 2f);
        colorPicker.size(pickerW, pickerH);

        // Render it so it scales properly inside the scissor
        colorPicker.render(context, mouseX, mouseY, 0);

        totalH += THEME_ITEM_HEIGHT + 4f;

        if (colorPicker.isExpanded()) {
            float expandedY = customY + THEME_ITEM_HEIGHT + 5f;

            for (String themeName : expandedThemeNames) {
                int themeColor = loadThemeColor(themeName);
                boolean selectedTheme = themeName.equalsIgnoreCase(ThemeManager.getCurrentThemeName());
                boolean hoveredTheme = Calculate.isHovered(mouseX, mouseY, px + PADDING, expandedY, customW, EXPANDED_THEME_ITEM_HEIGHT);

                rectangle.render(ShapeProperties.create(matrix, px + PADDING, expandedY, customW, EXPANDED_THEME_ITEM_HEIGHT)
                        .round(3.5f)
                        .color(hoveredTheme ? new Color(44, 46, 58, 130).getRGB() : new Color(28, 30, 40, 95).getRGB())
                        .build());

                rectangle.render(ShapeProperties.create(matrix, px + PADDING + 4f, expandedY + 3f, 8f, 8f)
                        .round(4f)
                        .color(themeColor)
                        .build());

                Fonts.getSize(11, Fonts.Type.DEFAULT).drawString(matrix, themeName,
                        px + PADDING + 16f, expandedY + 4f, selectedTheme ? themeColor : 0xFFE5E7EF);

                expandedY += EXPANDED_THEME_ITEM_HEIGHT + EXPANDED_THEME_GAP;
                totalH += EXPANDED_THEME_ITEM_HEIGHT + EXPANDED_THEME_GAP;
            }
        }

        scissor.pop();

        float maxScroll = Math.max(0f, totalH - contentHeight);
        themesScroll = MathHelper.clamp(themesScroll, -maxScroll, 0f);
        smoothedThemesScroll = Calculate.interpolateSmooth(2, smoothedThemesScroll, themesScroll);
    }

    private void renderInputField(DrawContext context, float px, float py, float pw, float ph, int mouseX,
            int mouseY) {
        MatrixStack matrix = context.getMatrices();
        FontRenderer font = Fonts.getSize(10, Fonts.Type.DEFAULT);
        FontRenderer iconFont = Fonts.getSize(14, Fonts.Type.ICONSCATEGORY);

        updateXOffset(font, cursorPosition);

        float inputW = pw - 20f;
        float inputX = px;
        float inputY = py;
        float inputH = ph;

        rectangle.render(ShapeProperties.create(matrix, inputX, inputY, inputW, inputH)
                .round(4f)
                .color(new Color(25, 25, 35, 120).getRGB())
                .build());

        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(matrix.peek().getPositionMatrix(), inputX + 2f, inputY, inputW - 4f, inputH);

        String displayText = inputText.isEmpty() && !typing ? "Создать конфиг" : inputText;

        if (typing && selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
            int start = Math.max(0, Math.min(getStartOfSelection(), inputText.length()));
            int end = Math.max(0, Math.min(getEndOfSelection(), inputText.length()));
            if (start < end) {
                float selectionXStart = inputX + 4f - xOffset + font.getStringWidth(inputText.substring(0, start));
                float selectionXEnd = inputX + 4f - xOffset + font.getStringWidth(inputText.substring(0, end));
                float selectionWidth = selectionXEnd - selectionXStart;
                rectangle.render(
                        ShapeProperties.create(matrix, selectionXStart, inputY + (inputH / 2) - 4, selectionWidth, 8)
                                .color(0xFF5585E8).build());
            }
        }

        font.drawString(matrix, displayText, inputX + 4f - xOffset, inputY + (inputH / 2) - 2f,
                typing ? -1 : 0xFF878894);

        long currentTime = System.currentTimeMillis();
        boolean focused = typing && (currentTime % 1000 < 500);
        if (focused && (selectionStart == -1 || selectionStart == selectionEnd)) {
            float cursorX = font.getStringWidth(inputText.substring(0, cursorPosition));
            rectangle.render(
                    ShapeProperties.create(matrix, inputX + 4f - xOffset + cursorX, inputY + (inputH / 2) - 4f, 0.5f, 8)
                            .color(-1).build());
        }
        scissor.pop();

        float addX = inputX + inputW + 4f;
        boolean addHovered = Calculate.isHovered(mouseX, mouseY, addX, inputY, 16f, inputH);
        rectangle.render(ShapeProperties.create(matrix, addX, inputY, 16f, inputH)
                .round(3f)
                .color(addHovered ? new Color(50, 50, 60, 150).getRGB() : new Color(35, 35, 45, 120).getRGB())
                .build());
        float iconZWidth = iconFont.getStringWidth("z");
        iconFont.drawString(matrix, "z", addX + (16f - iconZWidth) / 2f, inputY + (inputH - 10f) / 2f + 3f,
                addHovered ? 0xFF7BFF7B : 0xFFD4D6E1);

        if (dragging) {
            double[] transformed = transformMouseCoords(mouseX, mouseY);
            cursorPosition = getCursorIndexAt(transformed[0], inputX);
            if (selectionStart == -1)
                selectionStart = cursorPosition;
            selectionEnd = cursorPosition;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float panelHeight = height / 2f - 3f;
        float confY = y;
        float themeY = y + panelHeight + 6f;

        if (Calculate.isHovered(mouseX, mouseY, x, confY, width, panelHeight)) {
            float inputY = confY + panelHeight - INPUT_HEIGHT - PADDING;
            float inputW = width - PADDING * 2 - 20f;
            if (Calculate.isHovered(mouseX, mouseY, x + PADDING, inputY, inputW, INPUT_HEIGHT) && button == 0) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 250) {
                    selectionStart = 0;
                    selectionEnd = inputText.length();
                } else {
                    typing = true;
                    dragging = true;
                    lastClickTime = currentTime;
                    double[] transformed = transformMouseCoords(mouseX, mouseY);
                    cursorPosition = getCursorIndexAt(transformed[0], x + PADDING);
                    selectionStart = cursorPosition;
                    selectionEnd = cursorPosition;
                }
                return true;
            }

            float addX = x + PADDING + inputW + 4f;
            if (Calculate.isHovered(mouseX, mouseY, addX, inputY, 16f, INPUT_HEIGHT) && button == 0) {
                saveConfig();
                return true;
            }

            float contentY = confY + HEADER_HEIGHT + 2f;
            float yOff = 0f;
            float renderScroll = Math.round(smoothedConfigsScroll);
            for (String config : configs) {
                float itemY = contentY + yOff + renderScroll;
                float itemW = width - PADDING * 2;
                if (itemY >= contentY && itemY + CONFIG_ITEM_HEIGHT <= inputY - PADDING
                        && Calculate.isHovered(mouseX, mouseY, x + PADDING, itemY, itemW, CONFIG_ITEM_HEIGHT)
                        && button == 0) {
                    float loadX = x + PADDING + itemW - 35f;
                    if (Calculate.isHovered(mouseX, mouseY, loadX, itemY + 1f, 15f, CONFIG_ITEM_HEIGHT - 2f)) {
                        loadConfig(config);
                        return true;
                    }
                    float deleteX = x + PADDING + itemW - 12f;
                    if (Calculate.isHovered(mouseX, mouseY, deleteX, itemY + 1f, 10f, CONFIG_ITEM_HEIGHT - 2f)) {
                        deleteConfig(config);
                        return true;
                    }
                    loadConfig(config); // default click
                    return true;
                }
                yOff += CONFIG_ITEM_HEIGHT + 4f;
            }
        } else {
            typing = false;
        }

        // Themes
        if (colorPicker.mouseClicked(mouseX, mouseY, button))
            return true;

        if (Calculate.isHovered(mouseX, mouseY, x, themeY, width, panelHeight)) {
            float contentY = themeY + HEADER_HEIGHT + 2f;
            float yOff = 0f;
            float renderScroll = Math.round(smoothedThemesScroll);
            for (ThemePreset theme : themes) {
                float itemY = contentY + yOff + renderScroll;
                float itemW = width - PADDING * 2;

                // Allow a tiny bit of overshoot to make clicking edges less frustrating
                if (itemY + THEME_ITEM_HEIGHT >= contentY && itemY <= themeY + panelHeight) {
                    if (Calculate.isHovered(mouseX, mouseY, x + PADDING, itemY, itemW, THEME_ITEM_HEIGHT)
                            && button == 0) {
                        ThemeManager.loadTheme(theme.name);
                        refreshThemes();
                        SoundManager.playSound(SoundManager.CATEGORY_CLICK);
                        return true;
                    }
                }
                yOff += THEME_ITEM_HEIGHT + 4f;
            }

            float customW = width - PADDING * 2;
            if (customThemeItemY + THEME_ITEM_HEIGHT >= contentY && customThemeItemY <= themeY + panelHeight
                    && Calculate.isHovered(mouseX, mouseY, x + PADDING, customThemeItemY, customW, THEME_ITEM_HEIGHT)
                    && button == 0) {
                colorPicker.toggleExpanded();
                SoundManager.playSound(SoundManager.CATEGORY_CLICK);
                return true;
            }

            if (colorPicker.isExpanded()) {
                float expandedY = customThemeItemY + THEME_ITEM_HEIGHT + 5f;
                for (String themeName : expandedThemeNames) {
                    if (Calculate.isHovered(mouseX, mouseY, x + PADDING, expandedY, customW, EXPANDED_THEME_ITEM_HEIGHT)
                            && button == 0) {
                        ThemeManager.loadTheme(themeName);
                        refreshThemes();
                        SoundManager.playSound(SoundManager.CATEGORY_CLICK);
                        return true;
                    }
                    expandedY += EXPANDED_THEME_ITEM_HEIGHT + EXPANDED_THEME_GAP;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            dragging = false;
        colorPicker.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        float panelHeight = height / 2f - 3f;
        float confY = y;
        float themeY = y + panelHeight + 6f;

        if (Calculate.isHovered(mouseX, mouseY, x, confY, width, panelHeight)) {
            configsScroll += amount * SCROLL_STEP;
            return true;
        }
        if (Calculate.isHovered(mouseX, mouseY, x, themeY, width, panelHeight)) {
            themesScroll += amount * SCROLL_STEP;
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (typing) {
            float maxTextWidth = Math.max(0, width - PADDING * 2 - 10f);
            if (Fonts.getSize(10, Fonts.Type.DEFAULT).getStringWidth(inputText) < maxTextWidth) {
                deleteSelectedText();
                inputText = inputText.substring(0, cursorPosition) + chr + inputText.substring(cursorPosition);
                cursorPosition++;
                clearSelection();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (typing) {
            if (Screen.hasControlDown()) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_A -> selectAllText();
                    case GLFW.GLFW_KEY_V -> pasteFromClipboard();
                    case GLFW.GLFW_KEY_C -> copyToClipboard();
                }
            } else {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_BACKSPACE -> handleBackspace();
                    case GLFW.GLFW_KEY_ENTER -> {
                        saveConfig();
                        typing = false;
                    }
                    case GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_RIGHT -> moveCursor(keyCode);
                }
            }
            return true;
        }
        return false;
    }

    private void saveConfig() {
        if (inputText.isEmpty())
            return;
        try {
            File configsDir = new File(Kronex.getInstance().getClientInfoProvider().configsDir(), "configs");
            if (!configsDir.exists())
                configsDir.mkdirs();
            ModuleFile moduleFile = new ModuleFile(Kronex.getInstance().getModuleRepository());

            // Get JSON data from ModuleFile
            String jsonData = moduleFile.getJsonData();

            // Encrypt: XOR + Base64
            String encrypted = encryptConfig(jsonData);

            // Save encrypted data
            File configFile = new File(configsDir, inputText + ".krn");
            Files.writeString(configFile.toPath(), encrypted, StandardCharsets.UTF_8);

            SoundManager.playSound(SoundManager.SAVED);
            inputText = "";
            cursorPosition = 0;
            clearSelection();
            refreshConfigs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadConfig(String name) {
        try {
            File configsDir = new File(Kronex.getInstance().getClientInfoProvider().configsDir(), "configs");
            File configFile = new File(configsDir, name + ".krn");
            if (configFile.exists() && configFile.length() > 0) {
                // Read encrypted data
                String encrypted = Files.readString(configFile.toPath(), StandardCharsets.UTF_8);

                // Decrypt: Base64 + XOR
                String jsonData = decryptConfig(encrypted);

                // Load from JSON
                ModuleFile moduleFile = new ModuleFile(Kronex.getInstance().getModuleRepository());
                moduleFile.loadFromJson(jsonData);

                SoundManager.playSound(SoundManager.LOADED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteConfig(String name) {
        try {
            File configsDir = new File(Kronex.getInstance().getClientInfoProvider().configsDir(), "configs");
            File configFile = new File(configsDir, name + ".krn");
            if (configFile.exists()) {
                configFile.delete();
                refreshConfigs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pasteFromClipboard() {
        String clipboardText = GLFW.glfwGetClipboardString(window.getHandle());
        if (clipboardText != null) {
            deleteSelectedText();
            replaceText(cursorPosition, cursorPosition, clipboardText);
        }
    }

    private void copyToClipboard() {
        if (hasSelection())
            GLFW.glfwSetClipboardString(window.getHandle(), getSelectedText());
    }

    private void selectAllText() {
        selectionStart = 0;
        selectionEnd = inputText.length();
        cursorPosition = inputText.length();
    }

    private void handleBackspace() {
        if (hasSelection())
            replaceText(getStartOfSelection(), getEndOfSelection(), "");
        else if (cursorPosition > 0)
            replaceText(cursorPosition - 1, cursorPosition, "");
    }

    private void moveCursor(int keyCode) {
        if (Screen.hasShiftDown()) {
            if (selectionStart == -1)
                selectionStart = cursorPosition;
        } else
            clearSelection();
        if (keyCode == GLFW.GLFW_KEY_LEFT && cursorPosition > 0)
            cursorPosition--;
        else if (keyCode == GLFW.GLFW_KEY_RIGHT && cursorPosition < inputText.length())
            cursorPosition++;
        if (Screen.hasShiftDown())
            selectionEnd = cursorPosition;
    }

    private void replaceText(int start, int end, String replacement) {
        if (start < 0)
            start = 0;
        if (end > inputText.length())
            end = inputText.length();
        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }
        inputText = inputText.substring(0, start) + replacement + inputText.substring(end);
        cursorPosition = start + replacement.length();
        clearSelection();
    }

    private boolean hasSelection() {
        return selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd;
    }

    private String getSelectedText() {
        return inputText.substring(getStartOfSelection(), getEndOfSelection());
    }

    private int getStartOfSelection() {
        return Math.min(selectionStart, selectionEnd);
    }

    private int getEndOfSelection() {
        return Math.max(selectionStart, selectionEnd);
    }

    private void clearSelection() {
        selectionStart = -1;
        selectionEnd = -1;
    }

    private int getCursorIndexAt(double mouseX, float inputX) {
        FontRenderer font = Fonts.getSize(10, Fonts.Type.DEFAULT);
        float relativeX = (float) mouseX - inputX - 4f + xOffset;
        int position = 0;
        while (position < inputText.length()) {
            float charWidth = font.getStringWidth(inputText.substring(position, position + 1));
            float textWidth = font.getStringWidth(inputText.substring(0, position));
            if (textWidth + charWidth / 2 > relativeX)
                break;
            position++;
        }
        return Math.max(0, Math.min(position, inputText.length()));
    }

    private void updateXOffset(FontRenderer font, int cursorPosition) {
        float cursorX = font.getStringWidth(inputText.substring(0, Math.min(cursorPosition, inputText.length())));
        float visibleWidth = width - PADDING * 2 - 10f;
        if (cursorX < xOffset)
            xOffset = Math.max(0, cursorX - 10);
        else if (cursorX - xOffset > visibleWidth)
            xOffset = cursorX - visibleWidth + 10;
        if (xOffset < 0)
            xOffset = 0;
    }

    private void deleteSelectedText() {
        if (hasSelection())
            replaceText(getStartOfSelection(), getEndOfSelection(), "");
    }

    /**
     * Encrypts config data using XOR + Base64
     * @param data JSON string to encrypt
     * @return Base64 encoded XOR encrypted string
     */
    private String encryptConfig(String data) {
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = XOR_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[dataBytes.length];

        for (int i = 0; i < dataBytes.length; i++) {
            encrypted[i] = (byte) (dataBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts config data using Base64 + XOR
     * @param encrypted Base64 encoded XOR encrypted string
     * @return Decrypted JSON string
     */
    private String decryptConfig(String encrypted) {
        byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
        byte[] keyBytes = XOR_KEY.getBytes(StandardCharsets.UTF_8);
        byte[] decrypted = new byte[encryptedBytes.length];

        for (int i = 0; i < encryptedBytes.length; i++) {
            decrypted[i] = (byte) (encryptedBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        return new String(decrypted, StandardCharsets.UTF_8);
    }
}
