package se.luppii.ladders.tile;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import se.luppii.ladders.LLadders;
import se.luppii.ladders.block.BlockGenericLadder;
import se.luppii.ladders.enums.OutputSide;
import se.luppii.ladders.lib.References;

public class TileEntityLadderDispenser extends TileEntityMachineBase implements ISidedInventory {

	private ItemStack[] inventory;

	private String name;

	private int mode;

	private int ticks;

	private boolean working;
	
	private OutputSide placement;

	public TileEntityLadderDispenser() {

		this.name = "Ladder Dispenser";
		this.inventory = new ItemStack[getSizeInventory()];
		this.mode = 0;
		this.placement = OutputSide.UPDOWN;
	}
	
	public void setPlacement(OutputSide side) {
		this.placement = side;
	}
	
	public OutputSide getPlacement() {
		return this.placement;
	}

	public int getMode() {

		return mode;
	}

	public boolean isWorking() {

		return working;
	}

	public void setIsWorking(boolean work) {

		working = work;
	}

	public void setMode(int par1) {

		mode = par1;
	}

	@Override
	public int getSizeInventory() {

		return 5;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return inventory[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {

		if (inventory[i] != null) {
			if (inventory[i].stackSize <= j) {
				ItemStack itemstack = inventory[i];
				setInventorySlotContents(i, null);
				return itemstack;
			}
			ItemStack itemstack1 = inventory[i].splitStack(j);
			if (inventory[i].stackSize <= 0) {
				inventory[i] = null;
			}
			markDirty();
			return itemstack1;
		}
		else {
			markDirty();
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {

		ItemStack slotContent = this.getStackInSlot(i);
		if (slotContent != null) {
			setInventorySlotContents(i, null);
		}
		return slotContent;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {

		inventory[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
		// If slot changed is 4 we need to mark the block to update as the texture used has most likely changed.
		if (i == 4)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	@Override
	public void updateEntity() {

		if (working && !worldObj.isRemote) { // Important to not run code on clients, block placement will be bugged for clients otherwise.
			ticks++;
			if (getActiveState()) {
				boolean did_work = false;
				if ((mode == 1 || mode == 2) && ticks == 6) { // Place ladders. Mode 1 is up, mode 2 is down ladder placement.
					ticks = 0;
					int direction = getForgeDirectionToInt(getFacingDirection());
					
					for (int i = 0; i < 2; i++) {
						
						for (int slot = 0; slot < getSizeInventory() - 1; slot++) {
							ItemStack stack = getStackInSlot(slot);
							
							if (stack != null) { // If stack contain items.
								BlockGenericLadder ladder;
								try {
									ladder = (BlockGenericLadder)Block.getBlockFromItem(stack.getItem());
								} catch (Exception err) {
									FMLLog.warning("[" + References.MOD_NAME + "] LadderDispenser found Block that is not any type of Luppis Ladders Ladder.");
									return;
								}
								if (ladder.isModeConforming(mode)) { // If block in slot is the same as the ladder we are trying to place - continue.
									
									//Make all calculations on where we are trying to place our ladder
									int[] offsets = this.calcOffsets(ladder);
									int xOffset = offsets[0];
									int vertDir = offsets[1];
									int zOffset = offsets[2];
									
									if (this.getPlacement() != OutputSide.UPDOWN) {
											vertDir += -ladder.getDirection();
										
									}
									
									FMLLog.info("[" + References.MOD_NAME + "] Trying to place ladder at [" + (xCoord + xOffset) + "," + (yCoord + vertDir) + "," + (zCoord + zOffset) + "]");
									if (this.canSetLadder((BlockGenericLadder)Block.getBlockFromItem(stack.getItem()), xCoord + xOffset, yCoord + vertDir, zCoord + zOffset)) {
										ItemStack ladderStack = extractLadderFromDispenser(slot);
										if (ladderStack != null && ladderStack.stackSize > 0) {
											if (setLadder(ladderStack, xCoord + xOffset, yCoord + vertDir, zCoord, direction)) {
												did_work = true;
												FMLLog.info("[" + References.MOD_NAME + "] did work!");
												break;
											}
										}	
									}	
								}
							}
						}
						
						setMode(getMode() + 1); // This round of Ladder placement finished, go to next mode.
					}
						
				}
				FMLLog.info("[" + References.MOD_NAME + "] mode is [" + mode + "], tick [" + ticks + "], work [" + did_work + "]");
				if (mode > 2) {
					if (did_work) { // If ladder placement is done but there's more work to do, reset to mode 1.
						ticks = 0;
						mode = 1;
					} else if (!worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) { // Else if block is unpowered - turn machine off and start retracting ladders.
						FMLLog.info("[" + References.MOD_NAME + "] machine off");
						ticks = 0;
						setActiveState(false);
					}
				}
			} else { // Retract ladders.
				if (ticks == 10) {
					//Make all calculations on where we are trying to place our ladder
					int[] offsets = this.calcOffsets();
					int xOffset = offsets[0];
					// Don't have a ladder, and thus don't know the y-direction
					int zOffset = offsets[2];
					
					ticks = 0;
					boolean finished = true;
					
					int x = xCoord + xOffset;
					int y = yCoord;
					int z = zCoord + zOffset;
					
					if (canRemoveLadder(x, y, z)) {
						this.removeLadder(x, y, z);
						finished = false;
					}
					
					if (canRemoveLadder(x, y - 1, z)) {
						this.removeLadder(x, y - 1, z);
						finished = false;
					}
					if (canRemoveLadder(x, y + 1, z)) {
						this.removeLadder(x, y + 1, z);
						finished = false;
					}
					if (finished) {
						mode = 0;
						working = false;
					}
				}
			}
			if (ticks > 10) {
				ticks = 0;
			}
		}
	}

	private boolean canRemoveLadder(int x, int y, int z) {

		Block block = worldObj.getBlock(x, y, z);
		return block == LLadders.blockRopeLadder || block == LLadders.blockSturdyLadder || block == LLadders.blockVineLadder;
	}

	private void removeLadder(int x, int y, int z) {

		Block block = worldObj.getBlock(x, y, z);
		int metadata = worldObj.getBlockMetadata(x, y, z);
		if (block != LLadders.blockRopeLadder && block != LLadders.blockSturdyLadder && block != LLadders.blockVineLadder) {
			return;
		}
		else if (worldObj.getBlock(x, y - 1, z) == LLadders.blockRopeLadder || worldObj.getBlock(x, y - 1, z) == LLadders.blockVineLadder) { // We want to retract from bottom and up.
			removeLadder(x, y - 1, z);
		}
		else if (worldObj.getBlock(x, y + 1, z) == LLadders.blockSturdyLadder) { // Or from the top down if sturdy ladders.
			removeLadder(x, y + 1, z);
		}
		else {
			worldObj.setBlockToAir(x, y, z);
			worldObj.removeTileEntity(x, y, z);
			ItemStack itemstack = new ItemStack(block, 1, metadata & 12);
			if (!this.insertLadderToDispenser(itemstack)) {
				dropBlockAsItem(x, y, z, itemstack);
			}
		}
	}
	
	private boolean canSetLadder(BlockGenericLadder ladder, int x, int y, int z) {

		if (y >= worldObj.getHeight() - 1 || y < 0)
			return false; // Make sure that we're not trying to place ladders out of the world.
		
		Block block = worldObj.getBlock(x, y, z);
		
		if (ladder == block) {
			FMLLog.info("[" + References.MOD_NAME + "] Found a ladder trying new coordinate [" + x + ", " + (y + ladder.getDirection()) + ", " + z + "]");
			return canSetLadder(ladder, x, y + ladder.getDirection(), z);
			
		} else if (!worldObj.isAirBlock(x, y, z)) {
			return false;
		}
		FMLLog.info("[" + References.MOD_NAME + "] Ladder can be placed at [" + x + ", " + y + ", " + z + "]");
		return true;
	}

	private boolean setLadder(ItemStack stack, int x, int y, int z, int meta) {
		if (stack != null && stack.stackSize > 0 && !worldObj.isRemote) {
			BlockGenericLadder block = null;
			try {
				block = (BlockGenericLadder)Block.getBlockFromItem(stack.getItem());
			} catch (Exception err) {
				FMLLog.warning("[" + References.MOD_NAME + "] not a type of ladder when trying to place ladders from dispenser!");
				return false;
			}
			
			if (worldObj.isAirBlock(x, y, z) && worldObj.getActualHeight() >= y) {
				worldObj.setBlock(x, y, z, block, meta, 2);
				return true;
			}
			
			return setLadder(stack, x, y + block.getDirection(), z, meta);
				
			
		}
		return false;
	}

	private int[] calcOffsets(BlockGenericLadder block) {
		ForgeDirection horDir = ForgeDirection.getOrientation(this.getForgeDirectionToInt(this.getFacingDirection())); //ForgeDirections enum seems to be a bit messed up. This fixes it so it will output correctly
		
		FMLLog.info("[" + References.MOD_NAME + "] I think I'm facing: [" + horDir.toString() + "]");
		
		int xOffset = 0;
		int zOffset = 0;
		
		int vertDir = 0;
		
		if (horDir == ForgeDirection.NORTH) {
			if (this.getPlacement() == OutputSide.LEFT)
				xOffset = -1;
			else if (this.getPlacement() == OutputSide.RIGHT)
				xOffset = 1;
			
		} else if (horDir == ForgeDirection.SOUTH) {
			if (this.getPlacement() == OutputSide.LEFT)
				xOffset = 1;
			else if (this.getPlacement() == OutputSide.RIGHT)
				xOffset = -1;
			
		} else if (horDir == ForgeDirection.WEST) {
			if (this.getPlacement() == OutputSide.LEFT)
				zOffset = -1;
			else if (this.getPlacement() == OutputSide.RIGHT)
				zOffset = 1;
		} else if (horDir == ForgeDirection.EAST) {
			if (this.getPlacement() == OutputSide.LEFT)
				zOffset = -1;
			else if (this.getPlacement() == OutputSide.RIGHT)
				zOffset = 1;
		} else { // this shouldn't happen. Means we have an invalid facing direction
			FMLLog.warning("[" + References.MOD_NAME + "] Got invalid facing direction!");
			return new int[] {0, 0, 0};
		}
		vertDir = block.getDirection();
		
		return new int[] {xOffset, vertDir, zOffset};
	}
	
	private int [] calcOffsets() {
		ForgeDirection horDir = this.getFacingDirection();
		int xOffset = 0;
		int zOffset = 0;
		
		int vertDir = 0;
		
		if (this.getPlacement() != OutputSide.UPDOWN) {
		
			if (horDir == ForgeDirection.NORTH) {
				if (this.getPlacement() == OutputSide.LEFT)
					xOffset = -1;
				else if (this.getPlacement() == OutputSide.RIGHT)
					xOffset = 1;
				
			} else if (horDir == ForgeDirection.SOUTH) {
				if (this.getPlacement() == OutputSide.LEFT)
					xOffset = 1;
				else if (this.getPlacement() == OutputSide.RIGHT)
					xOffset = -1;
				
			} else if (horDir == ForgeDirection.WEST) {
				if (this.getPlacement() == OutputSide.LEFT)
					zOffset = -1;
				else if (this.getPlacement() == OutputSide.RIGHT)
					zOffset = 1;
			} else if (horDir == ForgeDirection.EAST) {
				if (this.getPlacement() == OutputSide.LEFT)
					zOffset = -1;
				else if (this.getPlacement() == OutputSide.RIGHT)
					zOffset = 1;
			} else { // this shouldn't happen. Means we have an invalid facing direction
				FMLLog.warning("[" + References.MOD_NAME + "] Got invalid facing direction!");
				return new int[] {0, 0, 0};
			}
		}
		
		return new int[] {xOffset, vertDir, zOffset};
		
	}
	
	private boolean insertLadderToDispenser(ItemStack itemstack) {

		if (this.isItemStackInDispenser(itemstack)) {
			for (int i = 0; i < getSizeInventory() - 1; i++) {
				if (getStackInSlot(i) != null && getStackInSlot(i).isItemEqual(itemstack) && isItemValidForSlot(i, itemstack)
						&& getStackInSlot(i).stackSize < getInventoryStackLimit()) {
					ItemStack stack = new ItemStack(itemstack.getItem(), getStackInSlot(i).stackSize + 1, itemstack.getItemDamage());
					setInventorySlotContents(i, stack);
					return true;
				}
			}
		}
		else {
			for (int i = 0; i < getSizeInventory() - 1; i++) {
				if (getStackInSlot(i) == null) {
					setInventorySlotContents(i, itemstack);
					return true;
				}
			}
		}
		return false;
	}

	private boolean isItemStackInDispenser(ItemStack stack) {

		for (int i = 0; i < getSizeInventory() - 1; i++) {
			ItemStack tempStack = getStackInSlot(i);
			if (tempStack != null && tempStack.isItemEqual(stack) && tempStack.stackSize < getInventoryStackLimit())
				return true;
		}
		return false;
	}

	private ItemStack extractLadderFromDispenser(int slot) {

		ItemStack stack = this.getStackInSlot(slot);
		if ((stack != null)
				&& (stack.isItemEqual(new ItemStack(LLadders.blockRopeLadder)) || stack.isItemEqual(new ItemStack(LLadders.blockSturdyLadder)) || stack
						.isItemEqual(new ItemStack(LLadders.blockVineLadder)))) {
			return this.decrStackSize(slot, 1);
		}
		else {
			return null;
		}
	}

	private void dropBlockAsItem(int x, int y, int z, ItemStack itemstack) {

		if (!worldObj.isRemote && worldObj.getGameRules().getGameRuleBooleanValue("doTileDrops")) {
			float f = 0.7F;
			double d0 = (double) (worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d1 = (double) (worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d2 = (double) (worldObj.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			EntityItem ei = new EntityItem(worldObj, (double) x + d0, (double) y + d1, (double) z + d2, itemstack);
			ei.delayBeforeCanPickup = 10;
			worldObj.spawnEntityInWorld(ei);
		}
	}

	@Override
	public String getInventoryName() {

		return name;
	}

	@Override
	public boolean hasCustomInventoryName() {

		return false;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {

		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		}
		return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {

		ItemStack slotContent = this.getStackInSlot(i);
		if (slotContent == null)
			return true;
		if (itemstack != null && slotContent.isItemEqual(itemstack)) {
			if (itemstack.getTagCompound() == null && slotContent.getTagCompound() == null)
				return true;
			if (itemstack.getTagCompound() == null || slotContent.getTagCompound() == null)
				return false;
			return itemstack.getTagCompound().equals(slotContent.getTagCompound());
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {

		int[] slots = new int[getSizeInventory() - 1];
		return slots;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {

		if (itemstack != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {

		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {

		super.readFromNBT(par1NBTTagCompound);
		inventory = new ItemStack[getSizeInventory()];
		NBTTagList nbttaglist;
		placement = OutputSide.fromInt((int) par1NBTTagCompound.getByte("placement"));
		mode = (int) par1NBTTagCompound.getByte("mode");
		working = par1NBTTagCompound.getBoolean("working");
		this.placement = OutputSide.fromInt((int)par1NBTTagCompound.getByte("placement"));
		if (par1NBTTagCompound.hasKey("Items")) {
			nbttaglist = par1NBTTagCompound.getTagList("Items", 10);
			for (int i = 0; i < nbttaglist.tagCount(); i++) {
				NBTTagCompound slot = (NBTTagCompound) nbttaglist.getCompoundTagAt(i);
				int j = slot.getByte("Slot") & 0xff;
				if (j >= 0 && j < inventory.length) {
					inventory[j] = ItemStack.loadItemStackFromNBT(slot);
				}
			}
		}
		markDirty();
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {

		super.writeToNBT(par1NBTTagCompound);
		NBTTagList nbttaglist;
		par1NBTTagCompound.setByte("placement", (byte)this.placement.toInt());
		if (inventory.length > 0) {
			nbttaglist = new NBTTagList();
			par1NBTTagCompound.setByte("mode", (byte) mode);
			par1NBTTagCompound.setBoolean("working", working);
			for (int i = 0; i < inventory.length; i++) {
				if (inventory[i] != null && inventory[i].stackSize > 0) {
					NBTTagCompound slot = new NBTTagCompound();
					slot.setByte("Slot", (byte) i);
					inventory[i].writeToNBT(slot);
					nbttaglist.appendTag(slot);
				}
			}
			par1NBTTagCompound.setTag("Items", nbttaglist);
		}
	}
}
