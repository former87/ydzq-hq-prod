package com.ydzq.hq.prod.netty.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 处理读写空闲
 * 
 * @author f.w
 *
 */
public abstract class HqChannelInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {

	private final Logger log = LoggerFactory.getLogger(HqChannelInboundHandler.class);

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// IdleStateHandler 所产生的 IdleStateEvent 的处理逻辑.
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			switch (e.state()) {
			case READER_IDLE:
				handleReaderIdle(ctx);
				break;
			case WRITER_IDLE:
				handleWriterIdle(ctx);
				break;
			case ALL_IDLE:
				handleAllIdle(ctx);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.info("---" + ctx.channel().remoteAddress() + " is active---");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.info("---" + ctx.channel().remoteAddress() + " is inactive---");
	}

	protected void handleReaderIdle(ChannelHandlerContext ctx) {
		log.info("---READER_IDLE---");
	}

	protected void handleWriterIdle(ChannelHandlerContext ctx) {
		log.info("---WRITER_IDLE---");
	}

	protected void handleAllIdle(ChannelHandlerContext ctx) {
		log.info("---ALL_IDLE---");
	}
}