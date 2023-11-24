package nandonalt.mods.coralmod.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import nandonalt.mods.coralmod.CommonProxy;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

public final class ClientProxy extends CommonProxy {

	@Override
	protected void clientSetup() {
		// instantiate and register key binding
        final KeyBinding binding = new KeyBinding("coralmod.key.gui", Keyboard.KEY_C, "coralmod.settings.title");
        ClientRegistry.registerKeyBinding(binding);
        
        // instantiate and register key handler
        final CoralKeyHandler keyHandler = new CoralKeyHandler(binding);
        FMLCommonHandler.instance().bus().register(keyHandler);
	}

}