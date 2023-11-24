package nandonalt.mods.scubadiving.client;

import nandonalt.mods.scubadiving.ContainerAirCompressor;
import nandonalt.mods.scubadiving.TileEntityAirCompressor;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAirCompressor extends GuiContainer {

	private static final ResourceLocation compressorGuiTextures = new ResourceLocation("scubadiving:textures/gui/aircompressor.png");
	private final TileEntityAirCompressor aircompressorInventory;

   public GuiAirCompressor(InventoryPlayer inventoryplayer, TileEntityAirCompressor tileentityaircompressor) {
      super(new ContainerAirCompressor(inventoryplayer, tileentityaircompressor));
      this.aircompressorInventory = tileentityaircompressor;
   }

   @Override
   protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
      this.fontRendererObj.drawString(I18n.format("scubadiving.container.aircompressor"), 60, 6, 0x404040);
      this.fontRendererObj.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 0x404040);
   }

   @Override
   protected void drawGuiContainerBackgroundLayer(float f, int p_146976_2_, int p_146976_3_) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(compressorGuiTextures);
      int x = (this.width - this.xSize) / 2;
      int y = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
      int i1;
      if(this.aircompressorInventory.isBurning()) {
         i1 = this.aircompressorInventory.getBurnTimeRemainingScaled(12);
         this.drawTexturedModalRect(x + 56, y + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 2);
      }

      i1 = this.aircompressorInventory.getCookProgressScaled(24);
      this.drawTexturedModalRect(x + 79, y + 34, 176, 14, i1 + 1, 16);
   }
}
