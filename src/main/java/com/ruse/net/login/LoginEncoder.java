package com.ruse.net.login;

import com.ruse.net.packet.Packet;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;


public class LoginEncoder extends MessageToMessageEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) {
		out.add(((Packet) msg).getBuffer());
	}
}
