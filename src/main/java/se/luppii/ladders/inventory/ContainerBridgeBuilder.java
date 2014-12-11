package se.luppii.ladders.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import se.luppii.ladders.tile.TileEntityBridgeBuilder;

public class ContainerBridgeBuilder extends Container {

	protected TileEntityBridgeBuilder tileEntity;

	public ContainerBridgeBuilder(IInventory playerInventory, TileEntityBridgeBuilder te) {

		this.tileEntity = te;
		this.addSlotToContainer(new SlotBridgeBuilder(this.tileEntity, 0, 71, 35));
		this.addSlotToContainer(new SlotBlockTexture(this.tileEntity, 1, 135, 35));
		bindPlayerInventory(playerInventory);
	}

	private void bindPlayerInventory(IInventory playerInventory) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {

		return this.tileEntity.isUseableByPlayer(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {

		ItemStack stack = null;
		Slot slotObject = (Slot) inventorySlots.get(slot);
		int invSize = this.tileEntity.getSizeInventory();
		// Checks if the item can be stacked and is not null
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();
			// If the item is in the tileEntity - merge into player inventory
			if (slot < invSize) {
				if (!this.mergeItemStack(stackInSlot, invSize, inventorySlots.size(), true)) {
					return null;
				}
			}
			// Places item in tileEntity - the item is in the players inventory
			else if (!this.mergeItemStack(stackInSlot, 0, invSize, false)) {
				return null;
			}
			slotObject.onSlotChange(stackInSlot, stack);
			if (stackInSlot.stackSize == 0) {
				slotObject.putStack(null);
			}
			else {
				slotObject.onSlotChanged();
			}
			if (stackInSlot.stackSize == stack.stackSize) {
				return null;
			}
			slotObject.onPickupFromSlot(player, stackInSlot);
		}
		return stack;
	}
}
