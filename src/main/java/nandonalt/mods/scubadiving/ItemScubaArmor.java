package nandonalt.mods.scubadiving;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ItemScubaArmor extends ItemArmor {

	private static final String[] types = new String[] {"ScubaHelmet", "ScubaTank"};
	
	private static final ResourceLocation helmetTexPath = new ResourceLocation("scubadiving:textures/gui/scubahelmet.png");
	
	public ItemScubaArmor(ArmorMaterial material, int renderIndex, int renderType) {
		super(material, renderIndex, renderType);
	}

	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		// FIXME: (?) assumes armorType is a safe value
		return "scubadiving:textures/armor/" + types[armorType] + "_1.png";
	}
	
	// prevent scuba helmet from being enchanted
	@Override
	public int getItemEnchantability() {
		return (this == ScubaDiving.scubaHelmet) ? 0 : super.getItemEnchantability();
	}
	
	// Forge-specific
	@Override
	@SideOnly(Side.CLIENT)
	public void renderHelmetOverlay(ItemStack stack, EntityPlayer player, ScaledResolution res, float partialTicks, boolean hasScreen, int mouseX, int mouseY) {
		// only render helmet overlay for helmet :)
		if(stack.getItem() != ScubaDiving.scubaHelmet) return;
		// FIXME: no way to disable rendering...
        renderHelmet(Minecraft.getMinecraft(), res.getScaledWidth(), res.getScaledHeight());
	}
	
	// based upon pumpkin blur overlay code
	@SideOnly(Side.CLIENT)
    private void renderHelmet(Minecraft mc, double width, double height) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        mc.getTextureManager().bindTexture(helmetTexPath);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0.0D, height, -90.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(width, height, -90.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(width, 0.0D, -90.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, -90.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
	
}
