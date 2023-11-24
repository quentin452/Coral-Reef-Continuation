package nandonalt.mods.scubadiving;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public class TileEntityAirCompressor extends TileEntity implements IInventory {

	public int aircompressorBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int aircompressorCookTime = 0;
	private ItemStack[] aircompressorItemStacks = new ItemStack[3];

	@Override
	public int getSizeInventory() {
		return this.aircompressorItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int paramInt) {
		return this.aircompressorItemStacks[paramInt];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if(this.aircompressorItemStacks[slot] == null) return null;

		final ItemStack stack;
		if(this.aircompressorItemStacks[slot].stackSize <= size) {
			stack = this.aircompressorItemStacks[slot];
			this.aircompressorItemStacks[slot] = null;
			return stack;
		}

		stack = this.aircompressorItemStacks[slot].splitStack(size);
		if(this.aircompressorItemStacks[slot].stackSize == 0) {
			this.aircompressorItemStacks[slot] = null;
		}
		return stack;
	}

	/**
	 * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
	 * like when you close a workbench GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (this.aircompressorItemStacks[slot] != null) {
			ItemStack var2 = this.aircompressorItemStacks[slot];
			this.aircompressorItemStacks[slot] = null;
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		this.aircompressorItemStacks[slot] = stack;
		if(stack != null && stack.stackSize > this.getInventoryStackLimit()) {
			stack.stackSize = this.getInventoryStackLimit();
		}

	}

	@Override
	public String getInventoryName() {
		return "container.aircompressor"; // Air Compressor
	}

	@Override
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		super.readFromNBT(nbtTagCompound);
		final NBTTagList itemsList = nbtTagCompound.getTagList("Items", 10);
		this.aircompressorItemStacks = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < itemsList.tagCount(); ++i) {
			final NBTTagCompound itemTag = (NBTTagCompound)itemsList.getCompoundTagAt(i);
			final byte j = itemTag.getByte("Slot");
			if(j >= 0 && j < this.aircompressorItemStacks.length) {
				this.aircompressorItemStacks[j] = ItemStack.loadItemStackFromNBT(itemTag);
			}
		}

		this.aircompressorBurnTime = nbtTagCompound.getShort("BurnTime");
		this.aircompressorCookTime = nbtTagCompound.getShort("CookTime");
		this.currentItemBurnTime = this.getItemBurnTime(this.aircompressorItemStacks[1]);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbtTagCompound) {
		super.writeToNBT(nbtTagCompound);
		nbtTagCompound.setShort("BurnTime", (short)this.aircompressorBurnTime);
		nbtTagCompound.setShort("CookTime", (short)this.aircompressorCookTime);
		final NBTTagList itemsList = new NBTTagList();

		for(int i = 0; i < this.aircompressorItemStacks.length; ++i) {
			if(this.aircompressorItemStacks[i] != null) {
				final NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte)i);
				this.aircompressorItemStacks[i].writeToNBT(itemTag);
				itemsList.appendTag(itemTag); // was setTag
			}
		}

		nbtTagCompound.setTag("Items", itemsList);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	public int getCookProgressScaled(int paramInt) {
		return this.aircompressorCookTime * paramInt / 125;
	}

	public int getBurnTimeRemainingScaled(int paramInt) {
		if(this.currentItemBurnTime == 0) {
			this.currentItemBurnTime = 150;
		}

		return this.aircompressorBurnTime * paramInt / this.currentItemBurnTime;
	}

	public boolean isBurning() {
		return this.aircompressorBurnTime > 0;
	}

	// TODO: cleanup (optimally, we would put body code in a method that returns dirty)
	@Override
	public void updateEntity() {
		boolean burning = this.aircompressorBurnTime > 0;
		boolean dirty = false;
		if(this.aircompressorBurnTime > 0) {
			--this.aircompressorBurnTime;
		}

		if(!this.worldObj.isRemote) {
			if(this.aircompressorBurnTime == 0 && this.canSmelt()) {
				this.currentItemBurnTime = this.aircompressorBurnTime = this.getItemBurnTime(this.aircompressorItemStacks[1]);
				if(this.aircompressorBurnTime > 0) {
					dirty = true;
					if(this.aircompressorItemStacks[1] != null) {
						--this.aircompressorItemStacks[1].stackSize;

						if(this.aircompressorItemStacks[1].stackSize == 0) {
							final Item item = this.aircompressorItemStacks[1].getItem().getContainerItem();
							this.aircompressorItemStacks[1] = null;
						}
					}
				}
			}

			if(this.isBurning() && this.canSmelt()) {
				++this.aircompressorCookTime;
				if(this.aircompressorCookTime == 125) {
					this.aircompressorCookTime = 0;
					this.smeltItem();
					dirty = true;
				}
			} else {
				this.aircompressorCookTime = 0;
			}

			if(dirty != this.aircompressorBurnTime > 0) {
				dirty = true;
			}
		}

		if(dirty) {
			this.markDirty();
		}

	}

	private boolean canSmelt() {
		if(this.aircompressorItemStacks[0] == null) return false;
		final ItemStack stack = AirCompressorRecipes.instance().getSmeltingResult(this.aircompressorItemStacks[0]);
		if(stack == null) return false;
		if(aircompressorItemStacks[2] == null) return true;
		if(!this.aircompressorItemStacks[2].isItemEqual(stack)) return false;
		if(this.aircompressorItemStacks[2].stackSize < this.getInventoryStackLimit()) {
			if(this.aircompressorItemStacks[2].stackSize < this.aircompressorItemStacks[2].getMaxStackSize()) {
				return true;
			}
		}
		return this.aircompressorItemStacks[2].stackSize < stack.getMaxStackSize();
	}

	public void smeltItem() {
		if(!this.canSmelt()) return;

		final ItemStack localgm = AirCompressorRecipes.instance().getSmeltingResult(this.aircompressorItemStacks[0]);
		if(this.aircompressorItemStacks[2] == null) {
			this.aircompressorItemStacks[2] = localgm.copy();
		} else if(this.aircompressorItemStacks[2].getItem() == localgm.getItem()) {
			++this.aircompressorItemStacks[2].stackSize;
		}

		--this.aircompressorItemStacks[0].stackSize;

		if(this.aircompressorItemStacks[0].stackSize <= 0) {
			this.aircompressorItemStacks[0] = null;
		}
	}

	public static int getItemBurnTime(ItemStack stack) {
		// we can only 'burn' with redstone
		return (stack == null) ? 0 : (stack.getItem() == Items.redstone) ? 300 : 0;
	}

	public static boolean isItemFuel(ItemStack itemStack) {
		return getItemBurnTime(itemStack) > 0;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if(this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this) return false;
		return player.getDistanceSq(this.xCoord + 0.5D, this.yCoord + 0.5D, this.zCoord + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasCustomInventoryName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int arg0, ItemStack arg1) {
		// TODO Auto-generated method stub
		return false;
	}
}
