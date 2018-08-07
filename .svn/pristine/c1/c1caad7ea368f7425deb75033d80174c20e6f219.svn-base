package com.ydzq.hq.prod.netty.client.channel;

 
import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.codec.WrapDecoder;
import com.ydzq.hq.prod.netty.handler.HqClientHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

public class HqChannelInitializer extends ChannelInitializer<SocketChannel> {

	private HqClientHandler hqClientHandler;

	public HqChannelInitializer(HqClientHandler hqClientHandler) {
		this.hqClientHandler = hqClientHandler;
	}

	@Override
	public void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline p = channel.pipeline();
		p.addLast(new IdleStateHandler(AppConstrants.SOCKET.HEARTTIMROUT, 0, 0));
		// p.addLast(new LoggingHandler(LogLevel.WARN));
		p.addLast(new WrapDecoder());
		p.addLast(hqClientHandler);
	}
}