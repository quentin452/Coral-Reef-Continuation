package nandonalt.mods.scubadiving;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;

public final class Ticker {

	@SubscribeEvent
	public void playerTick(TickEvent.PlayerTickEvent evt) {
		// client just draws the fancy gui ^^
		if(evt.phase != Phase.END || evt.side == Side.CLIENT) return;

		// check for scuba helmet
		final ItemStack stack = evt.player.inventory.armorInventory[3];
		if(stack == null || stack.getItem() != ScubaDiving.scubaHelmet) return;

		// check for scuba tank
		final ItemStack tank = evt.player.inventory.armorInventory[2];
		if(tank == null || tank.getItem() != ScubaDiving.scubaTank) {
			// limit air if wearing helmet but no tank / empty tank
			if(evt.player.getAir() >= 11) evt.player.setAir(10);
			return;
		}

		evt.player.setAir(300); // *was* 299...

		// get air drain mode - FIXME: add customization; appropriate values
		final int mode1 = ScubaDiving.settingsManager.getIntValue("settings", "airdrainmode");
		final int airdrain;
		if(mode1 == 0) {
			airdrain = 20; // actually a sane value.
		} else if (mode1 == 1) {
			airdrain = 250;
		} else if (mode1 == 2) {
			airdrain = 1; // placeholder
		} else {
			airdrain = 100;
		}
		
		// ensure tank has an NBT tag (we use it to track air drain)
		if(tank.stackTagCompound == null) tank.stackTagCompound = new NBTTagCompound();
		final NBTTagCompound tag = tank.stackTagCompound;
		
		// increase drain by one
		short counter = tag.getShort("airdrain");
		counter++;
		
		// check drain value
		if(counter == airdrain) {
			tank.damageItem(1, evt.player);
			if(tank.stackSize == 0) {
				//evt.player.inventory.armorInventory[2] = new ItemStack(ScubaDiving.scubaTankEmpty);
				tank.func_150996_a(ScubaDiving.scubaTankEmpty);
				tank.stackSize = 1;
			}
			counter = 0;
		}
		
		// update value on item
		tag.setShort("airdrain", counter);
	}

}
