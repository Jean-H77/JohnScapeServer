package com.ruse.net.packet.codec;

import com.ruse.net.packet.Packet;
import com.ruse.net.packet.Packet.PacketType;
import com.ruse.net.security.IsaacRandom;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static io.netty.buffer.Unpooled.buffer;


public final class PacketEncoder extends MessageToByteEncoder<Packet> {

	public PacketEncoder(IsaacRandom encoder) {
		this.encoder = encoder;
	}

	private final IsaacRandom encoder;

	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf out) throws Exception {
		PacketType packetType = packet.getType();
		int headerLength = 1;
		int packetLength = packet.getSize();
		if (packet.getOpcode() == -1) {
			out.writeBytes(packet.getBuffer());
			return;
		}
		if (packetType == PacketType.BYTE) {
			headerLength += 1;
			if (packetLength >= 256) {
				throw new Exception("Packet length is too long for a sized packet.");
			}
		} else if (packetType == PacketType.SHORT) {
			headerLength += 2;
			if (packetLength >= 65536) {
				throw new Exception("Packet length is too long for a short packet.");
			}
		}
		ByteBuf buffer = buffer(headerLength + packetLength);
		buffer.writeByte((packet.getOpcode() + encoder.nextInt()) & 0xFF);
		if (packetType == PacketType.BYTE) {
			buffer.writeByte(packetLength);
		} else if (packetType == PacketType.SHORT) {
			buffer.writeShort(packetLength);
		}
		buffer.writeBytes(packet.getBuffer());
		out.writeBytes(buffer);
	}
}
