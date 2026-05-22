package kronex.fun.display.screens.clickgui.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;
import kronex.fun.features.module.setting.implement.BindSetting;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.math.MathUtil;
import kronex.fun.other.utils.other.StringUtil;

import static kronex.fun.other.utils.display.font.Fonts.Type.BOLD;

public class BindComponent extends AbstractSettingComponent {
    private final BindSetting setting;
    private boolean binding;

    public BindComponent(BindSetting setting) {
        super(setting);
        this.setting = setting;
    }

    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();

        String bindName = StringUtil.getBindName(setting.getKey());
        String name = binding ? "..." : bindName;
        float stringWidth = Fonts.getSize(12, BOLD).getStringWidth(name);
        String wrapped = setting.getDescription();

        height = 18;

        rectangle.render(ShapeProperties.create(matrix, x + width - stringWidth - 16, y + 5.5f, stringWidth + 8, 13)
                .round(3).softness(1).thickness(1.5f)
                .outlineColor(binding ? ColorAssist.getClientColor() : 0xFF808080)
                .color(binding ? ColorAssist.applyOpacity(ColorAssist.getClientColor(), 30) : 0x28282864)
                .build());

        int textColor = binding ? ColorAssist.getClientColor() : 0xFFFFFFFF;
        Fonts.getSize(12, BOLD).drawString(matrix, name, x + width - 12 - stringWidth, y + 9.8f, textColor);

        Fonts.getSize(14, BOLD).drawString(matrix, setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12, Fonts.Type.DEFAULT).drawString(matrix, wrapped, x + 9, y + 15, 0xFF878894);
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (MathUtil.isHovered(mouseX, mouseY, x, y, width, height)) {
                binding = !binding;
            } else {
                binding = false;
            }
        }

        if (binding && button > 1) {
            setting.setKey(button);
            binding = false;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int key = keyCode == GLFW.GLFW_KEY_DELETE ? -1 : keyCode;
        if (binding) {
            setting.setKey(key);
            binding = false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
