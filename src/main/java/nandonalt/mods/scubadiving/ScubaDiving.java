package nandonalt.mods.scubadiving;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import nandonalt.mods.nandocore.ModSettings;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "scubadiving", name="Scuba Diving", version="1.7.10_dev")

public final class ScubaDiving {

	/**
	 * Mod instance
	 */
	@Instance("scubadiving")
	public static ScubaDiving instance;

	/**
	 * Directory for storing configuration
	 */
	private File configDir;

	public static ModSettings settingsManager;
	
	static Block airCompressor;
	
	static Item scubaHelmet, scubaTank, scubaTankEmpty;
	
	/**
	 * Pre-initialization
	 */
	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		// Set configuration directory
		configDir = new File(evt.getModConfigurationDirectory(), "scubadiving");
		
		// Register blocks
		airCompressor = new BlockAirCompressor().setHardness(0.5F).setStepSound(Block.soundTypeStone).setBlockName("scubadiving.AirCompressor");
		GameRegistry.registerBlock(airCompressor, "AirCompressor");
		GameRegistry.registerTileEntity(TileEntityAirCompressor.class, "AirCompressor");
		
		// Register items - scuba armor is considered iron
		scubaHelmet = new ItemScubaArmor(ArmorMaterial.IRON, 0, 0).setUnlocalizedName("scubadiving.ScubaHelmet").setTextureName("scubadiving:scubahelmet");
		scubaTank = new ItemScubaArmor(ArmorMaterial.IRON, 0, 1).setUnlocalizedName("scubadiving.ScubaTank").setTextureName("scubadiving:scubatank");
		scubaTankEmpty = new ItemScubaArmor(ArmorMaterial.IRON, 0, 1).setUnlocalizedName("scubadiving.ScubaTankEmpty").setTextureName("scubadiving:scubatank");
		GameRegistry.registerItem(scubaHelmet, "ScubaHelmet");
		GameRegistry.registerItem(scubaTank, "ScubaTank");
		GameRegistry.registerItem(scubaTankEmpty, "ScubaTankEmpty");
		
		// Add recipes
		GameRegistry.addRecipe(new ItemStack(scubaHelmet), "XXX", "LGL", "XXX", 'X', Items.iron_ingot, 'L', Items.leather, 'G', Blocks.glass);
		GameRegistry.addRecipe(new ItemStack(scubaTankEmpty), "X X", "XLX", "X X", 'X', Items.iron_ingot, 'L', Items.leather);
		GameRegistry.addRecipe(new ItemStack(airCompressor), "XXX", "L L", "XXX", 'X', Items.iron_ingot, 'L', Items.redstone);
		
		// Add GUI handler
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, new ScubaGuiHandler());
	}
	
	/**
	 * Initialization
	 */
	@EventHandler
	public void init(FMLInitializationEvent evt) {
		// Experimental settings stuff
		final Configuration config = new Configuration(new File(configDir, "settings.cfg"));
		settingsManager = new ModSettings(config);
		final List<ModSettings.LocalProperty> settings = new LinkedList<ModSettings.LocalProperty>();
		settings.add(new ModSettings.IntProperty("airdrainmode", 0, 0, 2));
		settingsManager.register("settings", settings);
		settingsManager.loadSettings();
		
		// Register our tick handler
		FMLCommonHandler.instance().bus().register(new Ticker());
	}

}
