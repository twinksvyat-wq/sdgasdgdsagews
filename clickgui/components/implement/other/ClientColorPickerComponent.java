package kronex.fun.display.screens.clickgui.components.implement.other;

import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.features.module.setting.implement.ColorSetting;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import kronex.fun.other.utils.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

import static net.minecraft.util.math.MathHelper.clamp;

public class ClientColorPickerComponent extends AbstractComponent {
    
    private static final float PICKER_SIZE = 80f;
    private static final float HUE_BAR_HEIGHT = 8f;
    private static final float GAP = 5f;
    private static final float PREVIEW_SIZE = 16f;
    
    private boolean expanded = false;
    private boolean satBrightDragging = false;
    private boolean hueDragging = false;
    
    private float hue = 0f;
    private float saturation = 1f;
    private float brightness = 1f;
    
    public ClientColorPickerComponent() {
        ColorSetting colorSetting = ThemeManager.primaryColor;
        int color = colorSetting.getColor();
        float[] hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();
        syncFromSetting();
        
        int currentColor = Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
        
        // Preview button
        float previewX = x + width - PREVIEW_SIZE - 4;
        float previewY = y + height - PREVIEW_SIZE - 4;
        
        blur.render(ShapeProperties.create(matrix, previewX - 2, previewY - 2, PREVIEW_SIZE + 4, PREVIEW_SIZE + 4)
                .round(6f)
                .color(new Color(11, 12, 18, 200).getRGB())
                .build());
        
        rectangle.render(ShapeProperties.create(matrix, previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE)
                .round(5f)
                .color(new Color(18, 20, 28, 230).getRGB())
                .build());
        
        rectangle.render(ShapeProperties.create(matrix, previewX, previewY, PREVIEW_SIZE, PREVIEW_SIZE)
                .round(5f)
                .thickness(1.5f)
                .color(0x00000000)
                .outlineColor(0xAAFFFFFF)
                .build());

        Fonts.getSize(20, Fonts.Type.ICONS2).drawCenteredString(matrix, "F",
                previewX + PREVIEW_SIZE / 2f, previewY + 5.75f, currentColor);
        
        if (!expanded) return;
        
        // Фон пикера
        float pickerX = previewX - PICKER_SIZE - GAP;
        float pickerY = previewY - PICKER_SIZE - HUE_BAR_HEIGHT - GAP - 4;
        float totalHeight = PICKER_SIZE + GAP + HUE_BAR_HEIGHT;
        
        blur.render(ShapeProperties.create(matrix, pickerX - 4, pickerY - 4, PICKER_SIZE + 8, totalHeight + 8)
                .round(6)
                .color(new Color(11, 12, 18, 230).getRGB())
                .build());
        
        // Saturation/Brightness квадрат
        int[] colors = {
                0xFF000000,
                0xFFFFFFFF,
                0xFF000000,
                Color.HSBtoRGB(hue, 1, 1)
        };
        
        rectangle.render(ShapeProperties.create(matrix, pickerX, pickerY, PICKER_SIZE, PICKER_SIZE)
                .round(3)
                .color(colors)
                .build());
        
        // Курсор на квадрате
        float cursorX = clamp(pickerX + PICKER_SIZE * saturation, pickerX, pickerX + PICKER_SIZE - 4);
        float cursorY = clamp(pickerY + PICKER_SIZE * (1 - brightness), pickerY, pickerY + PICKER_SIZE - 4);
        
        rectangle.render(ShapeProperties.create(matrix, cursorX - 2, cursorY - 2, 5, 5)
                .round(2.5f)
                .thickness(2)
                .color(0x00FFFFFF)
                .outlineColor(0xFFFFFFFF)
                .build());
        
        // Hue бар
        float hueY = pickerY + PICKER_SIZE + GAP;
        image.setTexture("textures/gui/sliderhue.png").render(ShapeProperties.create(matrix, pickerX, hueY, PICKER_SIZE, HUE_BAR_HEIGHT).round(2).build());
        
        // Курсор на hue баре
        float hueCursorX = clamp(pickerX + PICKER_SIZE * hue, pickerX, pickerX + PICKER_SIZE - HUE_BAR_HEIGHT);
        
        rectangle.render(ShapeProperties.create(matrix, hueCursorX, hueY, HUE_BAR_HEIGHT, HUE_BAR_HEIGHT)
                .round(HUE_BAR_HEIGHT / 2f)
                .thickness(2)
                .color(0x00FFFFFF)
                .outlineColor(0xFFFFFFFF)
                .build());
        
        // Обработка драга
        if (satBrightDragging) {
            saturation = clamp((float)(mouseX - pickerX) / PICKER_SIZE, 0, 1);
            brightness = clamp(1 - (float)(mouseY - pickerY) / PICKER_SIZE, 0, 1);
            updateColor();
        }
        
        if (hueDragging) {
            hue = clamp((float)(mouseX - pickerX) / PICKER_SIZE, 0, 1);
            updateColor();
        }
    }
    
    private void updateColor() {
        int newColor = Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
        ThemeManager.primaryColor.setColor(newColor);
        ThemeManager.applyToRuntime();
    }

    public void toggleExpanded() {
        expanded = !expanded;
    }

    public boolean isExpanded() {
        return expanded;
    }

    private void syncFromSetting() {
        if (satBrightDragging || hueDragging) {
            return;
        }

        int color = ThemeManager.primaryColor.getColor();
        int currentColor = Color.HSBtoRGB(hue, saturation, brightness) | 0xFF000000;
        if ((color | 0xFF000000) == (currentColor | 0xFF000000)) {
            return;
        }

        float[] hsb = Color.RGBtoHSB((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, null);
        hue = hsb[0];
        saturation = hsb[1];
        brightness = hsb[2];
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return false;
        
        float previewX = x + width - PREVIEW_SIZE - 4;
        float previewY = y + height - PREVIEW_SIZE - 4;
        
        // Клик на превью - открыть/закрыть
        if (Calculate.isHovered(mouseX, mouseY, previewX - 2, previewY - 2, PREVIEW_SIZE + 4, PREVIEW_SIZE + 4)) {
            expanded = !expanded;
            return true;
        }
        
        if (!expanded) return false;
        
        float pickerX = previewX - PICKER_SIZE - GAP;
        float pickerY = previewY - PICKER_SIZE - HUE_BAR_HEIGHT - GAP - 4;
        float hueY = pickerY + PICKER_SIZE + GAP;
        
        // Клик на квадрат saturation/brightness
        if (Calculate.isHovered(mouseX, mouseY, pickerX, pickerY, PICKER_SIZE, PICKER_SIZE)) {
            satBrightDragging = true;
            saturation = clamp((float)(mouseX - pickerX) / PICKER_SIZE, 0, 1);
            brightness = clamp(1 - (float)(mouseY - pickerY) / PICKER_SIZE, 0, 1);
            updateColor();
            return true;
        }
        
        // Клик на hue бар
        if (Calculate.isHovered(mouseX, mouseY, pickerX, hueY, PICKER_SIZE, HUE_BAR_HEIGHT)) {
            hueDragging = true;
            hue = clamp((float)(mouseX - pickerX) / PICKER_SIZE, 0, 1);
            updateColor();
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        satBrightDragging = false;
        hueDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }
}


