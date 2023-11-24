package nandonalt.mods.coralmod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public final class BlockReef extends Block {

	final int type;

	@SideOnly(Side.CLIENT)
	private IIcon[] iconBuffer;

	BlockReef(int type) {
		super(Material.rock);
		this.type = type;
		setCreativeTab(CreativeTabs.tabBlock);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconBuffer = new IIcon[] {
			iconRegister.registerIcon("coralmod:reef1"),
			iconRegister.registerIcon("coralmod:reef2")
		};
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int metadata) {
		return (type < 0 || type >= iconBuffer.length) ? iconBuffer[0] : iconBuffer[type];
	}

}
