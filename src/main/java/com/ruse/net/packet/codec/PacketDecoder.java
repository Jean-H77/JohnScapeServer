package com.ruse.net.packet.codec;

import com.ruse.net.packet.Packet;
import com.ruse.net.packet.Packet.PacketType;
import com.ruse.net.packet.PacketConstants;
import com.ruse.net.security.IsaacRandom;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import static io.netty.buffer.Unpooled.buffer;

public class PacketDecoder extends ByteToMessageDecoder {
	
	private final IsaacRandom random;
	
	private int opcode = -1;
	private int size = -1;

	public PacketDecoder(IsaacRandom random) {
		this.random = random;
	}

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) {
		if (opcode == -1) {
			if (byteBuf.isReadable()) {
				int encryptedOpcode = byteBuf.readUnsignedByte();
				opcode = (encryptedOpcode - random.nextInt()) & 0xFF;
				size = PacketConstants.MESSAGE_SIZES[opcode];
			} else {
				return;
			}
		}
		if (size == -1) {
			if (byteBuf.isReadable()) {
				size = byteBuf.readUnsignedByte();
			} else {
				return;
			}
		}
		if (byteBuf.readableBytes() >= size) {
			final byte[] data = new byte[size];
			byteBuf.readBytes(data);
			final ByteBuf payload = buffer(size);
			payload.writeBytes(data);
			try {
				out.add(new Packet(opcode, PacketType.FIXED, payload));
			} finally {
				opcode = -1;
				size = -1;
			}
		}
	}
}
