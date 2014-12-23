package se.luppii.ladders.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import se.luppii.ladders.LLadders;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.tile.TileEntityBridgeBuilder;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBridgeBuilder extends BlockContainer {

	private static String[] names = new String[] { "side", "front", "back" };

	private IIcon[] icons;

	public BlockBridgeBuilder() {

		super(Material.iron);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeMetal);
		this.setBlockName("lladders.block.bridgebuilder");
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
		if (te != null && te instanceof TileEntityBridgeBuilder) {
			// Special slot 1 - For custom texture
			ItemStack itemstack = ((TileEntityBridgeBuilder) te).getStackInSlot(1);
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
		if (te != null && te instanceof TileEntityBridgeBuilder) {
			// Special slot 1 - For custom texture
			ItemStack itemstack = ((TileEntityBridgeBuilder) te).getStackInSlot(1);
			if (itemstack != null) {
				Block block = Block.getBlockFromItem(itemstack.getItem());
				if (block != null && block.renderAsNormalBlock())
					return block.getIcon(par5, itemstack.getItemDamage());
			}
			par5 = ((TileEntityBridgeBuilder) te).getRotatedSide(par5);
			int ordinal = ((TileEntityBridgeBuilder) te).getFacingDirection().ordinal();
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
		if (te instanceof TileEntityBridgeBuilder) {
			((TileEntityBridgeBuilder) te).setFacingDirection(determineDirection(par1World, par2, par3, par4, par5EntityLivingBase));
		}
	}

	public static int determineDirection(World world, int x, int y, int z, EntityLivingBase entity) {

		if (MathHelper.abs((float) entity.posX - (float) x) < 2.0F && MathHelper.abs((float) entity.posZ - (float) z) < 2.0F) {
			double d0 = entity.posY + 1.8D - entity.yOffset;
			if (d0 - (double) y > 2.0D)
				return 1;
			if ((double) y - d0 > 0.0D)
				return 0;
		}
		int dir = MathHelper.floor_double((double) (entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
		return dir == 0 ? 2 : (dir == 1 ? 5 : (dir == 2 ? 3 : (dir == 3 ? 4 : 3)));
	}

	public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5) {

		TileEntityBridgeBuilder te = (TileEntityBridgeBuilder) par1World.getTileEntity(par2, par3, par4);
		if (par1World.isBlockIndirectlyGettingPowered(par2, par3, par4)) {
			if (!te.getActiveState()) {
				te.setMode(1); // Block is powered. Set first working mode.
				te.setActiveState(true);
				te.setIsWorking(true);
			}
		}
	}

	public TileEntity getBlockEntity(int par1) {

		switch (par1) {
			case 0:
				return new TileEntityBridgeBuilder();
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2) {

		switch (par2) {
			case 0:
				if (par1 == 2) // Front-Back flip in inventory.
					par1 = 2;
				else if (par1 == 3)
					par1 = 1; // Front-Back flip in inventory.
				else
					par1 = 0; // Side
				return icons[par1];
			default:
				FMLLog.warning("[" + References.MOD_NAME + "] Invalid metadata for " + getUnlocalizedName() + ". Metadata received was " + par2 + ".",
						new Object[0]);
				return icons[0];
		}
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int par1, int par2, int ordinal) {

		switch (par2) {
			case 0:
				if (par1 == 2)
					par1 = 1; // Front
				else if (par1 == 3)
					par1 = 2; // Back
				else
					par1 = 0; // Side
				return icons[par1];
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
			TileEntityBridgeBuilder te = TileEntityBridgeBuilder.class.newInstance();
			return te;
		}
		catch (IllegalAccessException ex) {
			FMLLog.severe("[" + References.MOD_NAME + "] Unable to create TileEntity instance from Bridge Builder.");
			return null;
		}
		catch (InstantiationException ex) {
			FMLLog.severe("[" + References.MOD_NAME + "] Unable to create TileEntity instance from Bridge Builder.");
			return null;
		}
	}
}
