package kronex.fun.display.screens.clickgui.components.implement.module;

import kronex.fun.Kronex;
import kronex.fun.features.impl.render.Interface;
import kronex.fun.other.utils.display.other.animation.Animation;
import kronex.fun.other.utils.display.other.animation.Direction;
import kronex.fun.other.common.animation.implement.Decelerate;
import kronex.fun.display.screens.clickgui.MenuScreen;
import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.AbstractSettingComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.BindComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.CheckboxComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.ColorComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.GroupComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.SButtonComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.SliderComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.TextComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.multiselect.MultiSelectComponent;
import kronex.fun.display.screens.clickgui.components.implement.settings.select.SelectComponent;
import kronex.fun.features.module.Module;
import kronex.fun.features.module.setting.SettingComponentAdder;
import kronex.fun.other.utils.client.chat.StringHelper;
import kronex.fun.other.utils.client.sound.SoundManager;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.display.color.HudColorutility;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.other.animation.implement.DecelerateAnimation;
import kronex.fun.other.utils.display.scissor.ScissorAssist;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import kronex.fun.other.utils.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import kronex.fun.other.utils.display.geometry.Render2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static kronex.fun.other.utils.display.font.Fonts.Type.*;

public class PanelsModuleComponent extends AbstractComponent {
    private final Module module;
    private final List<AbstractSettingComponent> settingComponents = new ArrayList<>();

    private static final float ROW_H = 17f;
    private static final float HEADER_TEXT_X = 7f;
    private static final float HEADER_TEXT_Y = 5.5f;
    private static final float HEADER_RADIUS = 3f;
    private static final float DIVIDER_MARGIN_X = 5f;
    private static final float DIVIDER_HEIGHT = 0.5f;
    private static final float DIVIDER_Y_OFFSET = 2f;
    private static final float SETTINGS_TOP_PADDING = 2f;
    private static final float SETTINGS_BOTTOM_PADDING = 8f;
    private static final float SETTINGS_RENDER_THRESHOLD = 0.65f;

    private static final int EXPAND_ANIMATION_MS = 281;

    private static final long SHAKE_DURATION = 500;
    private static final float SHAKE_INTENSITY = 3f;
    private static final int SHAKE_FREQUENCY = 8;

    private static final float BIND_BOX_HEIGHT = 10f;
    private static final float BIND_BOX_PADDING = 3f;
    private static final float BIND_BOX_ROUND = 3f;
    private static final float BIND_BOX_RIGHT_MARGIN = 5f;

    private boolean expanded = false;
    private boolean binding = false;

    private long shakeStartTime = 0;

    private final kronex.fun.other.utils.display.other.animation.Animation expandAnimation = new DecelerateAnimation().setMs(EXPAND_ANIMATION_MS).setValue(1);
    private final kronex.fun.other.utils.display.other.animation.Animation hoverAnimation = new DecelerateAnimation().setMs(140).setValue(1);

    public PanelsModuleComponent(Module module) {
        this.module = module;
        new SettingComponentAdder().addSettingComponent(module.settings(), settingComponents);

        expandAnimation.setDirection(Direction.BACKWARDS);
        expandAnimation.reset();

        hoverAnimation.setDirection(Direction.BACKWARDS);
        hoverAnimation.reset();
    }

    public Module getModule() {
        return module;
    }

    private boolean isShaking() {
        return System.currentTimeMillis() - shakeStartTime < SHAKE_DURATION;
    }

    private float getShakeOffset() {
        if (!isShaking()) return 0f;
        long elapsed = System.currentTimeMillis() - shakeStartTime;
        float progress = (float) elapsed / SHAKE_DURATION;
        float decay = 1f - progress;
        return (float) Math.sin(elapsed * SHAKE_FREQUENCY * 0.05) * SHAKE_INTENSITY * decay;
    }

    private void startShake() {
        shakeStartTime = System.currentTimeMillis();
    }

    private float estimateSettingHeight(AbstractSettingComponent c) {
        if (c instanceof CheckboxComponent) return 18f;
        if (c instanceof BindComponent) return 18f;
        if (c instanceof ColorComponent) return 18f;
        if (c instanceof TextComponent) return 18f;
        if (c instanceof SliderComponent) return 28f;
        if (c instanceof GroupComponent) return 18f;
        if (c instanceof SButtonComponent) return 18f;
        if (c instanceof SelectComponent) return 18f;
        if (c instanceof MultiSelectComponent) return 18f;
        return 18f;
    }

