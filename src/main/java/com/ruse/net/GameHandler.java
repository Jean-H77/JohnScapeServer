package com.ruse.net;

import com.ruse.model.entity.character.player.Player;
import com.ruse.net.login.AuthenticationService;
import com.ruse.net.login.LoginDetailsMessage;
import com.ruse.net.login.LogoutDetailsMessage;
import com.ruse.net.packet.Packet;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import java.io.IOException;

@Sharable
public class GameHandler extends ChannelInboundHandlerAdapter {

	public static final AttributeKey<PlayerSession> SESSION_KEY = AttributeKey.valueOf("session");

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
		if(!(e.getCause() instanceof IOException)) {
			//logger.log(Level.WARNING, "Exception occured for channel: " + ctx.channel() + ", closing...", e.getCause());
			ctx.channel().close();
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Attribute<PlayerSession> attribute = ctx.channel().attr(SESSION_KEY);
		PlayerSession session = attribute.get();
		if(session != null) {
			if(session.getState() != SessionState.LOGGED_OUT) {
				AuthenticationService.addToQueue(session.getPlayer());
			}
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg != null) {

			Attribute<PlayerSession> attribute = ctx.channel().attr(SESSION_KEY);
			PlayerSession session = attribute.get();

			if(session != null && msg instanceof Packet packet) {
				session.handleIncomingMessage(packet);
			}

			if(msg instanceof LoginDetailsMessage login) {
				AuthenticationService.addToQueue(login);
			}
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}
}
