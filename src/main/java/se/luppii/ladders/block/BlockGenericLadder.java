package se.luppii.ladders.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * An abstract class collecting together all similarities for the concrete
 * ladder classes.
 * <p>
 * This class is used to collect all the same functionality for different
 * concrete classes of ladders. Also implements some abstract classes to make
 * sure that they are present in each concrete class.
 * 
 * @author Gix
 * @author Aiquen
 * 
 */
public abstract class BlockGenericLadder extends Block implements ITileEntityProvider {

	private IIcon blockIIcon;

	protected boolean enableLeftClick;

	public static int renderID;
	
	protected int direction;

	// Basic constructor
	public BlockGenericLadder(boolean par1Boolean) {

		super(Material.circuits);
		enableLeftClick = par1Boolean;
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	public BlockGenericLadder(Material par1Material) {

		super(par1Material);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	/* Abstract methods go here */
	/**
	 * Sets the "edges" of the ladder.
	 * <p>
	 * This sets where the black lines in the bounding box are drawn and how
	 * "big" the ladder is in a block.
	 * 
	 * @param par1
	 *            Facing direction of the ladder
	 */
	public abstract void updateLadderBounds(int par1);

	/**
	 * Check if block can be placed
	 * <p>
	 * Checks if a block can be placed at specific coordinates
	 * 
	 * @param par1World
	 *            The current minecraft world to work on
	 * @param par2
	 *            X coordinate to test
	 * @param par3
	 *            Y coordinate to test
	 * @param par4
	 *            Z coordinate to test
	 * 
	 * @return True if block can be placed, false otherwise
	 */
	public abstract boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4);

	public abstract int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int par9);

	public abstract void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack);

	public abstract void onBlockClicked(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer);

	/**
	 * Returns the {@link TileEntity} for this block
	 * 
	 * @see TileEntity
	 * @param par1
	 *            Not used
	 * @return TileEntity of current block
	 */
	public abstract TileEntity getBlockEntity(int par1);

	/**
	 * Tries to create a new {@link TileEntity} for this block
	 * 
	 * @see TileEntity
	 * @param world
	 *            The current minecraft world we operate on
	 * @return TileEntity if on is created
	 */
	public abstract TileEntity createTileEntity(World world, int metadata);
	
	/**
	 * Check to see if the working mode of a LadderDispenser is the same mode the Ladder is supposed to be placed.
	 * 
	 * This is a helper method for the LadderDispenser that checks if the mode of the LadderDispenser is conforming to the type of Ladder boolean true is returned
	 * @param mode the mode the LadderDispenser is currently in. Sent as an integer
	 * @return boolean true if the mode is the expected mode for this type of ladder
	 */
	public abstract boolean isModeConforming(int mode);
	
	public abstract TileEntity createNewTileEntity(World var1, int var2);

	/**
	 * Extension of {@link canPlaceBlockAt}
	 * <p>
	 * Checks if a ladder of the specific concrete type can be placed at
	 * specific coordinates
	 * 
	 * @param world
	 *            The current minecraft world we operate on
	 * @param x
	 *            X coordinate in the world
	 * @param y
	 *            Y coordinate in the world
	 * @param z
	 *            Z coordinate in the world
	 * @param meta
	 *            metadata of the block (in this case the facing of the ladder
	 * @return True if ladder can be placed, false otherwise
	 */
	protected abstract boolean canSetLadder(World world, int x, int y, int z, int meta);

	/**
	 * Places a ladder in the world
	 * 
	 * @param world
	 *            The current minecraft world we operate on
	 * @param x
	 *            X coordinate in the world
	 * @param y
	 *            Y coordinate in the world
	 * @param z
	 *            Z coordinate in the world
	 * @param meta
	 *            metadata of the block (in this case the facing of the ladder
	 * @param player
	 *            player that does the placeing
	 * @return True if ladder was placed successfully, false otherwise
	 */
	protected abstract boolean setLadder(World world, int x, int y, int z, int meta, EntityPlayer player);

	/* Concrete methods go here */
	public int getDirection() {
		return this.direction;
	}
	
	public boolean isBlockNormalCube(World world, int i, int j, int k) {

		return false;
	}

	@Override
	public boolean isOpaqueCube() {

		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {

		return false;
	}

	@Override
	public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {

		return false;
	}

	public static void setRenderID(int id) {

		renderID = id;
	}

	public int getRenderType() {

		return renderID;
	}

	public int quantityDropped(Random par1Random) {

		return 1;
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {

		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IIconRegister) {

		blockIIcon = par1IIconRegister.registerIcon("lladders:" + getUnlocalizedName());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {

		return blockIIcon;
	}

	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {

		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
	}

	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4) {

		this.updateLadderBounds(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
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
	 * @param par2
	 *            X coordinate in the world
	 * @param par3
	 *            Y coordinate in the world
	 * @param par4
	 *            Z coordinate in the world
	 * @return A BoundingBox aligned to the axis of the world
	 */
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4) {

		this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
		// Fix a collisionbox that is tiny bit bigger than the block the ladder is placed on. This to enable climbing from both sides
		int direction = par1World.getBlockMetadata(par2, par3, par4) & 3;
		double f = 1.0D / 18.0D;
		float factor = 1.0F;
		switch (direction) {
			case 0:
				// SOUTH
				AxisAlignedBB box = super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
				box = box.expand(0, 0, -f);
				return box;
			case 1:
				// WEST
				box = super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
				box = box.expand(-f, 0, 0);
				return box;
			case 2:
				// NORTH
				box = super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
				box = box.expand(0, 0, -f);
				return box;
			case 3:
				// EAST
				box = super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
				box = box.expand(-f, 0, 0);
				return box;
		}
		return AxisAlignedBB.getBoundingBox(par2 + f, par3, par4 + f, par2 + factor - f, par3 + factor - f, par4 + factor - f);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {

		// Check to see if it is a player as we are only interested in  manipulating them
		if (entity instanceof EntityPlayer) {
			// Cast to EntityPlayer to make it more strict
			EntityPlayer player = (EntityPlayer) entity;
			int direction = world.getBlockMetadata(x, y, z) & 3;
			boolean player_close = false;
			// Look at placement position and calculate if the player is close enough to ladder to "climb"
			switch (direction) {
			// SOUTH
				case 0:
					if (player.posZ - z >= 0.8D)
						player_close = true;
					break;
				// WEST
				case 1:
					if (player.posX - x <= 0.2D)
						player_close = true;
					break;
				// NORTH
				case 2:
					if (player.posZ - z <= 0.2D)
						player_close = true;
					break;
				// EAST
				case 3:
					if (player.posX - x >= 0.8D)
						player_close = true;
					break;
				default:
					player_close = false;
			}
			if (player.posY - 1.0D >= y && player_close) {
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
				player.setVelocity(0.0D, 0.08D, 0.0D); // Found this by experimenting. An upward velocity of 0.08 negates gravity fall
			}
		}
	}
}
