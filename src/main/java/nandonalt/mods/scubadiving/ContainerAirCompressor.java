package nandonalt.mods.scubadiving;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public final class ContainerAirCompressor extends Container {

	private final TileEntityAirCompressor tileFurnace;
	private int lastCookTime = 0;
	private int lastBurnTime = 0;
	private int lastItemBurnTime = 0;

	public ContainerAirCompressor(InventoryPlayer inventory, TileEntityAirCompressor tileEntity) {
		this.tileFurnace = tileEntity;
		this.addSlotToContainer(new Slot(tileEntity, 0, 56, 17));
		this.addSlotToContainer(new Slot(tileEntity, 1, 56, 53));
		this.addSlotToContainer(new SlotFurnace(inventory.player, tileEntity, 2, 116, 35));

		for(int i = 0; i < 3; ++i) {
			for(int i2 = 0; i2 < 9; ++i2) {
				this.addSlotToContainer(new Slot(inventory, i2 + i * 9 + 9, 8 + i2 * 18, 84 + i * 18));
			}
		}

		for(int j = 0; j < 9; ++j) {
			this.addSlotToContainer(new Slot(inventory, j, 8 + j * 18, 142));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.aircompressorCookTime);
		crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.aircompressorBurnTime);
		crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.currentItemBurnTime);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for(int i = 0; i < this.crafters.size(); ++i) {
			final ICrafting crafting = (ICrafting)this.crafters.get(i);
			if(this.lastCookTime != this.tileFurnace.aircompressorCookTime) {
				crafting.sendProgressBarUpdate(this, 0, this.tileFurnace.aircompressorCookTime);
			}

			if(this.lastBurnTime != this.tileFurnace.aircompressorBurnTime) {
				crafting.sendProgressBarUpdate(this, 1, this.tileFurnace.aircompressorBurnTime);
			}

			if(this.lastItemBurnTime != this.tileFurnace.currentItemBurnTime) {
				crafting.sendProgressBarUpdate(this, 2, this.tileFurnace.currentItemBurnTime);
			}
		}

		this.lastCookTime = this.tileFurnace.aircompressorCookTime;
		this.lastBurnTime = this.tileFurnace.aircompressorBurnTime;
		this.lastItemBurnTime = this.tileFurnace.currentItemBurnTime;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		if(i == 0) this.tileFurnace.aircompressorCookTime = j;
		else if(i == 1) this.tileFurnace.aircompressorBurnTime = j;
		else if(i == 2) this.tileFurnace.currentItemBurnTime = j;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityPlayer) {
		return this.tileFurnace.isUseableByPlayer(entityPlayer);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
	 public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {
		final Slot slot = (Slot)this.inventorySlots.get(slotIndex);

		if (slot == null || !slot.getHasStack()) return null;

		final ItemStack stack = slot.getStack();
		final ItemStack originalStack = stack.copy();

		if (slotIndex == 2) {
			if (!this.mergeItemStack(stack, 3, 39, true)) return null;
			slot.onSlotChange(stack, originalStack);
		} else if (slotIndex != 1 && slotIndex != 0) {
			if (AirCompressorRecipes.instance().getSmeltingResult(stack) != null) {
				if (!this.mergeItemStack(stack, 0, 1, false)) return null;
			} else if (TileEntityAirCompressor.isItemFuel(stack)) {
				if (!this.mergeItemStack(stack, 1, 2, false)) return null;
			}
			else if (slotIndex >= 3 && slotIndex < 30) {
				if (!this.mergeItemStack(stack, 30, 39, false)) return null;
			} else if (slotIndex >= 30 && slotIndex < 39 && !this.mergeItemStack(stack, 3, 30, false)) {
				return null;
			}
		} else if (!this.mergeItemStack(stack, 3, 39, false)) {
			return null;
		}

		if (stack.stackSize == 0) {
			slot.putStack((ItemStack)null); // why the cast?
		} else {
			slot.onSlotChanged();
		}

		if (stack.stackSize == originalStack.stackSize) return null;

		slot.onPickupFromSlot(player, stack);

		return originalStack;
	 }

}
