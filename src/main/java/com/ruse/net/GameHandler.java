package com.ruse.net;

import com.ruse.GameServer;
import com.ruse.GameSettings;
import com.ruse.model.entity.character.player.Player;
import com.ruse.model.entity.character.player.PlayerLoading;
import com.ruse.net.login.LoginDetails;
import com.ruse.net.login.LoginResponses;
import com.ruse.net.packet.Packet;
import com.ruse.net.packet.PacketBuilder;
import com.ruse.net.security.ConnectionHandler;
import com.ruse.util.NameUtils;
import com.ruse.world.World;
import io.netty.channel.*;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ruse.net.login.LoginResponses.*;

@Sharable
public class GameHandler extends ChannelInboundHandlerAdapter {

	public static final AttributeKey<PlayerSession> SESSION_KEY = AttributeKey.valueOf("session");
	public static final ExecutorService fileIOExecutor = Executors.newFixedThreadPool(1);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		if(!(e.getCause() instanceof IOException)) {
			ctx.channel().close();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Attribute<PlayerSession> attribute = ctx.channel().attr(SESSION_KEY);
		PlayerSession session = attribute.getAndSet(null);

		if(session != null) {
			if(session.getState() != SessionState.LOGGED_OUT) {
				World.logoutQueue.offer(session.getPlayer());
			}
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		try {
			Attribute<PlayerSession> attribute = ctx.channel().attr(SESSION_KEY);
			PlayerSession session = attribute.get();

			switch (msg) {
				case Packet pkt -> session.handleIncomingMessage(pkt);
				case LoginDetails ld -> handleLogin(ctx, ld);
                default -> throw new IllegalStateException("Unexpected value: " + msg);
            }

		} finally {
			ReferenceCountUtil.release(msg);
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	public void handleLogin(ChannelHandlerContext ctx, LoginDetails ld) {
		if( World.getPlayerByName(ld.getUsername()) != null) {
			sendReturnCode(ctx.channel(), LoginResponses.LOGIN_ACCOUNT_ONLINE);
			return;
		}

		Channel channel = ctx.channel();
		PlayerSession playerSession = new PlayerSession(ctx.channel());
		Player player = new Player(playerSession);

		playerSession.setPlayer(player);

		player.setUsername(ld.getUsername())
				.setLongUsername(NameUtils.stringToLong(ld.getUsername()))
				.setPassword(ld.getPassword())
				.setHostAddress(ld.getHost())
				.setMac(ld.getMac())
				.setUUID(ld.getUUID());

		if(ld.getClientVersion() != GameSettings.GAME_VERSION || ld.getUid() != GameSettings.GAME_UID) {
			sendReturnCode(channel, OLD_CLIENT_VERSION);
			return;
		}

		if (World.getPlayers().isFull()) {
			sendReturnCode(channel, LOGIN_WORLD_FULL);
		}

		if(GameServer.isUpdating()) {
			sendReturnCode(channel, LOGIN_GAME_UPDATE);
		}

		if (!NameUtils.isValidName(player.getUsername())) {
			sendReturnCode(channel, LOGIN_INVALID_CREDENTIALS);
			return;
		}

		if(player.getUsername().startsWith(" ")) {
			sendReturnCode(channel, USERNAME_STARTS_WITH_SPACE);
			return;
		}

		if(player.getUsername().endsWith(" ")) {
			sendReturnCode(channel, USERNAME_ENDS_WITH_SPACE);
			return;
		}

		if(World.getPlayerByName(player.getUsername()) != null) {
			sendReturnCode(channel, LOGIN_ACCOUNT_ONLINE);
			return;
		}

		CompletableFuture.supplyAsync(() -> PlayerLoading.getResult(player), fileIOExecutor)
				.thenAcceptAsync((response) -> {
			if(World.getPlayerByName(ld.getUsername()) != null || World.loginQueue.contains(player)) {
				sendReturnCode(ctx.channel(), LoginResponses.LOGIN_ACCOUNT_ONLINE);
				return;
			}

			final boolean newAccount = response == LoginResponses.NEW_ACCOUNT;
			if(newAccount) {
				player.setNewPlayer(true);
				response = LoginResponses.LOGIN_SUCCESSFUL;
			}

			int hostHandlerResponse = ConnectionHandler.getResponse(player, ld);
			if(hostHandlerResponse != LOGIN_SUCCESSFUL) {
				sendReturnCode(ctx.channel(),hostHandlerResponse);
			}

			if (response == LoginResponses.LOGIN_SUCCESSFUL) {
				channel.write(new PacketBuilder().put((byte)2).put((byte)player.getRights().ordinal()).put((byte)0).toPacket());
				channel.attr(SESSION_KEY).set(playerSession);
				World.loginQueue.offer(player);
			} else {
				sendReturnCode(channel, response);
			}
		});
	}

	public static void sendReturnCode(final Channel channel, final int code) {
		ChannelFuture future = channel.writeAndFlush(new PacketBuilder().put((byte) code).toPacket());
		future.addListener((ChannelFutureListener) f -> {
			if (f.isSuccess()) {
				f.channel().close();
			}
		});
	}
}
