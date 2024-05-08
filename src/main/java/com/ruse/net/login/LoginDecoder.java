package com.ruse.net.login;

import com.ruse.GameSettings;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.net.packet.codec.PacketDecoder;
import com.ruse.net.packet.codec.PacketEncoder;
import com.ruse.net.security.IsaacRandom;
import com.ruse.util.Misc;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;

import static io.netty.buffer.Unpooled.wrappedBuffer;


public final class LoginDecoder extends ByteToMessageDecoder {
	
	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;
	private static final SecureRandom RANDOM = new SecureRandom();

	private int state = CONNECTED;
	private long seed;

	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
		Channel channel = channelHandlerContext.channel();

		if(!channel.isActive()) {
			return;
		}

		switch (state) {
			case CONNECTED:
				if (byteBuf.readableBytes() < 1) {
					return;
				}
				int request = byteBuf.readUnsignedByte();
				if (request != 14) {
					channel.close();
					return;
				}
				seed = RANDOM.nextLong();
				channel.writeAndFlush(new PacketBuilder().put((byte) 0).putLong(seed).toPacket());
				state = LOGGING_IN;
				return;
			case LOGGING_IN:
				if (byteBuf.readableBytes() < 2) {
					return;
				}
				int loginType = byteBuf.readByte();
				if (loginType != 16 && loginType != 18) {
					channel.close();
					return;
				}
				int blockLength = byteBuf.readByte() & 0xff;
				if (byteBuf.readableBytes() < blockLength) {
					channel.close();
					return;
				}
				int magicId = byteBuf.readUnsignedByte();
				if(magicId != 0xFF) {
					channel.close();
					return;
				}
				int clientVersion = byteBuf.readShort();
				int memory =  byteBuf.readByte();
				if (memory != 0 && memory != 1) {
					channel.close();
					return;
				}
				/**
				 * Our RSA components.
				 */
				int length = byteBuf.readUnsignedByte();
				byte[] rsaBytes = new byte[length];
				byteBuf.readBytes(rsaBytes);

				BigInteger bigInteger = new BigInteger(rsaBytes);
				bigInteger = bigInteger.modPow(GameSettings.RSA_EXPONENT, GameSettings.RSA_MODULUS);
				ByteBuf rsaBuffer = Unpooled.wrappedBuffer(bigInteger.toByteArray());

				int securityId = rsaBuffer.readByte();
				if(securityId != 10) {
					channel.close();
					rsaBuffer.release();
					return;
				}
				long clientSeed = rsaBuffer.readLong();
				long seedReceived = rsaBuffer.readLong();
				if (seedReceived != seed) {
					channel.close();
					rsaBuffer.release();
					return;
				}
				int[] seed = new int[4];
				seed[0] = (int) (clientSeed >> 32);
				seed[1] = (int) clientSeed;
				seed[2] = (int) (this.seed >> 32);
				seed[3] = (int) this.seed;
				IsaacRandom decodingRandom = new IsaacRandom(seed);
				for (int i = 0; i < seed.length; i++) {
					seed[i] += 50;
				}
				int uid = rsaBuffer.readInt();
				String username = Misc.readString(rsaBuffer);
				String password = Misc.readString(rsaBuffer);
				String mac = Misc.readString(rsaBuffer);
				String uuid = Misc.readString(rsaBuffer);
				if (username.length() > 12 || password.length() > 20) {
					channel.close();
					rsaBuffer.release();
					return;
				}
				username = Misc.formatText(username.toLowerCase());
				channel.pipeline().replace("encoder", "encoder", new PacketEncoder(new IsaacRandom(seed)));
				channel.pipeline().replace("decoder", "decoder", new PacketDecoder(decodingRandom));
				out.add(new LoginDetails(username, password, ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress(), mac, uuid, clientVersion, uid, channel));
				rsaBuffer.release();
		}
	}
}
