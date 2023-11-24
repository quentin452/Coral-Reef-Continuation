package nandonalt.mods.scubadiving;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public final class BlockAirCompressor extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon sideIcon;
	
	// limit instantiation to this package
	BlockAirCompressor() {
		super(Material.iron);
	}

	// update metadata when block is placed
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.updateMetadata(world, x, y, z);
	}

	// set block metadata based on opaque-ness of surrounding blocks
	private void updateMetadata(World world, int x, int y, int z) {
		final Block i = world.getBlock(x, y, z - 1);
		final Block j = world.getBlock(x, y, z + 1);
		final Block k = world.getBlock(x - 1, y, z);
		final Block m = world.getBlock(x + 1, y, z);
		int n = 3;

		if(i.func_149730_j() && !j.func_149730_j()) {
			n = 3;
		}

		if(j.func_149730_j() && !i.func_149730_j()) {
			n = 2;
		}

		if(k.func_149730_j() && !m.func_149730_j()) {
			n = 5;
		}

		if(m.func_149730_j() && !k.func_149730_j()) {
			n = 4;
		}

		world.setBlockMetadataWithNotify(x, y, z, n, 2);
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		// FIXME: WIP, should take metadata into account
		if(side == 2) return this.blockIcon;
		return this.sideIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.blockIcon = iconRegister.registerIcon("scubadiving:aircompressor");
		this.sideIcon = iconRegister.registerIcon("scubadiving:aircompressorside");
		// TODO: make 'clean' ver. of icon (?) (for top + bottom)
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity != null && tileEntity instanceof TileEntityAirCompressor) {
			if(!((TileEntityAirCompressor)tileEntity).isBurning()) return;
			final int meta = world.getBlockMetadata(x, y, z);
			float f1 = x + 0.5F;
			float f2 = y + 0.0F + random.nextFloat() * 9.0F / 22.0F;
			float f3 = z + 0.5F;
			float f4 = 0.52F;
			float f5 = random.nextFloat() * 0.6F - 0.3F;
			if(meta == 4) {
				world.spawnParticle("explode", (double)(f1 - f4), (double)f2, (double)(f3 + f5), 0.0D, 0.0D, 0.0D);
			} else if(meta == 2) {
				world.spawnParticle("explode", (double)(f1 + f5), (double)f2, (double)(f3 - f4), 0.0D, 0.0D, 0.0D);
			}
		}
	}

	/**
	 * Called upon block activation (right click on the block). Args : world, x, y, z, player, side, hitX, hitY, hitZ.
	 * Return : Swing hand (client), abort the block placement (server)
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		if(world.isRemote) {
			return true;
		} else {
			// we don't actually need to check that there is an associated tile entity..
			// that's not to say it's a bad idea ;P
			TileEntityAirCompressor teac = (TileEntityAirCompressor)world.getTileEntity(x, y, z);
			if(teac != null) player.openGui(ScubaDiving.instance, 1, world, x, y, z);
			return true;
		}
	}

	/**
	 * Update which block the compressor is using depending on whether or not it is active
	 */
	private void updateCompressorBlockState(boolean paramBoolean, World world, int x, int y, int z) {
		final int meta = world.getBlockMetadata(x, y, z);
		world.setBlockMetadataWithNotify(x, y, z, meta, 2);
		final TileEntity tileEntity = world.getTileEntity(x, y, z);
		if(tileEntity == null) return;
		tileEntity.validate();
		world.setTileEntity(x, y, z, tileEntity);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int p_149915_2_) {
		return new TileEntityAirCompressor();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		final int i = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		
		final int n = (i == 0) ? 2 : (i == 1) ? 5 : (i == 2) ? 3 : (i == 3) ? 4 : -1;
		if(n == -1) return;
		
		world.setBlockMetadataWithNotify(x, y, z, n, 2);
	}
}
