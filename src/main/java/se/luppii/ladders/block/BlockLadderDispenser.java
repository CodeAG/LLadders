package se.luppii.ladders.block;

import java.util.List;

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
import se.luppii.ladders.LLadders;
import se.luppii.ladders.enums.OutputSide;
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
		this.icons = new IIcon[names.length];
		this.setCreativeTab(CreativeTabs.tabRedstone);
	}

	@Override
	public int damageDropped(int par1) {

		return par1;
	}

	@Override
	public int getLightValue(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {

		TileEntity te = par1IBlockAccess.getTileEntity(par2, par3, par4);
		if (te != null && te instanceof TileEntityLadderDispenser) {
			// Special slot 4 - For custom texture
			ItemStack itemstack = ((TileEntityLadderDispenser) te).getStackInSlot(4);
			if (itemstack != null) {
				Block block = Block.getBlockFromItem(itemstack.getItem());
				if (block != null)
					return block.getLightValue();
			}
		}
		return 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		if (te != null && te instanceof TileEntityLadderDispenser) {
			// Special slot 4 - For custom texture
			ItemStack itemstack = ((TileEntityLadderDispenser) te).getStackInSlot(4);
			if (itemstack != null) {
				Block block = Block.getBlockFromItem(itemstack.getItem());
				if (block != null && block.renderAsNormalBlock())
					return block.getIcon(par5, itemstack.getItemDamage());
			}
			par5 = ((TileEntityLadderDispenser) te).getRotatedSide(par5);
			// Do stuff with this for bottom texture flip
			int ordinal = ((TileEntityLadderDispenser) te).getFacingDirection().ordinal();
			return this.getIcon(par5, meta, ordinal, ((TileEntityLadderDispenser) te).getPlacement());
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
			((TileEntityLadderDispenser) te).setFacingDirection(determineDirection(par1World, par2, par3, par4, par5EntityLivingBase));
		}
	}

	public static int determineDirection(World world, int x, int y, int z, EntityLivingBase entity) {

		int dir = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return dir == 0 ? 5 : (dir == 1 ? 3 : (dir == 2 ? 4 : (dir == 3 ? 2 : 5)));
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {

		TileEntityLadderDispenser te = (TileEntityLadderDispenser) par1World.getTileEntity(par2, par3, par4);
		if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
			if (!te.getActiveState()) {
				te.setMode(1); // Block is powered. Place ladders.
				te.setActiveState(true);
				te.setIsWorking(true);
			}
		}
	}

	public TileEntity getBlockEntity(int par1) {

		switch (par1) {
			case 0:
				return new TileEntityLadderDispenser();
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
				FMLLog.warning("[" + References.MOD_NAME + "] Invalid metadata for " + getUnlocalizedName() + ". Metadata received was " + par2 + ".",
						new Object[0]);
				return icons[0];
		}
	}

	/**
	 * Returns an texture for a side of the block depending on side and facing 
	 * 
	 * par1 = 0 -> bottom
	 * par1 = 1 -> top
	 * par1 = 2 -> left
	 * par1 = 3 -> right
	 * par1 = 4 -> front (facing player)
	 * par1 = 5 -> back (facing away from player)
	 * 
	 * @param par1 side of the block
	 * @param par2 metadata of block
	 * @param par3 facing of the block
	 * @param par4 side ladders are being outputed on
	 * @return Icon (texture) of the side of the block
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2, int par3, OutputSide par4) {
		
		// This bit is when ladders is outputed to the side and turns the textures appropriately 
		if (par4 != OutputSide.UPDOWN) {
			switch (par1) {
				case 0: case 1:
					return this.icons[4];
				case 2:
					if (par4 == OutputSide.LEFT)
						return this.icons[1];
					else
						return this.icons[4];
				case 3:
					if (par4 == OutputSide.RIGHT)
						return this.icons[0];
					else
						return this.icons[4];
			}
		}
		
		// this bit is when ladders are outputed up and down and need to turn the texture to face correctly 
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
				FMLLog.warning("[" + References.MOD_NAME + "] Invalid metadata for " + getUnlocalizedName() + ". Metadata received was " + par2 + ".",
						new Object[0]);
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
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Gets collision bounds for a block from the pool
	 * <p>
	 * This gets the collision box to use for a block. This is not necessarily
	 * the same as the normal drawn bounding box that is the black wireframe
	 * around a block.
	 * 
	 * @see AxisAlignedBB
	 * @param par1World
	 *            The current minecraft world we operate on
	 * @param x
	 *            X coordinate in the world
	 * @param y
	 *            Y coordinate in the world
	 * @param z
	 *            Z coordinate in the world
	 * @return A BoundingBox aligned to the axis of the world (i.e. follows the
	 *         grid of blocks)
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

		// Check to see if it is a player as we are only interested in manipulating them
		if (entity instanceof EntityPlayer && Config.canClimbOnDispenser.getBoolean(true)) {
			// Cast to EntityPlayer to make it more strict
			EntityPlayer player = (EntityPlayer) entity;
			if (player.posY - 1.0D >= y) {
				player.moveForward = 0.0F;
				// If player is moving down, move slowly down.
				if (player.motionY < -0.15D)
					player.motionY = -0.15D;
				// check if we want to climb up
				if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)
						|| GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack)
						|| GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft)
						|| GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight)) {
					if (player.motionY < 0.2D)
						player.motionY = 0.2D;
				}
			}
			// Check if we are sneaking and want to climb up, or just want to sneak "stand still" on the ladder
			if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)
					&& (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindForward)
							|| GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindBack)
							|| GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindLeft) || GameSettings
								.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindRight))) {
				player.motionY = 0.2D;
			}
			else if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
				player.setVelocity(0.0D, 0.08D, 0.0D); // Found this by experimenting. An upward velocity of 0.08 negates gravity fall.
			}
		}
	}

	private void dropItems(TileEntity te) {

		if (te instanceof IInventory) {
			World world = te.getWorldObj();
			IInventory inventory = ((IInventory) te);
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack itemstack = inventory.getStackInSlot(i);
				if (itemstack == null)
					continue;
				while (itemstack.stackSize > 0) {
					int j = world.rand.nextInt(21) + 10;
					if (j > itemstack.stackSize)
						j = itemstack.stackSize;
					itemstack.stackSize -= j;
					float xOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float yOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					float zOffset = world.rand.nextFloat() * 0.8F + 0.1F;
					EntityItem ei = new EntityItem(world, te.xCoord + xOffset, te.yCoord + yOffset, te.zCoord + zOffset, new ItemStack(itemstack.getItem(), j,
							itemstack.getItemDamage()));
					float factor = 0.05F;
					ei.motionX = (float) world.rand.nextGaussian() * factor;
					ei.motionY = (float) world.rand.nextGaussian() * factor + 0.2F;
					ei.motionZ = (float) world.rand.nextGaussian() * factor;
					if (itemstack.hasTagCompound()) {
						ei.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
					}
					world.spawnEntityInWorld(ei);
				}
			}
		}
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
			FMLLog.severe("[" + References.MOD_NAME + "] Unable to create TileEntity instance from Ladder Dispenser.");
			return null;
		}
		catch (InstantiationException ex) {
			FMLLog.severe("[" + References.MOD_NAME + "] Unable to create TileEntity instance from Ladder Dispenser.");
			return null;
		}
	}
}
