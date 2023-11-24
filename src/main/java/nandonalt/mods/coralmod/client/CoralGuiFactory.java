package nandonalt.mods.coralmod.client;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import cpw.mods.fml.client.IModGuiFactory;

public class CoralGuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft paramMinecraft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiCoralReef.class;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RuntimeOptionGuiHandler getHandlerFor(
			RuntimeOptionCategoryElement paramRuntimeOptionCategoryElement) {
		// TODO Auto-generated method stub
		return null;
	}

}
