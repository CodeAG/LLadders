package se.luppii.ladders.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class SlotBridgeBuilder extends Slot {

	public SlotBridgeBuilder(IInventory par1iInventory, int par2, int par3, int par4) {

		super(par1iInventory, par2, par3, par4);
	}

	@Override
	public int getSlotStackLimit() {

		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {

		return par1ItemStack.getItem() instanceof ItemBlock;
	}
}
