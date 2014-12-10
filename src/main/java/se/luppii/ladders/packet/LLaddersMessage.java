/**
 * 
 */
package se.luppii.ladders.packet;

import se.luppii.ladders.enums.OutputSide;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

/**
 * @author Aiquen
 *
 */
public class LLaddersMessage implements IMessage {

	private int x, y, z;
	private OutputSide side;
	
	public LLaddersMessage() {
		// Generic do nothing constructor
	}
	
	public LLaddersMessage(int x, int y, int z, OutputSide side) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.side = side;
	}

	/* (non-Javadoc)
	 * @see cpw.mods.fml.common.network.simpleimpl.IMessage#fromBytes(io.netty.buffer.ByteBuf)
	 */
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = ByteBufUtils.readVarInt(buf, 5);
		this.y = ByteBufUtils.readVarInt(buf, 5);
		this.z = ByteBufUtils.readVarInt(buf, 5);
		this.side = OutputSide.fromInt(ByteBufUtils.readVarInt(buf, 5));
	}

	/* (non-Javadoc)
	 * @see cpw.mods.fml.common.network.simpleimpl.IMessage#toBytes(io.netty.buffer.ByteBuf)
	 */
	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, this.x, 5);
		ByteBufUtils.writeVarInt(buf, this.y, 5);
		ByteBufUtils.writeVarInt(buf, this.z, 5);
		ByteBufUtils.writeVarInt(buf, side.toInt(), 5);

	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getZ() {
		return this.z;
	}
	
	public OutputSide getSide() {
		return this.side;
	}

}
