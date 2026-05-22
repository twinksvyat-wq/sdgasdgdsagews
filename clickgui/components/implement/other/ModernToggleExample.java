package kronex.fun.display.screens.clickgui.components.implement.other;

import kronex.fun.display.screens.clickgui.components.AbstractComponent;
import kronex.fun.other.utils.display.interfaces.QuickImports;

/**
 * Example usage of ModernToggleComponent
 * This shows how to create and use the modern toggle UI element
 */
public class ModernToggleExample extends AbstractComponent implements QuickImports {
    
    private final ModernToggleComponent autoAcceptToggle;
    private final ModernToggleComponent autoReplyToggle;
    
    public ModernToggleExample() {
        // Create AutoAccept toggle (like in the image)
        autoAcceptToggle = new ModernToggleComponent()
                .setTitle("AutoAccept")
                .setDescription("Auto accept teleport request")
                .setEnabled(false);
        
        // Create another example toggle
        autoReplyToggle = new ModernToggleComponent()
                .setTitle("AutoReply")
                .setDescription("Automatically reply to messages")
                .setEnabled(false);
        
        // Set the runnables after initialization to avoid variable initialization issues
        autoAcceptToggle.setToggleRunnable(() -> {
            // Handle toggle logic here
            System.out.println("AutoAccept toggled: " + autoAcceptToggle.isEnabled());
        });
        
        autoReplyToggle.setToggleRunnable(() -> {
            System.out.println("AutoReply toggled: " + autoReplyToggle.isEnabled());
        });
    }
    
    @Override
    public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        // Position the toggles
        autoAcceptToggle.x = x;
        autoAcceptToggle.y = y;
        autoAcceptToggle.render(context, mouseX, mouseY, delta);
        
        autoReplyToggle.x = x;
        autoReplyToggle.y = y + 50; // Space below the first toggle
        autoReplyToggle.render(context, mouseX, mouseY, delta);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Pass mouse events to the toggle components
        autoAcceptToggle.mouseClicked(mouseX, mouseY, button);
        autoReplyToggle.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean isHover(double mouseX, double mouseY) {
        return autoAcceptToggle.isHover(mouseX, mouseY) || 
               autoReplyToggle.isHover(mouseX, mouseY) ||
               super.isHover(mouseX, mouseY);
    }
    
    @Override
    public void tick() {
        autoAcceptToggle.tick();
        autoReplyToggle.tick();
        super.tick();
    }
    
    // Getters for accessing the toggles
    public ModernToggleComponent getAutoAcceptToggle() {
        return autoAcceptToggle;
    }
    
    public ModernToggleComponent getAutoReplyToggle() {
        return autoReplyToggle;
    }
}
