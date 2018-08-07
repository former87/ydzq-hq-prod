package com.ydzq.hq.prod.netty.codec;

import java.nio.ByteOrder;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ydzq.hq.prod.global.AppConstrants;
import com.ydzq.hq.prod.netty.protocol.WrapProtocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 解码器
 * 
 * @author f.w
 *
 */
public class WrapDecoder extends ByteToMessageDecoder {
	private Logger log = LoggerFactory.getLogger(WrapDecoder.class);

	@SuppressWarnings("deprecation")
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		try {
			log.info("[WrapLimit:" + in.readableBytes() + "-" + in.toString() + "]");

			if (in.readableBytes() < AppConstrants.SOCKET.WRAPHEADLENTH)
				return;

			in = in.order(ByteOrder.LITTLE_ENDIAN);
			while (in.readableBytes() >= AppConstrants.SOCKET.WRAPHEADLENTH) {
				try {
					in.markReaderIndex();

					int flag = in.readInt();
					int type = in.readInt();
					int len = in.readInt();

					WrapProtocol wrap = new WrapProtocol().setFlag(flag).setType(type).setLen(len);

					if (in.readableBytes() < len) {
						in.resetReaderIndex();
						wrap = null;
						break;
					}
					if (flag != 999 || type == 0) {
						wrap = null;
						break;
					}

					if (!(wrap.getType() + "").equals(AppConstrants.SOCKET.HEART)) {
				
						ByteBuf bytebuf = in.slice(in.readerIndex(), len);
						in.skipBytes(len);
						bytebuf.retain();

						wrap.setBytebuf(bytebuf);
					}

					out.add(wrap);

				} catch (Exception e) {
					for (StackTraceElement ste : e.getStackTrace()) {
						log.info(ste.toString());
					}
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}