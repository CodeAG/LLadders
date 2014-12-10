package se.luppii.ladders.gui.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import se.luppii.ladders.LLadders;
import se.luppii.ladders.enums.OutputSide;
import se.luppii.ladders.inventory.ContainerLadderDispenser;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.packet.LLaddersMessage;
import se.luppii.ladders.tile.TileEntityLadderDispenser;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLadderDispenser extends GuiContainer {

	private static final ResourceLocation ladderDispenserTexture = new ResourceLocation(References.MOD_ID.toLowerCase(), "textures/gui/ladderdispenser.png");

	private TileEntityLadderDispenser te;
	
	private GuiButtonExt sideButton;
	
	private OutputSide side;
	
	public GuiLadderDispenser(InventoryPlayer inventoryPlayer, TileEntityLadderDispenser tileEntity) {

		super(new ContainerLadderDispenser(inventoryPlayer, tileEntity));
		this.te = tileEntity;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {

		String s = this.te.hasCustomInventoryName() ? this.te.getInventoryName() : I18n.format(this.te.getInventoryName(), new Object[0]);
		this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		this.fontRendererObj.drawString(I18n.format("container.inventory", new Object[0]), 8, this.ySize - 96 + 2, 4210752);
		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {

		this.mc.renderEngine.bindTexture(ladderDispenserTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
		
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		
		this.sideButton = new GuiButtonExt(0, x + 103, y + 68, 65, 12, this.te.getPlacement().toString());
		
		buttonList.add(this.sideButton);
		
		
	}
	
	protected void actionPerformed(GuiButton button) {
		switch (button.id) {
			case 0:
				this.te.setPlacement(this.te.getPlacement().next());
				this.sideButton.displayString = this.te.getPlacement().toString();
				
				//Create and send message to server
				LLaddersMessage message = new LLaddersMessage(te.xCoord, te.yCoord, te.zCoord, te.getPlacement());
				LLadders.snw.sendToServer(message);
		}
	}

}
