package nandonalt.mods.scubadiving;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class AirCompressorRecipes {

   private static final AirCompressorRecipes compressorBase = new AirCompressorRecipes();
   
   /** The list of compressor results. */
   private final Map<ItemStack, ItemStack> compressorList = new HashMap<ItemStack, ItemStack>();

   public static final AirCompressorRecipes instance() {
      return compressorBase;
   }

	private AirCompressorRecipes() {
	   addSmelting(ScubaDiving.scubaTankEmpty, new ItemStack(ScubaDiving.scubaTank), 0.1F);
	}
   
	public void addSmelting(Block block, ItemStack destStack, float experience) {
		this.addSmelting(Item.getItemFromBlock(block), destStack, experience);
	}

	public void addSmelting(Item item, ItemStack destStack, float experience) {
		this.addSmelting(new ItemStack(item, 1, 32767), destStack, experience);
	}

	public void addSmelting(ItemStack srcStack, ItemStack destStack, float experience) {
		compressorList.put(srcStack, destStack);
		// we don't currently give any experience
		// this.experienceList.put(p_151394_2_, Float.valueOf(p_151394_3_));
	}

   /**
    * Returns the smelting result of an item.
    */
   public ItemStack getSmeltingResult(ItemStack srcStack) {
       final Iterator<Entry<ItemStack, ItemStack>> iterator = compressorList.entrySet().iterator();
       Entry<ItemStack, ItemStack> entry;

       do {
           if (!iterator.hasNext()) return null;

           entry = iterator.next();
       } while (!this.validateResult(srcStack, entry.getKey()));

       return entry.getValue();
   }

   private boolean validateResult(ItemStack srcStack, ItemStack destStack) {
	   if(destStack.getItem() != srcStack.getItem()) return false;
	   
	   return (destStack.getItemDamage() == 32767 || destStack.getItemDamage() == srcStack.getItemDamage());
   }
   
}
