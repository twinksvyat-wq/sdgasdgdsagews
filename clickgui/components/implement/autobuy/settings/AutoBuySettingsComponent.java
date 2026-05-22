package kronex.fun.display.screens.clickgui.components.implement.autobuy.settings;

import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.display.screens.clickgui.components.implement.autobuy.originalitems.ItemRegistry;
import kronex.fun.other.utils.display.color.ColorAssist;
import kronex.fun.other.utils.display.font.Fonts;
import kronex.fun.other.utils.display.shape.ShapeProperties;
import kronex.fun.other.utils.math.calc.Calculate;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.glfw.GLFW;

import java.awt.Color;

public abstract class AutoBuySettingsComponent extends AbstractComponent {
    protected final AutoBuyItemSettings settings;

    public AutoBuySettingsComponent(AutoBuyItemSettings settings) {
        this.settings = settings;
        this.height = 18; // slightly taller to make click easier
    }

    public static class BuyBelowComponent extends AutoBuySettingsComponent {
        private boolean editing = false;
        private String inputText = "";

        public BuyBelowComponent(AutoBuyItemSettings settings) {
            super(settings);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            MatrixStack matrix = context.getMatrices();

            Fonts.getSize(13, Fonts.Type.SEMI).drawString(matrix, "Покупать ниже:", x + 10, y + 10, ColorAssist.getText(0.8f));

            String displayText = editing ? inputText + "_" : settings.getBuyBelow() + "$";
            float textWidth = Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(displayText);

            rectangle.render(ShapeProperties.create(matrix, x + width - textWidth - 16, y + 5, textWidth + 9, 14)
                    .round(4).thickness(2.5f)
                    .outlineColor(editing ? new Color(255, 255, 255, 100).getRGB() : new Color(41, 42, 40, 140).getRGB())
                    .color(editing ? new Color(255, 255, 255, 20).getRGB() : new Color(41, 42, 40, 40).getRGB())
                    .build());

            Fonts.getSize(13, Fonts.Type.DEFAULT).drawString(matrix, displayText, x + width - textWidth - 12, y + 10.5f, ColorAssist.getText());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            // Click anywhere on the right side of the component
            if (Calculate.isHovered(mouseX, mouseY, x + width / 2f, y, width / 2f, height) && button == 0) {
                editing = true;
                inputText = String.valueOf(settings.getBuyBelow());
                return true;
            } else {
                editing = false;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (editing) {
                if (keyCode == GLFW.GLFW_KEY_ENTER) {
                    try {
                        int value = Integer.parseInt(inputText);
                        settings.setBuyBelow(Math.max(1, value));
                        AutoBuySettingsManager.getInstance().saveSettings(settings.getItemName(), settings);
                        ItemRegistry.reloadSettings();
                    } catch (NumberFormatException ignored) {}
                    editing = false;
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    editing = false;
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !inputText.isEmpty()) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                    return true;
                }
                return true; // absorb other keys while editing
            }
            return false;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (editing && Character.isDigit(chr) && inputText.length() < 9) {
                inputText += chr;
                return true;
            }
            if (editing) return true; // absorb chars
            return false;
        }

        @Override
        public boolean isHover(double mouseX, double mouseY) {
            return Calculate.isHovered(mouseX, mouseY, x, y, width, height);
        }
    }

    public static class MinQuantityComponent extends AutoBuySettingsComponent {
        private boolean editing = false;
        private String inputText = "";

        public MinQuantityComponent(AutoBuyItemSettings settings) {
            super(settings);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            MatrixStack matrix = context.getMatrices();

            Fonts.getSize(13, Fonts.Type.SEMI).drawString(matrix, "Количество от:", x + 10, y + 10, ColorAssist.getText(0.8f));

            String displayText = editing ? inputText + "_" : String.valueOf(settings.getMinQuantity());
            float textWidth = Fonts.getSize(13, Fonts.Type.DEFAULT).getStringWidth(displayText);

            rectangle.render(ShapeProperties.create(matrix, x + width - textWidth - 16, y + 5, textWidth + 9, 14)
                    .round(4).thickness(2.5f)
                    .outlineColor(editing ? new Color(255, 255, 255, 100).getRGB() : new Color(41, 42, 40, 140).getRGB())
                    .color(editing ? new Color(255, 255, 255, 20).getRGB() : new Color(41, 42, 40, 40).getRGB())
                    .build());

            Fonts.getSize(13, Fonts.Type.DEFAULT).drawString(matrix, displayText, x + width - textWidth - 11.5f, y + 10.5f, ColorAssist.getText());
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (Calculate.isHovered(mouseX, mouseY, x + width / 2f, y, width / 2f, height) && button == 0) {
                editing = true;
                inputText = String.valueOf(settings.getMinQuantity());
                return true;
            } else {
                editing = false;
            }
            return false;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (editing) {
                if (keyCode == GLFW.GLFW_KEY_ENTER) {
                    try {
                        int value = Integer.parseInt(inputText);
                        settings.setMinQuantity(Math.max(1, Math.min(64, value)));
                        AutoBuySettingsManager.getInstance().saveSettings(settings.getItemName(), settings);
                        ItemRegistry.reloadSettings();
                    } catch (NumberFormatException ignored) {}
                    editing = false;
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
                    editing = false;
                    return true;
                } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !inputText.isEmpty()) {
                    inputText = inputText.substring(0, inputText.length() - 1);
                    return true;
                }
                return true;
            }
            return false;
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            if (editing && Character.isDigit(chr) && inputText.length() < 2) {
                inputText += chr;
                return true;
            }
            if (editing) return true;
            return false;
        }

        @Override
        public boolean isHover(double mouseX, double mouseY) {
            return Calculate.isHovered(mouseX, mouseY, x, y, width, height);
        }
    }
}
