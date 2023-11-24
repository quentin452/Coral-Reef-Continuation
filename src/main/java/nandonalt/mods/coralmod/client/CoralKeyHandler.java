package nandonalt.mods.coralmod.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public final class CoralKeyHandler  {

    private final KeyBinding binding;

    CoralKeyHandler(KeyBinding binding) {
        this.binding = binding;
    }

    @SubscribeEvent
    public void keyInput(InputEvent.KeyInputEvent event) {
        if(!binding.getIsKeyPressed()) return;

        final Minecraft game = Minecraft.getMinecraft();
        if(game.currentScreen == null) game.displayGuiScreen(new GuiCoralReef(null));
    }
}