package com.ruse.net.login;

import com.ruse.GameSettings;
import com.ruse.model.entity.character.player.Player;
import com.ruse.net.PlayerSession;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.net.packet.codec.PacketDecoder;
import com.ruse.net.packet.codec.PacketEncoder;
import com.ruse.net.security.IsaacRandom;
import com.ruse.util.Misc;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.netty.buffer.Unpooled.wrappedBuffer;


public final class LoginDecoder extends ByteToMessageDecoder {
	
	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;
	private int state = CONNECTED;
	private long seed;

	private static final SecureRandom RANDOM = new SecureRandom();

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
					System.out.println("Invalid login request: " + request);
					channel.close();
					return;
				}
				seed = RANDOM.nextLong();
				channel.write(new PacketBuilder().put((byte) 0).putLong(seed).toPacket());
				state = LOGGING_IN;
				return;
			case LOGGING_IN:
				if (byteBuf.readableBytes() < 2) {
					System.out.println("no readable bytes");
					return;
				}
				int loginType = byteBuf.readByte();
				if (loginType != 16 && loginType != 18) {
					System.out.println("Invalid login type: " + loginType);
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
					System.out.println("Invalid magic id! magicId: "+magicId);
					channel.close();
					return;
				}
				int clientVersion = byteBuf.readShort();
				int memory =  byteBuf.readByte();
				if (memory != 0 && memory != 1) {
					System.out.println("Unhandled memory byte value");
					channel.close();
					return;
				}
			/*int[] archiveCrcs = new int[9];
			for (int i = 0; i < 9; i++) {
				archiveCrcs[i] = buffer.readInt();
			}*/
				int length = byteBuf.readUnsignedByte();
				/**
				 * Our RSA components.
				 */
				ByteBuf rsaBuffer = byteBuf.readBytes(length);
				byte[] bytes = new byte[rsaBuffer.readableBytes()];
				rsaBuffer.readBytes(bytes);
				BigInteger bigInteger = new BigInteger(bytes);
				bigInteger = bigInteger.modPow(GameSettings.RSA_EXPONENT, GameSettings.RSA_MODULUS);
				rsaBuffer = wrappedBuffer(bigInteger.toByteArray());

				int securityId = rsaBuffer.readByte();
				if(securityId != 10) {
					System.out.println("securityId id is not 10. It is "+securityId);
					channel.close();
					return;
				}
				long clientSeed = rsaBuffer.readLong();
				long seedReceived = rsaBuffer.readLong();
				if (seedReceived != seed) {
					System.out.println("Unhandled seed read: [seed, seedReceived] : [" + seed + ", " + seedReceived + "");
					channel.close();
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
				//String serial = Misc.readString(rsaBuffer);
				if (username.length() > 12 || password.length() > 20) {
					System.out.println("Username or password length too long");
					return;
				}
				username = Misc.formatText(username.toLowerCase());
				channel.pipeline().replace("encoder", "encoder", new PacketEncoder(new IsaacRandom(seed)));
				channel.pipeline().replace("decoder", "decoder", new PacketDecoder(decodingRandom));
				out.add(new LoginDetailsMessage(username, password, ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress(), mac, uuid, clientVersion, uid, channel));
		}
	}
}