    private float getExpandedHeight() {
        float h = ROW_H;
        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                Supplier<Boolean> visible = c.getSetting().getVisible();
                if (visible != null && !visible.get()) continue;
                h += c.height > 0 ? c.height : estimateSettingHeight(c);
            }
        }
        return h + SETTINGS_BOTTOM_PADDING;
    }

    private float getAnimatedHeight() {
        expandAnimation.setDirection(expanded ? Direction.FORWARDS : Direction.BACKWARDS);
        if (module.settings().isEmpty()) {
            return ROW_H;
        }
        float targetH = getExpandedHeight();
        float t = expandAnimation.getOutput().floatValue();
        return ROW_H + (targetH - ROW_H) * t;
    }

    public float getComponentHeight() {
        return getAnimatedHeight();
    }

    @Override
    public boolean isHover(double mouseX, double mouseY) {
        return Calculate.isHovered(mouseX, mouseY, x, y, width, getAnimatedHeight());
    }

    private String getBindDisplayName() {
        if (binding) return "...";
        int key = module.getKey();
        if (key == -1) return null;
        String name = StringHelper.getBindName(key);
        if (name == null || name.isEmpty()) {
            String glfwName = GLFW.glfwGetKeyName(key, 0);
            if (glfwName != null) return glfwName.toUpperCase();
            return String.valueOf((char) key);
        }
        return name;
    }

    private float getBindBoxWidth(String bindName) {
        if (bindName == null) return 0f;
        return Fonts.getSize(10, Fonts.Type.GUIICONS).getStringWidth("h") + BIND_BOX_PADDING * 2f + 2f;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();
        Matrix4f positionMatrix = matrix.peek().getPositionMatrix();

        float rowH = ROW_H;
        float radius = HEADER_RADIUS;

        float totalH = getAnimatedHeight();
        float t = expandAnimation.getOutput().floatValue();
        boolean renderSettings = expanded || t > SETTINGS_RENDER_THRESHOLD;

        boolean hovered = Calculate.isHovered(mouseX, mouseY, x, y, width, rowH);
        hoverAnimation.setDirection(hovered ? Direction.FORWARDS : Direction.BACKWARDS);

        if (hovered) {
            // Tooltip removed
        }

        float stateT = module.getAnimation().getOutput().floatValue();
        stateT = Math.max(0f, Math.min(1f, stateT));

        float shakeOffset = getShakeOffset();
        float renderX = x + shakeOffset;

        boolean shaking = isShaking();
        float shakeProgress = shaking ? 1f - (float)(System.currentTimeMillis() - shakeStartTime) / SHAKE_DURATION : 0f;

        int offBg = ColorAssist.rgba(24, 26, 33, 215);
        int onBg = ColorAssist.interpolateColor(ColorAssist.rgba(24, 26, 33, 215), HudColorutility.getClientColor(), 0.35f);
        int bgColor = ColorAssist.lerp(stateT, offBg, onBg);
        int outlineColor = ColorAssist.interpolateColor(
                ColorAssist.rgba(60, 66, 80, 130),
                HudColorutility.getClientColor(),
                stateT
        );

        blur.render(ShapeProperties.create(matrix, renderX, y, width, totalH)
                .round(radius)
                .softness(1)
                .thickness(2.1f)
                .outlineColor(outlineColor)
                .color(bgColor)
                .build());

        if (totalH > rowH + 0.5f) {
            rectangle.render(ShapeProperties.create(matrix, renderX + DIVIDER_MARGIN_X, y + rowH + DIVIDER_Y_OFFSET, width - (DIVIDER_MARGIN_X * 2f), DIVIDER_HEIGHT)
                    .color(ColorAssist.rgba(112, 116, 132, 80)).build());
        }

        int normalTextColor = ColorAssist.interpolateColor(ColorAssist.rgba(186, 191, 205, 255), Color.WHITE.getRGB(), stateT);
        int animatedTextColor;
        if (shaking) {
            int redColor = new Color(255, (int)(255 * (1f - shakeProgress)), (int)(255 * (1f - shakeProgress))).getRGB();
            animatedTextColor = ColorAssist.interpolateColor(normalTextColor, redColor, shakeProgress);
        } else {
            animatedTextColor = normalTextColor;
        }

        Fonts.getSize(14, DEFAULT).drawString(matrix, module.getVisibleName(), renderX + HEADER_TEXT_X, y + HEADER_TEXT_Y + 0.5f, animatedTextColor);

        String bindName = getBindDisplayName();
        float settingsIconX = renderX + width - 12f;

        if (bindName != null) {
            float bindBoxW = getBindBoxWidth(bindName);
            float bindBoxX;
            if (!module.settings().isEmpty()) {
                bindBoxX = settingsIconX - bindBoxW - 4f;
            } else {
                bindBoxX = renderX + width - BIND_BOX_RIGHT_MARGIN - bindBoxW;
            }
            float bindBoxY = y + (rowH - BIND_BOX_HEIGHT) / 2f;

            // Получаем цвет темы
            Color themeColor = new Color(ThemeManager.primaryColor.getColor());

            // Рисуем бокс с обводкой цвета темы
            rectangle.render(ShapeProperties.create(matrix, bindBoxX, bindBoxY, bindBoxW, BIND_BOX_HEIGHT)
                    .round(BIND_BOX_ROUND)
                    .thickness(1.5f)
                    .softness(1)
                    .outlineColor(binding ? themeColor.getRGB() : ColorAssist.rgba(128, 128, 128, 255))
                    .color(binding ? new Color(themeColor.getRed(), themeColor.getGreen(), themeColor.getBlue(), 30).getRGB() : ColorAssist.rgba(40, 40, 40, 100))
                    .build());

            int bindTextColor = binding ? themeColor.getRGB() : ColorAssist.rgba(200, 200, 200, 255);
            Fonts.getSize(10, Fonts.Type.GUIICONS).drawString(matrix, "h",
                    bindBoxX + BIND_BOX_PADDING + 1f,
                    bindBoxY + 3.8f,
                    bindTextColor);
        }

        if (!module.settings().isEmpty()) {
            Fonts.getSize(16, HUD2).drawString(matrix, "f", settingsIconX, y + 6.6f,
                    ColorAssist.interpolateColor(ColorAssist.rgba(120, 126, 145, 255), HudColorutility.getIconColor(), hoverAnimation.getOutput().floatValue()));
        }

        if (totalH <= rowH + 0.5f) {
            height = rowH;
            return;
        }

        if (!renderSettings) {
            height = totalH;
            return;
        }

        float visibleSettingsH = Math.max(0f, totalH - (rowH + SETTINGS_TOP_PADDING));
        ScissorAssist scissor = Kronex.getInstance().scissorManager;
        scissor.push(positionMatrix, renderX, y + rowH, width, rowH + SETTINGS_TOP_PADDING + visibleSettingsH);

        float yOff = rowH + SETTINGS_TOP_PADDING;

        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                Supplier<Boolean> visible = c.getSetting().getVisible();
                if (visible != null && !visible.get()) continue;

                c.x = renderX - 2f;
                c.y = y + yOff;
                c.width = width + 2f;
                c.render(context, mouseX, mouseY, delta);
                yOff += c.height > 0 ? c.height : estimateSettingHeight(c);
            }
        }

        scissor.pop();
        height = totalH;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float rowH = ROW_H;

        if (Calculate.isHovered(mouseX, mouseY, x, y, width, rowH)) {
            String bindName = getBindDisplayName();
            if (bindName != null || binding) {
                float bindBoxW = getBindBoxWidth(bindName != null ? bindName : "...");
                float settingsIconX = x + width - 12f;
                float bindBoxX;
                if (!module.settings().isEmpty()) {
                    bindBoxX = settingsIconX - bindBoxW - 4f;
                } else {
                    bindBoxX = x + width - BIND_BOX_RIGHT_MARGIN - bindBoxW;
                }
                float bindBoxY = y + (rowH - BIND_BOX_HEIGHT) / 2f;

                if (Calculate.isHovered(mouseX, mouseY, bindBoxX, bindBoxY, bindBoxW, BIND_BOX_HEIGHT)) {
                    binding = !binding;
                    return true;
                }
            }

            if (module.getKey() != -1 || binding) {
            } else {
                float noBindBoxW = getBindBoxWidth("...");
                float noBindSettingsIconX = x + width - 12f;
                float noBindBoxX;
                if (!module.settings().isEmpty()) {
                    noBindBoxX = noBindSettingsIconX - noBindBoxW - 4f;
                } else {
                    noBindBoxX = x + width - BIND_BOX_RIGHT_MARGIN - noBindBoxW;
                }
            }

            if (button == 0) {
                module.switchState();
                SoundManager.playSound(module.state ? SoundManager.ENABLE_MODULE : SoundManager.DISABLE_MODULE, Interface.getInstance().getModuleVolume(), 1.0f);
                return true;
            } else if (button == 1) {
                if (module.settings().isEmpty()) {
                    startShake();
                    return true;
                }
                expanded = !expanded;
                SoundManager.playSound(SoundManager.CATEGORY_CLICK, Interface.getInstance().getModuleVolume(), 1.0f);
                return true;
            } else if (button == 2) {
                binding = !binding;
                return true;
            }
        }

        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                Supplier<Boolean> visible = c.getSetting().getVisible();
                if (visible != null && !visible.get()) continue;
                if (c.mouseClicked(mouseX, mouseY, button)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                c.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                if (c.mouseScrolled(mouseX, mouseY, amount)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            int key = keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_DELETE ? -1 : keyCode;
            if (key != GLFW.GLFW_KEY_RIGHT_SHIFT) {
                module.setKey(key);
                binding = false;
            }
            return true;
        }

        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                if (c.keyPressed(keyCode, scanCode, modifiers)) return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (expanded) {
            for (AbstractSettingComponent c : settingComponents) {
                if (c.charTyped(chr, modifiers)) return true;
            }
        }
        return false;
    }
}
