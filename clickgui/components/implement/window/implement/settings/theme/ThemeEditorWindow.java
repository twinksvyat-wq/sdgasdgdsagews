package kronex.fun.display.screens.clickgui.components.implement.window.implement.settings.theme;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kronex.fun.Kronex;
import kronex.fun.display.screens.clickgui.MenuScreen;
import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.display.screens.clickgui.components.implement.window.AbstractWindow;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.display.font.FontRenderer;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import kronex.fun.other.utils.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static kronex.fun.other.utils.display.font.Fonts.Type.*;

public class ThemeEditorWindow extends AbstractWindow {
    private static final float HEADER_H = 24f;
    private static final int WINDOW_HEADER_COLOR = 0xF2282932;
    private static final int WINDOW_BODY_COLOR = 0xEE1D1F27;
    private static final int WINDOW_OUTLINE_COLOR = 0xFF353843;
    private static final int WINDOW_TITLE_COLOR = 0xFFF3F3F5;
    private static final int ITEM_COLOR = 0xE5282B34;
    private static final int ITEM_HOVER_COLOR = 0xF0303440;
    private static final int ITEM_OUTLINE_COLOR = 0x663C404C;
    private static final int ITEM_SELECTED_OUTLINE_COLOR = 0xFF6B7280;
    private static final int ITEM_TEXT_COLOR = 0xFFE6E6EA;
    private final List<AbstractComponent> presetComponents = new ArrayList<>();

    private float scroll = 0f;
    private float smoothedScroll = 0f;
    private float lastMaxScroll = 0f;

    public static void open() {
        open(MenuScreen.INSTANCE);
    }

    public static void open(MenuScreen menu) {
        ThemeEditorWindow existing = null;
        for (AbstractWindow w : windowManager.getWindows()) {
            if (w instanceof ThemeEditorWindow te) {
                existing = te;
                break;
            }
        }
        if (existing != null) {
            windowManager.getWindows().remove(existing);
            windowManager.add(existing);
            return;
        }

        float w = 116f;
        float h = 292f;
        float x = getAttachedX(menu, w);
        float y = getAttachedY(menu, h);

        ThemeEditorWindow win = new ThemeEditorWindow();
        win.position(x, y).size(w, h).draggable(false);
        windowManager.add(win);
    }

    public ThemeEditorWindow() {
        refreshPresets();
    }

