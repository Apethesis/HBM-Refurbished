package com.hbm.packet;

import com.hbm.tileentity.IBufPacketReceiver;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class BufPacket implements net.minecraftforge.fml.common.network.simpleimpl.IMessage {

	BlockPos pos;
	IBufPacketReceiver rec;
	ByteBuf buf;
	
	public BufPacket() { }

	public BufPacket(BlockPos pos, IBufPacketReceiver rec) {
		this.pos = pos;
		this.rec = rec;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(),buf.readInt(),buf.readInt());
		this.buf = buf;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
		buf.writeInt(pos.getY());
		buf.writeInt(pos.getZ());
		this.rec.serialize(buf);
	}
    // it annoys me how it requires the full ass thing
	public static class Handler implements net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler<BufPacket, net.minecraftforge.fml.common.network.simpleimpl.IMessage> {

        @Override
        public net.minecraftforge.fml.common.network.simpleimpl.IMessage onMessage(BufPacket m, net.minecraftforge.fml.common.network.simpleimpl.MessageContext ctx) {
			if(Minecraft.getMinecraft().world == null)
				return null;
			
			TileEntity te = Minecraft.getMinecraft().world.getTileEntity(m.pos);
			
			if(te instanceof IBufPacketReceiver) {
				((IBufPacketReceiver) te).deserialize(m.buf);
			}
			
			return null;
        } // this so janky
		
		
	}
}