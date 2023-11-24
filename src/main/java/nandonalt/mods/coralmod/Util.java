package nandonalt.mods.coralmod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import org.apache.logging.log4j.Level;

import cpw.mods.fml.common.FMLLog;

public final class Util {

	private Util() {
		throw new InstantiationError();
	}

	/**
	 * Coral-aware version of getBlockMetadata
	 * @param world world to get block metadata in
	 * @param x x position of block
	 * @param y y position of block
	 * @param z z position of block
	 * @return
	 */
	static int getCoralBlockMetadata(World world, int x, int y, int z) {
		final int metadata = world.getBlockMetadata(x, y, z);
		return (metadata < 8) ? metadata : metadata - 8;
	}

	/**
	 * Coral-aware version of setBlock
	 * @param world world to set block in
	 * @param x x position of block
	 * @param y y position of block
	 * @param z z position of block
	 * @param block block of block
	 * @param metadata metadata for block
	 * @param notify notify level
	 */
	static void setCoralBlock(World world, int x, int y, int z, Block block, int metadata, int notify) {
		// since this method is also called to set reef blocks (for now)
		final int coralMeta = (block instanceof BlockCoral && metadata < 8) ? metadata + 8 : metadata;
		world.setBlock(x, y, z, block, coralMeta, notify);
	}

	/**
	 * 'Safe' version of String.split
	 * @param string string to split
	 * @param regex regular expression to apply in splitting
	 * @return empty array if no elements, otherwise split string
	 */
	static String[] safeSplit(String string, String regex) {
		final String[] split = string.split(regex);
		return (split.length == 1 && split[0].length() == 0) ? new String[0] : split;
	}

	/**
	 * Checks if a block is water and if it's stationary
	 * @param block block to check
	 * @param stationary whether water block should be stationary
	 */
	static boolean checkWater(Block block, boolean stationary) {
		return checkWater(block) && block.func_149698_L() == stationary;
	}

	/**
	 * Checks if a block is water
	 * @param block block to check
	 */
	static boolean checkWater(Block block) {
		// if the block is any type of coral, it's not water
		return !(block instanceof BlockCoral) && block.getMaterial() == Material.water;
	}

	/**
	 * Log message w/ FML's logger, prefixing '[CoralMod] '
	 * @param s the message
	 * @param warning whether specified message is a warning
	 */
	public static void log(Level level, String s) {
		FMLLog.log(level, "[CoralMod] " + s);
	}
	
	/**
	 * Logs message as warning or info
	 * @param s the message
	 * @param warning whether or not it is a warning
	 */
	public static void log(String s, boolean warning) {
		log(warning ? Level.WARN : Level.INFO, s);
	}

	/**
	 * Logs specified message at INFO level
	 * @param s the message
	 */
	public static void log(String s) {
		log(s, false);
	}

	/**
	 * Logs a message with DEBUG level
	 * @param s the message
	 */
	public static void logDebug(String s) {
		//if(CoralMod.settingsManager.getBooleanValue("settings", "debug")) log(s);
		log(Level.DEBUG, s);
	}

}
