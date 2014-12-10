package se.luppii.ladders.tile;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityMachineBase extends TileEntity {

	private ForgeDirection facingDirection;

	private boolean active = false;

	private static final int[][] texturePattern = new int[][] { { 2, 3, 0, 0, 0, 0 }, // D
			{ 3, 2, 0, 0, 0, 0 }, // U
			{ 0, 1, 2, 3, 4, 5 }, // N
			{ 0, 1, 3, 2, 5, 4 }, // S
			{ 0, 1, 5, 4, 2, 3 }, // W
			{ 0, 1, 4, 5, 3, 2 }, // E
	};

	public ForgeDirection getFacingDirection() {

		return facingDirection != null ? facingDirection : ForgeDirection.NORTH;
	}

	public int getForgeDirectionToInt(ForgeDirection dir) {

		ForgeDirection[] directions =  ForgeDirection.VALID_DIRECTIONS;
		for (int i = 0; i < directions.length; i++) {
			if (directions[i] == dir) {
				switch (i) {
					case 2:
						return 3;
					case 3:
						return 1;
					case 4:
						return 2;
					case 5:
						return 0;
				}
			}
		}
		return 0;
	}

	public int getRotatedSide(int side) {

		if (facingDirection == null)
			facingDirection = ForgeDirection.NORTH;
		return texturePattern[facingDirection.ordinal()][side];
	}

	public void setFacingDirection(int side) {

		facingDirection = ForgeDirection.getOrientation(side);
		if (worldObj != null) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	public boolean getActiveState() {

		return active;
	}

	public void setActiveState(boolean isActive) {

		if (active != isActive && worldObj != null && !worldObj.isRemote) {
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		active = isActive;
	}

	@Override
	public Packet getDescriptionPacket() {

		NBTTagCompound nbt = new NBTTagCompound();
		//nbt.setInteger("facing", this.getFacingDirection().ordinal());
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, nbt);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

		this.readFromNBT(pkt.func_148857_g());
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {

		super.readFromNBT(par1NBTTagCompound);
		setFacingDirection(par1NBTTagCompound.getInteger("facing"));
		setActiveState(par1NBTTagCompound.getBoolean("active"));
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {

		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setInteger("facing", getFacingDirection().ordinal());
		par1NBTTagCompound.setBoolean("active", active);
	}

	@SideOnly(Side.CLIENT)
	public Object getGui(InventoryPlayer inventoryPlayer) {

		return null;
	}
}
