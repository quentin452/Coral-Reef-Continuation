package nandonalt.mods.scubadiving;

import nandonalt.mods.scubadiving.client.GuiAirCompressor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ScubaGuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(id != 1) return null;
		
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity == null || !(tileEntity instanceof TileEntityAirCompressor)) return null;
		
		final TileEntityAirCompressor teac = (TileEntityAirCompressor)tileEntity;
		return new ContainerAirCompressor(player.inventory, teac);
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if(id != 1) return null;
		
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity == null || !(tileEntity instanceof TileEntityAirCompressor)) return null;
		
		final TileEntityAirCompressor teac = (TileEntityAirCompressor)tileEntity;
		return new GuiAirCompressor(player.inventory, teac);
	}

}
