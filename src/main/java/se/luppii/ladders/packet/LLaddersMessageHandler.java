/**
 * 
 */
package se.luppii.ladders.packet;

import net.minecraft.world.World;
import se.luppii.ladders.enums.OutputSide;
import se.luppii.ladders.lib.References;
import se.luppii.ladders.tile.TileEntityLadderDispenser;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

/**
 * @author Aiquen
 *
 */
public class LLaddersMessageHandler implements IMessageHandler<LLaddersMessage, IMessage> {

	/**
	 * 
	 */
	public LLaddersMessageHandler() {

		// Default do nothing constructor
	}

	/**
	 * Handle a received message
	 * 
	 * This method handles messages received from the channel we listen to. It
	 * will then handle the message and send a reply
	 */
	@Override
	public IMessage onMessage(LLaddersMessage message, MessageContext ctx) {

		if (ctx.side.equals(Side.SERVER)) {
			int x, y, z;
			OutputSide side;
			// use short, easy to type local variables 
			x = message.getX();
			y = message.getY();
			z = message.getZ();
			side = message.getSide();
			// declare world. We are dependent on a player using the dispenser object
			World world;
			try {
				world = ctx.getServerHandler().playerEntity.worldObj;
			}
			catch (Exception err) {
				FMLLog.warning("[" + References.MOD_NAME + "] couldn't get placing player needed to get world object");
				return null;
			}
			try {
				TileEntityLadderDispenser ladderDispenserEntity = (TileEntityLadderDispenser) world.getTileEntity(x, y, z);
				ladderDispenserEntity.setPlacement(side);
				ladderDispenserEntity.updateEntity(); // this is to make sure we immediately start working with ladders on the newly chosen side
				world.markBlockForUpdate(x, y, z); // if we change output side, update icons by marking the block for update
			}
			catch (Exception err) {
				FMLLog.warning("[" + References.MOD_NAME + "] Didn't receive a LadderDispenser TileEntity. Nothing to do");
			}
		}
		return null;
	}
}
