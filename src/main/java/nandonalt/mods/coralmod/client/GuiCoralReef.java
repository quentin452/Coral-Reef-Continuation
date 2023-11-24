package nandonalt.mods.coralmod.client;

import nandonalt.mods.coralmod.CoralMod;
import nandonalt.mods.coralmod.Util;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import org.lwjgl.input.Keyboard;

import java.util.List;

public final class GuiCoralReef extends GuiScreen {

    /**
     * Array of strings corresponding to size values
     */
    private static final String[] SIZES = new String[] {
    	"small", "normal", "big", "custom"
    };

    /**
     * Parent GUI Screen
     */
    private final GuiScreen parentGuiScreen;

    public GuiCoralReef(GuiScreen parentGuiScreen) {
        this.parentGuiScreen = parentGuiScreen;
    }

    /**
     * Gets the description for button with specified index
     */
    private String getDesc(final int index) {
        final List<String> list = getSettings();
        final String desc;
        if(index >= 0 && index < list.size()) {
            desc = I18n.format("coralmod.settings." + list.get(index));
        } else {
            desc = I18n.format("coralmod.settings.unimplemented"); // fallback
        }
        return desc + ": " + getState(index);
    }

    /**
     * Gets the field for button with specified index
     */
    private String getField(int index) {
        final List<String> list = getSettings();
        final String field;

        if(index >= 0 && index < list.size()) {
            field = list.get(index);
        } else {
            field = "unknown"; // fallback
        }

        return field;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        final List<String> list = getSettings();
        for(int i = 0; i < list.size(); i++) {
            buttonList.add(new GuiOptionButton(i, width / 2 - 155 + i % 2 * 160, height / 6 + 24 * (i >> 1), getDesc(i)));
        }

        int j = buttonList.size();
        if(j % 2 == 1) j++;
        j += 2;

        final String s;
        if(isInGame()) {
            s = I18n.format("menu.returnToGame"); // native localization
        } else {
            s = I18n.format("gui.done"); // native localization
        }

        buttonList.add(new GuiButton(-1, width / 2 - 100, height / 6 + 24 * (j >> 1), s));
    }

    /**
     * Gets the current state of the button's setting
     */
    private String getState(int index) {
        final String field = getField(index);
        final String state = CoralMod.settingsManager.getValue("settings", field);
        
        final int size;
        try {
            size = Integer.parseInt(state);
        } catch (NumberFormatException nfe) {
            final Boolean bool = Boolean.parseBoolean(state);
            return I18n.format("options." + (bool ? "on" : "off"));
        }
        
        if(size >= 0 && size < SIZES.length) {
        	return I18n.format("coralmod.settings.avgsize." + SIZES[size]);
        } else {
        	Util.log(field + " has an unsupported value", true);
            return state;
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    @Override
    protected void keyTyped(char character, int key) {
        if (key == Keyboard.KEY_ESCAPE) CoralMod.settingsManager.updateSettings();
        super.keyTyped(character, key);
    }

    /**
     * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
     */
    @Override
    protected void actionPerformed(GuiButton guiButton) {
    	// return if button disabled
        if(!guiButton.enabled) return;

        // handle toggling if possible
        final List<String> list = getSettings();
        if(guiButton.id >= 0 && guiButton.id < list.size()) {
            CoralMod.settingsManager.toggle("settings", getField(guiButton.id));
            guiButton.displayString = getDesc(guiButton.id);
            return;
        }
        
        // otherwise close GUI
        if(isInGame()) {
        	mc.displayGuiScreen(null);
            mc.setIngameFocus();
        } else {
        	mc.displayGuiScreen(parentGuiScreen);
        }
        
        // update settings upon closing GUI
        CoralMod.settingsManager.updateSettings();
    }

    /**
     * Draws the screen and all the components in it.
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float renderPartialTicks) {
        drawDefaultBackground(); // draw the default background

        // FIXME: re-write when we introduce categories
        final String status;
        if(!isInGame()) {
            status = I18n.format("coralmod.settings.options");
        } else if(!mc.isSingleplayer()) {
            status = I18n.format("coralmod.settings.disabled");
        } else {
            status = I18n.format("coralmod.settings.enabled");
        }

        final String screenTitle = I18n.format("coralmod.settings.title") + " (" + status + ")";
        drawCenteredString(fontRendererObj, screenTitle, width / 2, 20, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, renderPartialTicks);
    }

    /**
     * Checks if the player is currently in-game
     */
    private boolean isInGame() {
        return parentGuiScreen == null;
    }

    /**
     * Returns a list of settings
     */
    private List<String> getSettings() {
        return CoralMod.settingsManager.getNames("settings");
    }

}
