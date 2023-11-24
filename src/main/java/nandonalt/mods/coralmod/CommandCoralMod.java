package nandonalt.mods.coralmod;

import cpw.mods.fml.common.Mod;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static net.minecraft.util.EnumChatFormatting.GREEN;
import static net.minecraft.util.EnumChatFormatting.WHITE;

final class CommandCoralMod extends CommandBase {

	@Override
	public String getCommandName() {
		return "coralmod";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		// if no arguments specified, print mod name + version and return
		if(args.length == 0) {
			final Mod annotation = CoralMod.class.getAnnotation(Mod.class);
			sendChatToPlayer(sender, annotation.name() + ", v" + annotation.version());
			return;
		}

		if(args[0].equals("biomes")) {
			// list biomes
			final String s;
			if(CoralMod.settingsManager.getBooleanValue("settings", "oceanonly")) {
				s = "Ocean only";
			} else {
				final String biomes = CoralMod.settingsManager.getValue("generation", "biomes");
				final String[] biomesArray = Util.safeSplit(biomes, ",");

				s = (biomesArray.length == 0) ? "All" : biomes;
			}
			sendChatToPlayer(sender, "Biomes: " + s);
		} else if(args[0].equals("regen")) {
			// attempt to (re-)generate coral
			final EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			final Random random = new Random(player.worldObj.getSeed());
			final int posX = MathHelper.floor_double(player.posX);
			final int posZ = MathHelper.floor_double(player.posZ);
			final int chunkX = posX >> 4; final int chunkZ = posZ >> 4;
			final long i = random.nextLong() / 2L * 2L + 1L;
			final long j = random.nextLong() / 2L * 2L + 1L;
			random.setSeed((long)chunkX * i + (long)chunkZ * j ^ player.worldObj.getSeed());
			if(CoralGenerator.generate(random, posX, posZ, player.worldObj)) {
				sendChatToPlayer(sender, "Re-generated coral at: " + chunkX + ", " + chunkZ);
			} else {
				sendChatToPlayer(sender, "Couldn't generate coral at: " + chunkX + ", " + chunkZ);
			}
		} else if(args[0].equals("settings")) {
			// list settings
			sendChatToPlayer(sender, GREEN + "===CoralMod Settings===");
			for(String setting : CoralMod.settingsManager.getNames("settings")) {
				sendChatToPlayer(sender, GREEN + setting + ": " + WHITE + getSettingsValue(setting));
			}
		} else if(args[0].equals("gensettings")) {
			// list generation settings
			sendChatToPlayer(sender, GREEN + "===CoralGen Settings===");
			for(String setting : CoralMod.settingsManager.getNames("generation")) {
				final String s = CoralMod.settingsManager.getValue("generation", setting);
				sendChatToPlayer(sender, GREEN + setting + ": " + WHITE + s);
			}
		} else if(args[0].equals("test")) {
			// run tests
			final EntityPlayerMP player = getCommandSenderAsPlayer(sender);
			final ItemStack stack = player.getCurrentEquippedItem();
			if(stack != null) {
				if(stack.getItem() instanceof ItemCoral) {
					sendChatToPlayer(sender, stack.toString());
				} else if(stack.getItem() instanceof ItemBlock) {
					ItemBlock itemBlock = (ItemBlock)stack.getItem();
					// hopefully this is named in next MCP release
					if(itemBlock.field_150939_a instanceof BlockReef) sendChatToPlayer(sender, stack.toString());
				}
			}
			
			final int x = MathHelper.floor_double(player.posX);
			final int y = MathHelper.floor_double(player.posY);
			final int z = MathHelper.floor_double(player.posZ);
			final Block block = player.worldObj.getBlock(x, y, z);
			if(block instanceof BlockCoral) {
				sendChatToPlayer(sender, "BlockCoral [meta" + player.worldObj.getBlockMetadata(x, y, z) + "]");
			}
			final Block block2 = player.worldObj.getBlock(x, y - 1, z);
			if(block2 instanceof BlockReef) {
				final int meta = player.worldObj.getBlockMetadata(x, y - 1, z);
				sendChatToPlayer(sender, "BlockReef(" + ((BlockReef)block2).type + ") [meta" + meta + "]");
			}
		} else {
			// assume user is attempting to view or toggle a setting
			final String setting;
			final boolean toggle;
			if(args[0].equals("toggle")) {
				if(args.length < 2) throw new WrongUsageException("commands.coralmod.usage");
				setting = args[1];
				toggle = true;
			} else {
				setting = args[0];
				toggle = false;
			}

			// user did not specify a valid setting name
			if(!CoralMod.settingsManager.getNames("settings").contains(setting)) {
				throw new WrongUsageException("commands.coralmod.usage");
			}

			if(!toggle) {
				sendChatToPlayer(sender, setting + ": " + getSettingsValue(setting));
				return;
			}
			
			final boolean toggled = CoralMod.settingsManager.toggle("settings", setting);

			if(!toggled) {
				sendChatToPlayer(sender, "Couldn't toggle " + setting);
				return;
			}

			sendChatToPlayer(sender, setting + ": " + getSettingsValue(setting) + " (toggled)");
			CoralMod.settingsManager.updateSettings();
		}
	}

	private String getSettingsValue(String s) {
		return CoralMod.settingsManager.getValue("settings", s);
	}

	private void sendChatToPlayer(ICommandSender sender, String msg) {
		sender.addChatMessage(new ChatComponentText(msg));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if(args.length == 1) return null;
		final List<String> tabCompletionOptions = new ArrayList<String>();
		tabCompletionOptions.addAll(CoralMod.settingsManager.getNames("settings"));
		Collections.sort(tabCompletionOptions);
		tabCompletionOptions.add(0, "biomes");
		tabCompletionOptions.add(1, "regen");
		tabCompletionOptions.add(2, "settings");
		return getListOfStringsFromIterableMatchingLastWord(args, tabCompletionOptions);
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.coralmod.usage";
	}

	/*@Override
	public int compareTo(Object o) {
		return compareTo((ICommand)o);
	}*/
}