/**
 * 
 */
package se.luppii.ladders.packet;

import net.minecraft.tileentity.TileEntity;
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
	 * This method handles messages received from the channel we listen to. It will then handle the message and send a reply
	 */
	@Override
	public IMessage onMessage(LLaddersMessage message, MessageContext ctx) {
		if (ctx.side.equals(Side.SERVER)) {
			int x, y, z;
			OutputSide side;
			
			x = message.getX();
			y = message.getY();
			z = message.getZ();
			side = message.getSide();
			
			World world = ctx.getServerHandler().playerEntity.worldObj;
			try {
				TileEntityLadderDispenser ladderDispenserEntity = (TileEntityLadderDispenser)world.getTileEntity(x, y, z);
				ladderDispenserEntity.setPlacement(side);
				
				ladderDispenserEntity.updateEntity(); // this is to make sure we immediately start working with ladders on the newly chosen side
				world.markBlockForUpdate(x, y, z); // if we change output side, update icons by marking the block for update

			} catch (Exception err) {
				FMLLog.warning("[" + References.MOD_NAME + "] Didn't receive a LadderDispenser TileEntity. Nothing to do");
			}
			
		}
		
		return null;
	}

}
