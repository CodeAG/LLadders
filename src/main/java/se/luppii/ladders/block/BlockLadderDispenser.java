package se.luppii.ladders.block;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import se.luppii.ladders.LLadders;
import se.luppii.ladders.lib.Config;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.tile.TileEntityLadderDispenser;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLadderDispenser extends BlockContainer {

	private static String[] names = new String[] { "bottom_2", "bottom_3", "bottom_4", "bottom_5", "side", "front" };
	private IIcon[] icons;
	
	public BlockLadderDispenser() {
		super(Material.iron);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeMetal);
		this.setBlockName("lladders.block.ladderdispenser");
		this.icons = new IIcon[names.length]; // Get new icons for the new sides
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public int damageDropped(int par1) {
		return par1;
	}
	
	@Override
	public int getLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {
		
		return 0;
	}
	
	@SuppressWarnings({ "unchecked","rawtypes" })
	@Override
	public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List) {
		for (int i = 0; i < 1; i++) {
			par3List.add(new ItemStack(par1Item, 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IIconRegister) {
		
		for (int i = 0; i < icons.length; i++) {
			icons[i] = par1IIconRegister.registerIcon("lladders:" + getUnlocalizedName() + "_" + names[i]);
		}
	}
	
	@Override
	public IIcon getIcon(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5) {
        
		TileEntity te = par1IBlockAccess.getTileEntity(par2, par3, par4);
		int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
		
		if (te instanceof TileEntityLadderDispenser) {
			
			// Special slot 4
			ItemStack itemstack = ((TileEntityLadderDispenser)te).getStackInSlot(4);
			if (itemstack != null) {
				
				Block block = Block.getBlockFromItem(itemstack.getItem());
				if (block != null && block.renderAsNormalBlock())
					return block.getIcon(par5, itemstack.getItemDamage());
			}
			
			par5 = ((TileEntityLadderDispenser)te).getRotatedSide(par5);
			
			// Do stuff with this for bottom texture flip
			int ordinal = ((TileEntityLadderDispenser)te).getFacingDirection().ordinal();
			
			return this.getIcon(par5, meta, ordinal);
		}
		return this.getIcon(par5, meta);
	}
	
	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		
		par5EntityPlayer.openGui(LLadders.instance, 0, par1World, par2, par3, par4);
		return true;
	}
	
	public void onBlockAdded(World par1World, int par2, int par3, int par4) {
		
		par1World.scheduleBlockUpdate(par2, par3, par4, this, 10);
	}
	
	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {

		if (par5EntityLivingBase == null) {
			return;
		}
		
		TileEntity te = par1World.getTileEntity(par2, par3, par4);
		if (par6ItemStack.getTagCompound() != null) {
			
			par6ItemStack.getTagCompound().setInteger("x", par2);
			par6ItemStack.getTagCompound().setInteger("y", par3);
			par6ItemStack.getTagCompound().setInteger("z", par4);
			te.readFromNBT(par6ItemStack.getTagCompound());
		}
		
		if (te instanceof TileEntityLadderDispenser) {
			
			int direction = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
			switch (direction) {
				case 0:
					((TileEntityLadderDispenser)te).setFacingDirection(5);
					break;
				case 1:
					((TileEntityLadderDispenser)te).setFacingDirection(3);
					break;
				case 2:
					((TileEntityLadderDispenser)te).setFacingDirection(4);
					break;
				case 3:
					((TileEntityLadderDispenser)te).setFacingDirection(2);
					break;
			}
		}
	}
	
	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {
		
		par1World.scheduleBlockUpdate(par2, par3, par4, this, 10);
	}
	
	public TileEntity getBlockEntity(int par1) {
		
		switch(par1) {
			case 0: return new TileEntityLadderDispenser();
		}
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {
		
		switch (par2) {
			case 0:
				if (par1 == 0 || par1 == 1) {
					
					par1 = 2;
				}
				else {
					par1 += 3;
					if (par1 == 6) {
						
						par1 = 5;
					}
					else if (par1 > 4) {
						
						par1 = 4;
					}
				}
				return icons[par1];
			default:
				FMLLog.warning("[" + References.MOD_NAME + "] Invalid metadata for " + getUnlocalizedName() + ". Metadata received was " + par2 + ".", new Object[0]);
				return icons[0];
		}
	}
	
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2, int par3) {
		
		switch (par2) {
		
			case 0:
				if (par3 > 1) {
					if (par1 == 0 || par1 == 1) {
						
						par1 = par3 - 2;
					}
					else {
						
						par1 += 3;
						if (par1 == 7) {
							
							par1 = 5;
						}
						else if (par1 > 4) {
							
							par1 = 4;
						}
					}
					return icons[par1];					
				}
				else {
					
					return getIcon(par1, par2);
				}
			default:
				FMLLog.warning("[" + References.MOD_NAME + "] Invalid metadata for " + getUnlocalizedName() + ". Metadata received was " + par2 + ".", new Object[0]);
				return icons[0];
		}
	}
	
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6) {
		
		TileEntity te = par1World.getTileEntity(par2, par3, par4);
		dropItems(te);
		super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
	}
	
	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		if (Config.canClimbOnDispenser.getBoolean(true)) {
			FMLLog.info("Climeable Dispensers activated");
			return true;
		} else {
			FMLLog.info("Climable Dispenser deactivated");
			return false;
		}
		
	}
	
	/**
	 * Gets collision bounds for a block from the pool
	 * <p>
	 * This gets the collision box to use for a block. This is not necessarily the same as the normal drawn bounding box that is the black wireframe around a block.
	 * 
	 * @see AxisAlignedBB
	 * @param par1World The current minecraft world we operate on
	 * @param x X coordinate in the world
	 * @param y Y coordinate in the world
	 * @param z Z coordinate in the world
	 * @return A BoundingBox aligned to the axis of the world (i.e. follows the grid of blocks)
	 */
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {		
		double border = 1.0D / 18.0D;
	    return AxisAlignedBB.getBoundingBox(x + border, y, z + border, x + 1 - border, y + 1, z + 1 - border);
	}
	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int i, int j, int k) {
		return AxisAlignedBB.getBoundingBox(i, j, k, i + 1, j + 1, k + 1);
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
    	//Check to see if it is a player as we are only interested in manipulating them
    	if (entity instanceof EntityPlayer) {
    		//Cast to EntityPlayer to make it more strict
    		EntityPlayer player = (EntityPlayer) entity;
    		    		
    		if (player.posY - 1.0D >= y) {
				player.moveForward = 0.0F;
				
				//If player is moving down, move slowly down.
				if (player.motionY < -0.15D)
					player.motionY = -0.15D;
				
				
				//check if we want to climbe up
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward) || GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack) || GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft) || GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight)) {
					if (player.motionY < 0.2D)
						player.motionY = 0.2D;
				}		
    		}
    		
    		//Check if we are sneaking and want to climb up, or just want to sneak "stand still" on the ladder
    		if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak) && (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward) || GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack) || GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft) || GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight))) {
    			player.motionY = 0.2D;
    		} else if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {	
    			player.setVelocity(0.0D, 0.08D, 0.0D); //Found this by experimenting. An upward velocity of 0.08 negates gravity fall
    		}
    	}
    }
	
	private void dropItems(TileEntity te) {
		
		if (te instanceof IInventory) {
			World world = te.getWorldObj();
			IInventory inventory = ((IInventory)te);
			
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				
				ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack == null) continue;
				while (itemstack.stackSize > 0) {
					
					int j = world.rand.nextInt(21) + 10;
					if (j > itemstack.stackSize) j = itemstack.stackSize;
					itemstack.stackSize -= j;
					
					float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					
					EntityItem ei = new EntityItem(world, te.xCoord + xOffset, te.yCoord + yOffset, te.zCoord + zOffset,
											new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));
					
					float factor = 0.05F;
					ei.motionX = (float)world.rand.nextGaussian() * factor;
					ei.motionY = (float)world.rand.nextGaussian() * factor + 0.2F;
					ei.motionZ = (float)world.rand.nextGaussian() * factor;
					
					if (itemstack.hasTagCompound()) {
						ei.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
					}
					
					world.spawnEntityInWorld(ei);
					
				}
			}
		}
	}
	
	@Override
	public void updateTick(World par1World, int par2, int par3, int par4, Random par5Random) {
		
		super.updateTick(par1World, par2, par3, par4, par5Random);
		operate(par1World, par2, par3, par4);
	}
	
	private void operate(World world, int x, int y, int z) {
		
		Block[] ladders = { LLadders.blockRopeLadder, LLadders.blockSturdyLadder, LLadders.blockVineLadder };	// List of ladders to try placing.
		boolean done = false,
				finished = false;
		
		TileEntityLadderDispenser te = (TileEntityLadderDispenser)world.getTileEntity(x, y, z);
		
		// Since direction is saved inside the tile entity we have to fetch it and convert back to numbers.
		int direction = getForgeDirectionToInt(te.getFacingDirection());
		
		if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
			
			// If block is receiving redstone signal, start the engine.
			te.setActiveState(true);
			
			// Set running mode.
			te.setMode(1);
		}
		
		if (!te.getActiveState()) {
			return;
		}
		
		if (te.getMode() == 1) {	// Place ladders.
			for (int l = 0; l < ladders.length; l++) {
				for (int i = 0; i < te.getSizeInventory() - 1; i++) {
					ItemStack stack = te.getStackInSlot(i);
					if (stack != null) { 
						Block ladder = Block.getBlockFromItem(stack.getItem());
						if (ladder == ladders[l]) {
							int dir = 0;	// Direction in Y-axis we want to use. 1 is up, -1 is down. 0 is no movement, which means something is wrong.
							boolean can_place = false;	// Flag to see if it is possible to put a ladder at the specific place.
							
							if (ladder == LLadders.blockRopeLadder || ladder == LLadders.blockVineLadder) {
								dir = -1;
								
							} else if (ladder == LLadders.blockSturdyLadder) {
								dir = 1;
							}
							
							can_place = this.canSetLadder(world, x, y + dir, z, direction);
							if (can_place && dir != 0) {	// We have a ladder, and can place it down or up.
								ItemStack ladderStack = this.extractLadderFromDispenser(te, i);
								if (ladderStack != null) {
									if (this.setLadder(world, ladderStack, x, y + dir, z, direction)) {
										done = true;
										finished = false;
										break;
									} else {
										te.setMode(2);
										finished = true;
									}
								} else {
									te.setMode(2);
									finished = true;
								}	
							} else {
								te.setMode(2);
								finished = true;
							}
						}
					}
				} 	// end for
			} 	// end for
			if (!done) {
				
				te.setMode(2);
				done = true;
				finished = true;
			}

		} else if (te.getMode() == 2) {	// Retract ladders.
			finished = true;
			if (this.canRemoveLadder(world, x, y - 1, z, direction)) {
				this.removeLadder(world, te, x, y - 1, z, direction);
				done = true;
				finished = false;
			}
			if (this.canRemoveLadder(world, x, y + 1, z, direction)) {
				this.removeLadder(world, te, x, y + 1, z, direction);
				done = true;
				finished = false;
			}
			if (finished) {
				te.setMode(0);	// Cycle done. Reset mode.
			}
		}
		else {
			
			// Turn off machine.
			te.setActiveState(false);
		}
		
		if (done && !finished) {
			world.scheduleBlockUpdate(x, y, z, this, te.getMode() == 1 ? 6 : 10);
		}
	}
	
	private boolean canRemoveLadder(World world, int x, int y, int z, int meta) {
		
		Block block = world.getBlock(x, y, z);
		return block == LLadders.blockRopeLadder || block == LLadders.blockSturdyLadder || block == LLadders.blockVineLadder;
	}
	
	private boolean canSetLadder(World world, int x, int y, int z, int meta) {
		Block ladder = world.getBlock(x, y, z);
		
		if (ladder == LLadders.blockRopeLadder || ladder == LLadders.blockSturdyLadder || ladder == LLadders.blockVineLadder) {	// We want to check if there is ladders below as well.
			int dir;
			if (ladder == LLadders.blockRopeLadder || ladder == LLadders.blockVineLadder)
				dir = -1;
			else if (ladder == LLadders.blockSturdyLadder)
				dir = 1;
			else
				return false; //Safety measure, should never happen
			
			return canSetLadder(world, x, y + dir, z, meta);
		}
		else if (!world.isAirBlock(x, y, z)) {
			return false;
		}
		return true;
	}
	
	private int getForgeDirectionToInt(ForgeDirection dir) {
		
		ForgeDirection[] directions = { ForgeDirection.DOWN, ForgeDirection.UP, ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST };
		for (int i = 0; i < directions.length; i++) {
			
			if (directions[i] == dir) {
				
				switch(i) {
					case 2: return 3;
					case 3: return 1;
					case 4: return 2;
					case 5: return 0;
				}
			}
		}
		return 0;
	}
	
	private boolean insertLadderToDispenser(TileEntityLadderDispenser te, ItemStack itemstack) {
		if (this.isItemStackInDispenser(itemstack, te)) {
			for (int i = 0; i < te.getSizeInventory() - 1; i++) {
				if (te.getStackInSlot(i) != null && te.getStackInSlot(i).isItemEqual(itemstack) && te.isItemValidForSlot(i, itemstack) && te.getStackInSlot(i).stackSize < te.getInventoryStackLimit()) {
					ItemStack stack = new ItemStack(itemstack.getItem(), te.getStackInSlot(i).stackSize + 1, itemstack.getItemDamage());
					te.setInventorySlotContents(i, stack);
					return true;
				}
			}
		} else {
			for (int i = 0; i < te.getSizeInventory() - 1; i++) {
				if (te.getStackInSlot(i) == null) {
					te.setInventorySlotContents(i, itemstack);
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isItemStackInDispenser(ItemStack stack, TileEntityLadderDispenser te) {
		for (int i = 0; i < te.getSizeInventory() - 1; i++) {
			ItemStack tempStack = te.getStackInSlot(i);
			if (tempStack != null && tempStack.isItemEqual(stack) && tempStack.stackSize < te.getInventoryStackLimit())
				return true;
		}
		
		return false;
	}
	
	private ItemStack extractLadderFromDispenser(TileEntityLadderDispenser te, int slot) {
		if ((te.getStackInSlot(slot) != null) && (te.getStackInSlot(slot).isItemEqual(new ItemStack(LLadders.blockRopeLadder)) || te.getStackInSlot(slot).isItemEqual(new ItemStack(LLadders.blockSturdyLadder)) || te.getStackInSlot(slot).isItemEqual(new ItemStack(LLadders.blockVineLadder)))) {
			return te.decrStackSize(slot, 1);
		} else {
			return null;
		}
	}
	
	private void removeLadder(World world, TileEntityLadderDispenser te, int x, int y, int z, int meta) {
		
		Block block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		if (block != LLadders.blockRopeLadder && block != LLadders.blockSturdyLadder && block != LLadders.blockVineLadder) {
			return;
		}
		else if (world.getBlock(x, y - 1, z) == LLadders.blockRopeLadder || world.getBlock(x, y - 1, z) == LLadders.blockVineLadder) {	// We want to retract from bottom and up.
			removeLadder(world, te, x, y - 1, z, meta);
		} 
		else if (world.getBlock(x,  y + 1, z) == LLadders.blockSturdyLadder) { // Or from the top down if sturdy ladders
			removeLadder(world, te, x, y + 1, z, meta);
		}
		else {
			world.setBlockToAir(x, y, z);
			world.removeTileEntity(x, y, z);
			ItemStack itemstack = new ItemStack(block, 1, metadata & 12);
			
			if (!insertLadderToDispenser(te, itemstack)) {
				dropBlockAsItem(world, x, y, z, itemstack);
			}
		}
	}
	
	private boolean setLadder(World world, ItemStack stack, int x, int y, int z, int meta) {
		if (stack != null) {
			Block block = Block.getBlockFromItem(stack.getItem());
	
			if (world.isAirBlock(x, y, z) && world.getActualHeight() >= y) {	
				world.setBlock(x, y, z, block, meta, 2);
				return true;
				
			}
			if (block == LLadders.blockRopeLadder || block == LLadders.blockVineLadder) {
				return setLadder(world, stack, x, y - 1, z, meta);
				
			} else if (block == LLadders.blockSturdyLadder) {
				return setLadder(world, stack, x, y + 1, z, meta);
				
			}
		}
		return false;
	}
	
	private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4, int par5) {
		
		return par5 != 0 && par1World.getIndirectPowerOutput(par2, par3 - 1, par4, 0) ? true :
			(par5 != 1 && par1World.getIndirectPowerOutput(par2, par3 + 1, par4, 1) ? true :
				(par5 != 2 && par1World.getIndirectPowerOutput(par2, par3, par4 - 1, 2) ? true :
					(par5 != 3 && par1World.getIndirectPowerOutput(par2, par3, par4 + 1, 3) ? true :
						(par5 != 5 && par1World.getIndirectPowerOutput(par2 + 1, par3, par4, 5) ? true :
							(par5 != 4 && par1World.getIndirectPowerOutput(par2 - 1, par3, par4, 4) ? true :
								(par1World.getIndirectPowerOutput(par2, par3, par4, 0) ? true :
									(par1World.getIndirectPowerOutput(par2, par3 + 2, par4, 1) ? true :
										(par1World.getIndirectPowerOutput(par2, par3 + 1, par4 - 1, 2) ? true :
											(par1World.getIndirectPowerOutput(par2, par3 + 1, par4 + 1, 3) ? true :
												(par1World.getIndirectPowerOutput(par2 - 1, par3 + 1, par4, 4) ? true :
													par1World.getIndirectPowerOutput(par2 + 1, par3 + 1, par4, 5)))))))))));
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return null;
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		
		try {
			TileEntityLadderDispenser te = TileEntityLadderDispenser.class.newInstance();
			return te;
		}
		catch (IllegalAccessException ex) {
			FMLLog.severe("[" + References.MOD_NAME + "] Unable to create TileEntity instance from Ladder Dispenser.", new Object[0]);
			return null;
		}
		catch (InstantiationException ex) {
			FMLLog.severe("[" + References.MOD_NAME + "] Unable to create TileEntity instance from Ladder Dispenser.", new Object[0]);
			return null;
		}
	}
}
