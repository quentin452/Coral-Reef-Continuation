package nandonalt.mods.coralmod;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public final class ItemCoral extends ItemBlock {

	private static final String[] names = new String[] {
		"orange", "green", "purple", "pink", "spiky"
	};

	@SideOnly(Side.CLIENT)
	private IIcon[] iconBuffer;

	public ItemCoral(final Block block) {
		super(block);
		setMaxDamage(0);  // prevent item from being damaged
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int itemDamage) {
		return itemDamage;
	}

	@Override
	public int getSpriteNumber() {
		return 1; // index for item textures
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		final String name = super.getUnlocalizedName();
		final int damage = stack.getItemDamage();
		if(damage >= 0 && damage < names.length) return name + "." + names[damage];
		return name;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister) {
		iconBuffer = new IIcon[BlockCoral.types.length];

		for (int i = 0; i < BlockCoral.types.length; i++) {
			iconBuffer[i] = iconRegister.registerIcon("coralmod:" + BlockCoral.types[i]);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIconFromDamage(int damageValue) {
		final IIcon sprite;
		if(damageValue < 0 || damageValue >= iconBuffer.length) {
			sprite = iconBuffer[0];
		} else {
			sprite = iconBuffer[damageValue];
		}

		return sprite;
	}

}