    public void refreshPresets() {
        presetComponents.clear();
        File[] files = ThemeManager.getThemeDir().listFiles();
        if (files != null) {
            List<File> sortedFiles = new ArrayList<>();
            for (File f : files) {
                sortedFiles.add(f);
            }
            sortedFiles.sort(Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
            for (File f : sortedFiles) {
                if (f.getName().endsWith(".color")) {
                    String name = f.getName().replace(".color", "");
                    if (name.startsWith("default_")) {
                        continue;
                    }
                    presetComponents.add(new PresetItemComponent(name, this));
                }
            }
        }
    }

    private static float getAttachedX(MenuScreen menu, float windowW) {
        // TODO: Fix panelsContainerComponent reference
        float panelsTotalWidth = 5 * 126f + 4 * 6f;
        float panelsStartX = menu.x + (menu.width - panelsTotalWidth) / 2f;
        float x = panelsStartX - windowW - 78f;
        return Math.max(8f, x);
    }

    private static float getAttachedY(MenuScreen menu, float windowH) {
        float y = menu.y + (menu.height - windowH) / 2f;
        return Math.max(10f, y);
    }


    @Override
    protected void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        if (mc.currentScreen instanceof MenuScreen menu) {
            this.x = getAttachedX(menu, this.width);
            this.y = getAttachedY(menu, this.height);
        }

        MatrixStack matrix = context.getMatrices();

        blur.render(ShapeProperties.create(matrix, x, y, width, HEADER_H + 1f)
                .round(10, 0, 10, 0)
                .softness(1)
                .thickness(2)
                .outlineColor(WINDOW_OUTLINE_COLOR)
                .color(WINDOW_HEADER_COLOR)
                .build());

        blur.render(ShapeProperties.create(matrix, x, y + HEADER_H, width, height - HEADER_H)
                .round(0, 10, 0, 10)
                .softness(1)
                .thickness(2)
                .outlineColor(WINDOW_OUTLINE_COLOR)
                .color(WINDOW_BODY_COLOR)
                .build());

        FontRenderer titleFont = Fonts.getSize(15, Fonts.Type.DEFAULT);
        String title = "Themes";
        float titleW = titleFont.getStringWidth(title);
        titleFont.drawString(matrix, title, x + (width / 2f) - (titleW / 2f), y + 10.75f, WINDOW_TITLE_COLOR);

        float contentX = x + 4f;
        float contentY = y + HEADER_H;
        float contentW = width - 8f;
        float contentH = height - HEADER_H - 6f;

        List<AbstractComponent> renderComponents = getRenderComponents();
        scroll = MathHelper.clamp(scroll, -lastMaxScroll, 0f);
        smoothedScroll = (float) Calculate.interpolateSmooth(2, smoothedScroll, scroll);
        float renderScroll = Math.round(smoothedScroll);

        Kronex.getInstance().scissorManager.push(matrix.peek().getPositionMatrix(), contentX, contentY, contentW, contentH);

        float cy = contentY + 2f + renderScroll;
        float totalH = 0f;
        for (AbstractComponent component : renderComponents) {
            component.x = contentX;
            component.y = cy;
            component.width = contentW;
            component.render(context, mouseX, mouseY, delta);
            cy += component.height + 2f;
            totalH += component.height + 2f;
        }

        Kronex.getInstance().scissorManager.pop();

        totalH += 4f; // Bottom padding
        lastMaxScroll = Math.max(0f, totalH - contentH);
        float clamped = MathHelper.clamp(scroll, -lastMaxScroll, 0f);
        if (clamped != scroll) {
            scroll = clamped;
            smoothedScroll = clamped;
        }

        ThemeManager.applyToRuntime();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        getRenderComponents().forEach(c -> c.mouseClicked(mouseX, mouseY, button));
        return Calculate.isHovered(mouseX, mouseY, x, y, width, height);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        getRenderComponents().forEach(c -> c.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        getRenderComponents().forEach(c -> c.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        getRenderComponents().forEach(c -> c.mouseReleased(mouseX, mouseY, button));
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!Calculate.isHovered(mouseX, mouseY, x, y, width, height)) return false;
        for (AbstractComponent c : getRenderComponents()) {
            if (c.mouseScrolled(mouseX, mouseY, amount)) return true;
        }
        scroll += (float) (amount * 14f);
        return true;
    }

    private List<AbstractComponent> getRenderComponents() {
        return presetComponents;
    }

    private final class PresetItemComponent extends AbstractComponent {
        private final String name;
        private final ThemePreview preview;
        private final ThemeEditorWindow win;
        public PresetItemComponent(String name, ThemeEditorWindow win) {
            this.name = name;
            this.win = win;
            this.preview = readThemePreview(name);
            this.height = 22f;
        }
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            boolean hovered = Calculate.isHovered(mouseX, mouseY, x + 2, y, width - 4, height);
            boolean selected = ThemeManager.getCurrentThemeName().equalsIgnoreCase(name);

            float cardX = x + 2f;
            float cardY = y;
            float cardW = width - 4f;
            float cardH = height;
            float iconBoxX = cardX + 5f;
            float iconBoxY = cardY + 4.5f;
            float iconBoxSize = 14f;

            rectangle.render(ShapeProperties.create(context.getMatrices(), cardX, cardY, cardW, cardH)
                    .round(5f).softness(1f)
                    .thickness(selected ? 1.5f : 1f)
                    .outlineColor(selected ? ITEM_SELECTED_OUTLINE_COLOR : ITEM_OUTLINE_COLOR)
                    .color(hovered ? ITEM_HOVER_COLOR : ITEM_COLOR)
                    .build());

            rectangle.render(ShapeProperties.create(context.getMatrices(), iconBoxX, iconBoxY, iconBoxSize, iconBoxSize)
                    .round(3f).thickness(0).softness(1f)
                    .color(preview.background)
                    .build());
            rectangle.render(ShapeProperties.create(context.getMatrices(), iconBoxX + 2f, iconBoxY + 2f, iconBoxSize - 4f, 3f)
                    .round(1.5f).thickness(0).softness(1f)
                    .color(preview.primary)
                    .build());
            rectangle.render(ShapeProperties.create(context.getMatrices(), iconBoxX + 2f, iconBoxY + 6f, iconBoxSize - 6f, 2f)
                    .round(1f).thickness(0).softness(1f)
                    .color(preview.secondary)
                    .build());
            rectangle.render(ShapeProperties.create(context.getMatrices(), iconBoxX + 2f, iconBoxY + 9f, iconBoxSize - 4f, 2f)
                    .round(1f).thickness(0).softness(1f)
                    .color(preview.surface)
                    .build());
            rectangle.render(ShapeProperties.create(context.getMatrices(), iconBoxX + iconBoxSize - 3.5f, iconBoxY + 1.5f, 1.5f, 1.5f)
                    .round(0.75f).thickness(0).softness(1f)
                    .color(preview.text)
                    .build());

            Fonts.getSize(12, DEFAULT).drawString(context.getMatrices(), name, iconBoxX + iconBoxSize + 6f, cardY + 8.5f,
                    ITEM_TEXT_COLOR);
        }
        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            float cardX = x + 2f;
            float cardW = width - 4f;
            if (Calculate.isHovered(mouseX, mouseY, cardX, y, cardW, height)) {
                ThemeManager.loadTheme(name);
                return true;
            }
            return false;
        }
    }

    private ThemePreview readThemePreview(String themeName) {
        int fallbackPrimary = 0xFF6C9AFD;
        int fallbackSecondary = 0xFF8C7FFF;
        int fallbackBackground = 0xFF111114;
        int fallbackSurface = 0xFF202023;
        int fallbackText = 0xFFFFFFFF;
        try {
            File themeFile = new File(ThemeManager.getThemeDir(), themeName + ".color");
            if (!themeFile.exists()) {
                return new ThemePreview(fallbackPrimary, fallbackSecondary, fallbackBackground, fallbackSurface, fallbackText);
            }
            String content = Files.readString(themeFile.toPath(), StandardCharsets.UTF_8);
            JsonObject theme = JsonParser.parseString(content).getAsJsonObject();
            JsonObject colors = theme.has("colors") ? theme.getAsJsonObject("colors") : null;
            if (colors == null) {
                return new ThemePreview(fallbackPrimary, fallbackSecondary, fallbackBackground, fallbackSurface, fallbackText);
            }

            int primary = getThemeColor(colors, fallbackPrimary, "Первый цвет", "РџРµСЂРІС‹Р№ С†РІРµС‚");
            int secondary = getThemeColor(colors, fallbackSecondary, "Второй цвет", "Р’С‚РѕСЂРѕР№ С†РІРµС‚");
            int background = getThemeColor(colors, fallbackBackground, "Цвет заднего фона", "Р¦РІРµС‚ Р·Р°РґРЅРµРіРѕ С„РѕРЅР°");
            int surface = getThemeColor(colors, fallbackSurface, "Цвет выкл. модулей", "Р¦РІРµС‚ РІС‹РєР». РјРѕРґСѓР»РµР№");
            int text = getThemeColor(colors, fallbackText, "Цвет текста", "Р¦РІРµС‚ С‚РµРєСЃС‚Р°");
            return new ThemePreview(primary, secondary, background, surface, text);
        } catch (Exception ignored) {
            return new ThemePreview(fallbackPrimary, fallbackSecondary, fallbackBackground, fallbackSurface, fallbackText);
        }
    }

    private int getThemeColor(JsonObject colors, int fallback, String... keys) {
        for (String key : keys) {
            if (colors.has(key)) {
                try {
                    return colors.get(key).getAsInt();
                } catch (Exception ignored) {
                }
            }
        }
        return fallback;
    }

    private static final class ThemePreview {
        private final int primary;
        private final int secondary;
        private final int background;
        private final int surface;
        private final int text;

        private ThemePreview(int primary, int secondary, int background, int surface, int text) {
            this.primary = primary;
            this.secondary = secondary;
            this.background = background;
            this.surface = surface;
            this.text = text;
        }
    }
}
