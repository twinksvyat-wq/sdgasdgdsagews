package kronex.fun.display.screens.clickgui.components.implement.other;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import kronex.fun.Kronex;
import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.display.font.FontRenderer;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.scissor.ScissorAssist;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import kronex.fun.other.utils.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeComponent extends AbstractComponent {
    private static final float CELL_W = 62f;
    private static final float CELL_H = 72f;
    private static final float CELL_GAP = 8f;
    private static final float SWATCH_SIZE = 34f;

    private List<String> allThemes;
    private final Map<String, Integer> themeColors = new HashMap<>();

    public ThemeComponent() {
        refreshThemes();
    }

    private void refreshThemes() {
        allThemes = ThemeManager.listThemes();
        themeColors.clear();

        for (String themeName : allThemes) {
            themeColors.put(themeName, loadThemeColor(themeName));
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

            String[] possibleKeys = {
                    "Первый цвет",
                    "primary",
                    "Primary Color",
                    "РџРµСЂРІС‹Р№ С†РІРµС‚",
                    "Р СџР ВµРЎР‚Р Р†РЎвЂ№Р в„– РЎвЂ Р Р†Р ВµРЎвЂљ"
            };

            for (String key : possibleKeys) {
                if (colors.has(key)) {
                    return colors.get(key).getAsInt() | 0xFF000000;
                }
            }
        } catch (Exception ignored) {
        }
        return 0xFF6C9AFD;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (allThemes == null || allThemes.isEmpty()) {
            refreshThemes();
        }

        MatrixStack matrix = context.getMatrices();
        Matrix4f posMatrix = matrix.peek().getPositionMatrix();
        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        FontRenderer nameFont = Fonts.getSize(11, Fonts.Type.BOLD);
        FontRenderer colorFont = Fonts.getSize(10, Fonts.Type.DEFAULT);
        int columns = getColumns();
        float gridW = columns * CELL_W + (columns - 1) * CELL_GAP;
        float startX = x + Math.max(0, (width - gridW) / 2f);
        String currentTheme = ThemeManager.getCurrentThemeName();

        scissor.push(posMatrix, x, y, width, height);
        for (int i = 0; i < allThemes.size(); i++) {
            String themeName = allThemes.get(i);
            int color = themeColors.getOrDefault(themeName, 0xFF6C9AFD);
            int col = i % columns;
            int row = i / columns;
            float cellX = startX + col * (CELL_W + CELL_GAP);
            float cellY = y + row * (CELL_H + CELL_GAP);
            boolean selected = themeName.equalsIgnoreCase(currentTheme);
            boolean hovered = Calculate.isHovered(mouseX, mouseY, cellX, cellY, CELL_W, CELL_H);

            rectangle.render(ShapeProperties.create(matrix, cellX, cellY, CELL_W, CELL_H)
                    .round(4f).thickness(1.5f)
                    .outlineColor(selected ? ColorAssist.getClientColor() : ColorAssist.getOutline(hovered ? 0.55f : 0.25f, 1))
                    .color(ColorAssist.getGuiRectColor2(hovered || selected ? 0.72f : 0.45f))
                    .build());

            float swatchX = cellX + (CELL_W - SWATCH_SIZE) / 2f;
            float swatchY = cellY + 7f;
            rectangle.render(ShapeProperties.create(matrix, swatchX, swatchY, SWATCH_SIZE, SWATCH_SIZE)
                    .round(4f).thickness(1.5f)
                    .outlineColor(ColorAssist.applyOpacity(0xFFFFFFFF, 45))
                    .color(color)
                    .build());

            String displayName = trimToWidth(nameFont, themeName, CELL_W - 8f);
            float nameW = nameFont.getStringWidth(displayName);
            nameFont.drawString(matrix, displayName, cellX + (CELL_W - nameW) / 2f, cellY + 48f,
                    selected ? ColorAssist.getClientColor() : 0xFFE5E7EF);

            String colorText = formatColor(color);
            float colorW = colorFont.getStringWidth(colorText);
            colorFont.drawString(matrix, colorText, cellX + (CELL_W - colorW) / 2f, cellY + 60f, 0xFF9EA1AD);
        }
        scissor.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (allThemes == null || allThemes.isEmpty()) {
            refreshThemes();
        }

        int columns = getColumns();
        float gridW = columns * CELL_W + (columns - 1) * CELL_GAP;
        float startX = x + Math.max(0, (width - gridW) / 2f);

        for (int i = 0; i < allThemes.size(); i++) {
            int col = i % columns;
            int row = i / columns;
            float cellX = startX + col * (CELL_W + CELL_GAP);
            float cellY = y + row * (CELL_H + CELL_GAP);

            if (Calculate.isHovered(mouseX, mouseY, cellX, cellY, CELL_W, CELL_H)) {
                ThemeManager.loadTheme(allThemes.get(i));
                refreshThemes();
                return true;
            }
        }

        return false;
    }

    private int getColumns() {
        return Math.max(1, (int) ((width + CELL_GAP) / (CELL_W + CELL_GAP)));
    }

    private String formatColor(int color) {
        return String.format("FF #%02X%02X%02X", (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
    }

    private String trimToWidth(FontRenderer font, String text, float maxWidth) {
        if (font.getStringWidth(text) <= maxWidth) {
            return text;
        }

        for (int i = text.length(); i > 0; i--) {
            String candidate = text.substring(0, i) + "..";
            if (font.getStringWidth(candidate) <= maxWidth) {
                return candidate;
            }
        }
        return "..";
    }
}
