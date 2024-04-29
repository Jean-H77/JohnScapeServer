package com.ruse.net;

import com.ruse.GameSettings;
import com.ruse.net.login.LoginDecoder;
import com.ruse.net.login.LoginEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;


public class PipelineInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel socketChannel) {
		ChannelPipeline pipeline = socketChannel.pipeline();
		pipeline.addLast("encoder", new LoginEncoder());
		pipeline.addLast("decoder", new LoginDecoder());
	//	pipeline.addLast("timeout", new IdleStateHandler(GameSettings.IDLE_TIME, 0, 0));
		pipeline.addLast("handler", new GameHandler());
	}
}
